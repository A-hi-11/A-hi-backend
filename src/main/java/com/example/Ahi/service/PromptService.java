package com.example.Ahi.service;

import com.example.Ahi.domain.*;
import com.example.Ahi.dto.requestDto.PreferenceRequestDto;
import com.example.Ahi.dto.requestDto.PromptListRequestDto;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptRepository promptRepository;
    private final ChatExampleRepository chatExampleRepository;
    private final TagsRepository tagsRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PreferenceRepository preferenceRepository;
    private final ConfigInfoRepository configInfoRepository;

    public String create(PromptRequestDto promptRequestDto){
        promptRequestDto.validate();
        Member member = memberRepository.findById(promptRequestDto.getMember_id()).orElse(null);
        Assert.notNull(member);
        // domain prompt 생성
        LocalDateTime now = LocalDateTime.now();
        Prompt prompt = promptRequestDto.toPrompt(member, now);

        // prompt 저장 코드
        promptRepository.save(prompt);
        if(promptRequestDto.getMediaType().equals("text")){
            configInfoRepository.save(promptRequestDto.getGptConfigInfo().toConfigInfo(prompt));
        }

        // tag 저장 코드
        saveTags(promptRequestDto.getTags(), prompt);

        // example 저장 코드
        saveChatExample(promptRequestDto.getExample(), prompt, member);

        return "create successfully!";
    }

    public List<PromptListResponseDto> getPromptList(PromptListRequestDto promptListRequestDto) {
        promptListRequestDto.validate();
        String sort = promptListRequestDto.getSort();
        String search = promptListRequestDto.getSearch();
        String category = promptListRequestDto.getCategory();
        String mediaType = promptListRequestDto.getMediaType();

        Sort sortObj;
        if ("time".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "updateTime");
        } else {
            sortObj = Sort.unsorted();
        }

        Specification<Prompt> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(search)) {
                Predicate titleLike = cb.like(root.get("title"), "%" + search + "%");
                predicates.add(titleLike);
            }
            if (!StringUtils.isEmpty(category)) {
                Predicate categoryEqual = cb.equal(root.get("category"), category);
                predicates.add(categoryEqual);
            }
            if (!StringUtils.isEmpty(mediaType)) {
                Predicate mediaTypeEqual = cb.equal(root.get("mediaType"), mediaType);
                predicates.add(mediaTypeEqual);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Prompt> promptList = promptRepository.findAll(spec, sortObj);
        List<PromptListResponseDto> promptListResponseDtos = getPromptListResponseDtos(promptList);
        if("likes".equals(sort)){
            promptListResponseDtos = promptListResponseDtos.stream()
                    .sorted(Comparator.comparingLong(PromptListResponseDto::getLikes).reversed())
                    .toList();;
        }
        return promptListResponseDtos;
    }

    public PromptResponseDto getPrompt(long prompt_id, String member_id){
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        Assert.notNull(prompt);
        Member promptMember = memberRepository.findById(prompt.getMember().getMember_id()).orElse(null);
        PromptResponseDto promptResponseDto = prompt.toPromptResponseDto(promptMember.getNickname());

        // 태그 넣기
        ArrayList<Tags> tagsList =
                (ArrayList<Tags>) tagsRepository
                        .findByPrompt(prompt);
        Set<String> tags = new HashSet<>();
        for(Tags tag: tagsList){
            tags.add(tag.getContent());
        }
        promptResponseDto.setTags(tags);

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
        promptResponseDto.setExample(chatExamples);


        // 댓글 추가
        List<Comment> comments = commentRepository.findByPromptId(prompt);
        promptResponseDto.setComments(getCommentList(comments, member_id));
        // 좋아요 추가
        List<Preference> like = preferenceRepository.findByPromptAndStatus(prompt, "like");
        promptResponseDto.setLikes(like.size());
        // 싫어요 추가
        List<Preference> dislike = preferenceRepository.findByPromptAndStatus(prompt, "dislike");
        promptResponseDto.setDislikes(dislike.size());
        // 내 프롬프트인지 여부
        Member member = memberRepository.findById(member_id).orElse(null);
        boolean isMyPrompt = prompt.getMember() == member;
        promptResponseDto.setMyPrompt(isMyPrompt);
        return promptResponseDto;
    }
    public ArrayList<PromptListResponseDto> getMyList(String member_id) {
        Member member = memberRepository.findById(member_id).orElse(null);
        Assert.notNull(member);
        List<Prompt> promptList = promptRepository.findByMember(member);
        return (ArrayList<PromptListResponseDto>) getPromptListResponseDtos(promptList);
    }

    public String addPreference(PreferenceRequestDto preferenceRequestDto){
        preferenceRequestDto.validate();
        Prompt prompt = promptRepository.findById(preferenceRequestDto.getPrompt_id()).orElse(null);
        Member member = memberRepository.findById(preferenceRequestDto.getMember_id()).orElse(null);
        Assert.notNull(prompt);
        Assert.notNull(member);

        List<Preference> preferenceList =  preferenceRepository.findByMemberAndPrompt(member, prompt);
        if(preferenceList.isEmpty()){
            Preference newPreference =
                    Preference.builder()
                            .member(member)
                            .prompt(prompt)
                            .status(preferenceRequestDto.getStatus())
                            .build();
            preferenceRepository.save(newPreference);
            return "성공적으로 등록되었습니다.";
        }
        else{
            Preference preference = preferenceList.get(0);
            preferenceRepository.delete(preference);
            return "성공적으로 취소되었습니다.";
        }

    }

    @Transactional
    public String modifyPrompt(Long prompt_id, PromptRequestDto promptRequestDto){
        promptRequestDto.validate();
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        Assert.notNull(prompt);
        Assert.isTrue(Objects.equals(promptRequestDto.getMember_id(), prompt.getMember().getMember_id()));
        Member member = memberRepository.findById(promptRequestDto.getMember_id()).orElse(null);
        prompt.setTitle(promptRequestDto.getTitle());
        prompt.setDescription(promptRequestDto.getDescription());
        prompt.setCategory(promptRequestDto.getCategory());
        prompt.setContent(promptRequestDto.getContent());
        prompt.setPermission(promptRequestDto.isPermission());
        prompt.setWelcome_message(promptRequestDto.getWelcome_message());
        prompt.setMediaType(promptRequestDto.getMediaType());
        prompt.setUpdateTime(LocalDateTime.now());
        promptRepository.save(prompt);

        tagsRepository.deleteByPrompt(prompt);
        chatExampleRepository.deleteByPrompt(prompt);

        // tag 저장 코드
        saveTags(promptRequestDto.getTags(), prompt);
        // example 저장 코드
        saveChatExample(promptRequestDto.getExample(), prompt, member);

//        // configInfo 수정
//        if(promptRequestDto.getMediaType().equals("text")){
//            configInfoRepository.deleteByPromptId(prompt);
//            configInfoRepository.save(promptRequestDto.getGptConfigInfo().toConfigInfo(prompt));
//        }
        return "정상적으로 수정되었습니다.";
    }

    @Transactional
    public String deletePrompt(Long prompt_id, String memberId){
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        Assert.notNull(prompt);
        Assert.isTrue(Objects.equals(memberId, prompt.getMember().getMember_id()));

        tagsRepository.deleteByPrompt(prompt);
        chatExampleRepository.deleteByPrompt(prompt);
        commentRepository.deleteByPromptId(prompt);
        preferenceRepository.deleteByPrompt(prompt);

        if(prompt.getMediaType().equals("text")){
            configInfoRepository.deleteByPromptId(prompt);
        }

        promptRepository.delete(prompt);
        return "정상적으로 삭제되었습니다.";
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
    private void saveTags(Set<String> tags, Prompt prompt){
        for(String tag: tags){
            Tags newTag = new Tags();
            newTag.setPrompt(prompt);
            newTag.setContent(tag);
            tagsRepository.save(newTag);
        }
    }

    private void saveChatExample(ArrayList<ArrayList<Message>> example, Prompt prompt, Member member){
        for(ArrayList<Message> arrayList: example){
            for(Message message: arrayList){
                ChatExample chatExample = message.toChatExample(prompt, member);
                chatExampleRepository.save(chatExample);
            }
        }
    }

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
