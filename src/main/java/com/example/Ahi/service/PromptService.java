package com.example.Ahi.service;

import com.example.Ahi.domain.*;
import com.example.Ahi.dto.requestDto.PreferenceRequestDto;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
import com.example.Ahi.dto.responseDto.CommentListResponse;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import com.example.Ahi.repository.*;
import com.example.Ahi.entity.Message;
import io.jsonwebtoken.lang.Assert;
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
        Member member = memberRepository.findById(promptRequestDto.getMember_id()).orElse(null);
        // domain prompt 생성
        LocalDateTime now = LocalDateTime.now();
        Prompt prompt = promptRequestDto.toPrompt(member, now);
        if(prompt.getMember() == null){
            return "member가 비었습니다.";
        }
        // prompt 저장 코드
        promptRepository.save(prompt);
        if(promptRequestDto.getMediaType().equals("text")){
            configInfoRepository.save(promptRequestDto.getGptConfigInfo().toConfigInfo(prompt));
        }

        // tag 저장 코드
        saveTags(promptRequestDto.getTags(), prompt);
        // example 저장 코드
        saveChatExample(promptRequestDto.getExample(), prompt);

        return "create successfully!";
    }

    public ArrayList<PromptListResponseDto> getPromptList(String sort, String search) {
        Sort sortObj;
        if ("likes".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "likes");
        } else if ("time".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "create_time");
        } else if ("category".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "category");
        } else {
            sortObj = Sort.unsorted();
        }

        Specification<Prompt> spec = (root, query, cb) -> {
            if (search != null) {
                Predicate nameLike = cb.like(root.get("title"), "%" + search + "%");
//                Predicate tagLike = cb.like(root.get("tag"), "%" + search + "%");
                return cb.or(nameLike);
            } else {
                return cb.conjunction();
            }
        };

        List<Prompt> promptList = promptRepository.findAll(spec, sortObj);
        return getPromptListResponseDtos(promptList);
    }

    public PromptResponseDto getPrompt(long prompt_id, String member_id){
//        Prompt prompt = promptRepository.findById(prompt_id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프롬프트입니다."));
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        Assert.notNull(prompt, "존재하지 않는 프롬프트입니다");
//        if(prompt == null){
//            return null;
//        }
        PromptResponseDto promptResponseDto = prompt.toPromptResponseDto();

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
        promptResponseDto.setComments(getCommentList(comments));
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
        Optional<Member> member = memberRepository.findById(member_id);
        if(member.isEmpty()){
            return null;
        }
        List<Prompt> promptList = promptRepository.findByMember(member.get());
        return getPromptListResponseDtos(promptList);
    }

    public String addPreference(PreferenceRequestDto preferenceRequestDto){
        Prompt prompt = promptRepository.findById(preferenceRequestDto.getPrompt_id()).orElse(null);
        Member member = memberRepository.findById(preferenceRequestDto.getMember_id()).orElse(null);
        if (prompt == null || member == null){
            return "프롬프트 혹은 회원이 존재하지 않습니다.";
        }
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
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        if (prompt != null) {
            prompt.setTitle(promptRequestDto.getTitle());
            prompt.setDescription(promptRequestDto.getDescription());
            prompt.setCategory(promptRequestDto.getCategory());
            prompt.setContent(promptRequestDto.getContent());
            prompt.setPermission(promptRequestDto.isPermission());
            prompt.setWelcome_message(promptRequestDto.getWelcome_message());
            prompt.setMediaType(promptRequestDto.getMediaType());
            prompt.setUpdate_time(LocalDateTime.now());
            promptRepository.save(prompt);
        } else {
            return "존재하지 않는 프롬프트입니다.";
        };
        tagsRepository.deleteByPrompt(prompt);
        chatExampleRepository.deleteByPrompt(prompt);
        // tag 저장 코드
        saveTags(promptRequestDto.getTags(), prompt);
        // example 저장 코드
        saveChatExample(promptRequestDto.getExample(), prompt);


        return "정상적으로 수정되었습니다.";
    }

    @Transactional
    public String deletePrompt(Long prompt_id){
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
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

    private ArrayList<PromptListResponseDto> getPromptListResponseDtos(List<Prompt> promptList) {
        ArrayList<PromptListResponseDto> responseList = new ArrayList<>();
        for (Prompt prompt : promptList) {
            List<Preference> likes = preferenceRepository.findByPromptAndStatus(prompt, "like");
            List<Preference> dislikes = preferenceRepository.findByPromptAndStatus(prompt, "dislike");
            List<Comment> commentList = commentRepository.findByPromptId(prompt);
            PromptListResponseDto responseDto = prompt
                    .toPromptListResponseDto(commentList.size(), likes.size(), dislikes.size());
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

    private void saveChatExample(ArrayList<ArrayList<Message>> example, Prompt prompt){
        for(ArrayList<Message> arrayList: example){
            for(Message message: arrayList){
                ChatExample chatExample = message.toChatExample(prompt);
                chatExampleRepository.save(chatExample);
            }
        }
    }

    private ArrayList<CommentListResponse> getCommentList(List<Comment> list){
        ArrayList<CommentListResponse> result = new ArrayList<>();
        for(Comment comment: list){
            Member member = memberRepository.findById(comment.getMember_id().getMember_id()).orElse(null);
            assert member != null;
            result.add(comment.toCommentListResponse(member));
        }
        return result;
    }
}
