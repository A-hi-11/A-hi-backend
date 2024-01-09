package com.example.Ahi.service.PromptService;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import com.example.Ahi.entity.Message;
import com.example.Ahi.repository.ChatExampleRepository;
import com.example.Ahi.repository.TagsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PromptUtils {
    private final TagsRepository tagsRepository;
    private final ChatExampleRepository chatExampleRepository;

    public void saveTags(Set<String> tags, Prompt prompt){
        for(String tag: tags){
            Tags newTag = new Tags();
            newTag.setPrompt(prompt);
            newTag.setContent(tag);
            tagsRepository.save(newTag);
        }
    }

    public void saveChatExample(ArrayList<ArrayList<Message>> example, Prompt prompt, Member member){
        for(ArrayList<Message> arrayList: example){
            for(Message message: arrayList){
                ChatExample chatExample = message.toChatExample(prompt, member);
                chatExampleRepository.save(chatExample);
            }
        }
    }
}
