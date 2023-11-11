package com.example.Ahi.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
import com.example.Ahi.dto.requestDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import com.example.Ahi.repository.ChatExampleRepository;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PromptRepository;
import com.example.Ahi.repository.TagsRepository;
import com.example.Ahi.entity.Message;
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
        ArrayList<PromptListResponseDto> responseList = new ArrayList<>();
        for (Prompt prompt : promptList) {
            // TODO: 좋아요 수 및 댓글 수 로직 추가 필요
            PromptListResponseDto responseDto = prompt.toPromptListResponseDto();
            responseList.add(responseDto);
        }
        return responseList;
    }

    public PromptResponseDto getPrompt(Long id){
        // TODO: 좋아요 및 댓글 추가 필요
        Prompt prompt = promptRepository.findById(id).orElse(null);
        if(prompt == null){
            return null;
        }
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

        return promptResponseDto;
    }
    public ArrayList<PromptListResponseDto> getMyList(String member_id) {
        Optional<Member> member = memberRepository.findById(member_id);
        if(member.isEmpty()){
            return null;
        }
        List<Prompt> promptList = promptRepository.findByMember(member.get());
        ArrayList<PromptListResponseDto> responseList = new ArrayList<>();
        for (Prompt prompt : promptList) {
            // TODO: 좋아요 수 및 댓글 수 로직 추가 필요
            PromptListResponseDto responseDto = prompt.toPromptListResponseDto();
            responseList.add(responseDto);
        }
        return responseList;
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
            throw new NotFoundException("Prompt not found with id " + prompt_id);
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
        // TODO: comment likes 삭제 추가 필요 // 또는 table 제약 조건 변경
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        tagsRepository.deleteByPrompt(prompt);
        chatExampleRepository.deleteByPrompt(prompt);
        if (prompt != null) {
            promptRepository.delete(prompt);
        } else {
            throw new NotFoundException("Prompt not found with id " + prompt_id);
        }
        return "정상적으로 삭제되었습니다.";
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
}
