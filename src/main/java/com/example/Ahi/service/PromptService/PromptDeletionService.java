package com.example.Ahi.service.PromptService;

import com.example.Ahi.domain.Prompt;
import com.example.Ahi.repository.*;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PromptDeletionService {
    private final PromptRepository promptRepository;
    private final ChatExampleRepository chatExampleRepository;
    private final TagsRepository tagsRepository;
    private final CommentRepository commentRepository;
    private final PreferenceRepository preferenceRepository;
    private final ConfigInfoRepository configInfoRepository;
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
}
