package com.example.Ahi.service;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import com.example.Ahi.dto.PromptRequestDto;
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
import java.util.List;


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

        // tag 저장 코드
        for(String tag: promptRequestDto.getTags()){
            Tags newTag = new Tags();
            newTag.setPrompt_id(prompt);
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
        // prompt 저장 코드
        promptRepository.save(prompt);
        return "create successfully!";
    }

    public ArrayList<PromptResponseDto> getPromptList(String sort, String search) {
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
                Predicate nameLike = cb.like(root.get("name"), "%" + search + "%");
                Predicate tagLike = cb.like(root.get("tag"), "%" + search + "%");
                return cb.or(nameLike, tagLike);
            } else {
                return cb.conjunction();
            }
        };

        List<Prompt> promptList = promptRepository.findAll(spec, sortObj);
        ArrayList<PromptResponseDto> responseList = new ArrayList<>();
        for (Prompt prompt : promptList) {
            PromptResponseDto responseDto = prompt.toPromptResponseDto();
            responseList.add(responseDto);
        }
        return responseList;
    }

    public Prompt getPrompt(Long id){
        return promptRepository.findById(id).orElse(null);
    }
}
