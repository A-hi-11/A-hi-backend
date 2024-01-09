package com.example.Ahi.service.PromptService;

import com.example.Ahi.domain.*;
import com.example.Ahi.dto.requestDto.PromptListRequestDto;
import com.example.Ahi.dto.responseDto.CommentListResponse;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import com.example.Ahi.entity.Message;
import com.example.Ahi.repository.*;
import io.jsonwebtoken.lang.Assert;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptRetrievalService {
    private final PromptRepository promptRepository;
    private final ChatExampleRepository chatExampleRepository;
    private final TagsRepository tagsRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PreferenceRepository preferenceRepository;

    public List<PromptListResponseDto> getPromptList(PromptListRequestDto promptListRequestDto) {
        promptListRequestDto.validate();
        String sort = promptListRequestDto.getSort();

        Sort sortObj = determineSortOrder(sort);
        Specification<Prompt> spec = buildSpecification(promptListRequestDto);

        List<Prompt> promptList = promptRepository.findAll(spec, sortObj);
        List<PromptListResponseDto> promptListResponseDtos = getPromptListResponseDtos(promptList);

        return maybeSortByLikes(promptListResponseDtos, sort);
    }

    public PromptResponseDto getPrompt(long prompt_id, String member_id){
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        Assert.notNull(prompt);
        Member promptMember = memberRepository.findById(prompt.getMember().getMember_id()).orElse(null);
        PromptResponseDto promptResponseDto = prompt.toPromptResponseDto(promptMember.getNickname());

        promptResponseDto.setTags(getTagsForPrompt(prompt));
        promptResponseDto.setExample(getChatExamplesForPrompt(prompt));
        promptResponseDto.setComments(getCommentList(commentRepository.findByPromptId(prompt), member_id));
        promptResponseDto.setLikes(getPreferenceCountForPrompt(prompt, "like"));
        promptResponseDto.setDislikes(getPreferenceCountForPrompt(prompt, "dislike"));
        promptResponseDto.setMyPrompt(isMyPrompt(prompt, member_id));

        return promptResponseDto;
    }

    public ArrayList<PromptListResponseDto> getMyList(String member_id) {
        Member member = memberRepository.findById(member_id).orElse(null);
        Assert.notNull(member);
        List<Prompt> promptList = promptRepository.findByMember(member);
        return (ArrayList<PromptListResponseDto>) getPromptListResponseDtos(promptList);
    }

    private List<PromptListResponseDto> getPromptListResponseDtos(List<Prompt> promptList) {
        List<PromptListResponseDto> responseList = new ArrayList<>();

        for (Prompt prompt : promptList) {
            List<Preference> likes = preferenceRepository.findByPromptAndStatus(prompt, "like");
            List<Preference> dislikes = preferenceRepository.findByPromptAndStatus(prompt, "dislike");
            List<Comment> commentList = commentRepository.findByPromptId(prompt);
            PromptListResponseDto responseDto = prompt
                    .toPromptListResponseDto(commentList.size(), likes.size(), dislikes.size());
            responseDto.setImage("");
            if(prompt.getMediaType().equals("image")){
                List<ChatExample> chatExampleList = chatExampleRepository.findByPrompt(prompt);
                if(!chatExampleList.isEmpty()){
                    if(!chatExampleList.get(0).isQuestion()){
                        responseDto.setImage(chatExampleList.get(0).getMessage());
                    }else{
                        responseDto.setImage(chatExampleList.get(1).getMessage());
                    }
                }
            }
            responseList.add(responseDto);
        }

        return responseList;
    }
    private Sort determineSortOrder(String sort) {
        if ("time".equals(sort)) {
            return Sort.by(Sort.Direction.DESC, "updateTime");
        }
        return Sort.unsorted();
    }

    private Specification<Prompt> buildSpecification(PromptListRequestDto request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(request.getSearch())) {
                predicates.add(cb.like(root.get("title"), "%" + request.getSearch() + "%"));
            }
            if (!StringUtils.isEmpty(request.getCategory())) {
                predicates.add(cb.equal(root.get("category"), request.getCategory()));
            }
            if (!StringUtils.isEmpty(request.getMediaType())) {
                predicates.add(cb.equal(root.get("mediaType"), request.getMediaType()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<PromptListResponseDto> maybeSortByLikes(List<PromptListResponseDto> dtos, String sort) {
        if ("likes".equals(sort)) {
            return dtos.stream()
                    .sorted(Comparator.comparingLong(PromptListResponseDto::getLikes).reversed())
                    .toList();
        }
        return dtos;
    }

    private Set<String> getTagsForPrompt(Prompt prompt) {
        return tagsRepository.findByPrompt(prompt).stream()
                .map(Tags::getContent)
                .collect(Collectors.toSet());
    }
    // 채팅 예시 조회 로직
    private ArrayList<ArrayList<Message>> getChatExamplesForPrompt(Prompt prompt) {
        // example 추가
        ArrayList<ChatExample> chatExampleArrayList =
                (ArrayList<ChatExample>) chatExampleRepository
                        .findByPrompt(prompt);
        ArrayList<Message> list1 = new ArrayList<>();
        ArrayList<Message> list2 = new ArrayList<>();
        for(ChatExample chatExample: chatExampleArrayList){
            if(chatExample.getChat_order() == 0){
                list1.add(chatExample.toMessage());
            }
            else{
                list2.add(chatExample.toMessage());
            }
        }
        ArrayList<ArrayList<Message>> chatExamples = new ArrayList<>();
        chatExamples.add(list1);
        if(!list2.isEmpty()){
            chatExamples.add(list2);
        }
        return chatExamples;
    }

    // 선호도(좋아요/싫어요) 수 조회 로직
    private int getPreferenceCountForPrompt(Prompt prompt, String status) {
        return preferenceRepository.findByPromptAndStatus(prompt, status).size();
    }

    // 내 프롬프트 여부 확인 로직
    private boolean isMyPrompt(Prompt prompt, String member_id) {
        Member member = memberRepository.findById(member_id).orElse(null);
        return prompt.getMember() == member;
    }
    
    // 댓글 리스트 조회 로직
    private ArrayList<CommentListResponse> getCommentList(List<Comment> list, String member_id){
        ArrayList<CommentListResponse> result = new ArrayList<>();
        for(Comment comment: list){
            Member member = memberRepository.findById(comment.getMember_id().getMember_id()).orElse(null);
            Assert.notNull(member);
            result.add(comment.toCommentListResponse(member, Objects.equals(member.getMember_id(), member_id)));
        }
        return result;
    }
}
