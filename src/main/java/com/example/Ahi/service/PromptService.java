package com.example.Ahi.service;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import com.example.Ahi.dto.PromptRequestDto;
import com.example.Ahi.dto.PromptListResponseDto;
import com.example.Ahi.dto.PromptResponseDto;
import com.example.Ahi.repository.ChatExampleRepository;
import com.example.Ahi.repository.PromptRepository;
import com.example.Ahi.repository.TagsRepository;
import com.example.Ahi.entity.Message;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class PromptService {
    private final PromptRepository promptRepository;
    private final ChatExampleRepository chatExampleRepository;
    private final TagsRepository tagsRepository;

    public PromptService(PromptRepository promptRepository,
                         ChatExampleRepository chatExampleRepository,
                         TagsRepository tagsRepository){
        this.promptRepository = promptRepository;
        this.chatExampleRepository = chatExampleRepository;
        this.tagsRepository = tagsRepository;
    }
    public String create(PromptRequestDto promptRequestDto){
        // domain prompt 생성
        LocalDateTime now = LocalDateTime.now();
        Prompt prompt = promptRequestDto.toPrompt(now);
// prompt 저장 코드
        promptRepository.save(prompt);
        // tag 저장 코드
        for(String tag: promptRequestDto.getTags()){
            Tags newTag = new Tags();
            newTag.setPrompt(prompt);
            newTag.setContent(tag);
            tagsRepository.save(newTag);
        }

        // example 저장 코드
        for(ArrayList<Message> arrayList: promptRequestDto.getExample()){
            for(Message message: arrayList){
                ChatExample chatExample = message.toChatExample(prompt);
                chatExampleRepository.save(chatExample);
            }
        }

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
}
