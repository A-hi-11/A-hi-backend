package com.example.Ahi.service;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import com.example.Ahi.dto.PromptRequestDto;
import com.example.Ahi.repository.ChatExampleRepository;
import com.example.Ahi.repository.PromptRepository;
import com.example.Ahi.repository.TagsRepository;
import com.example.Ahi.entity.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;


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

        // TODO: chatExampe에서 order 와 isQuestion이 필요할까요? 그렇다면 채팅내역도 바뀌어야 할 것 같습니다.
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
}
