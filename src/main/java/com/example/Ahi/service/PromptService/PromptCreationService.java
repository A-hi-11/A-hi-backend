package com.example.Ahi.service.PromptService;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
import com.example.Ahi.repository.*;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromptCreationService {
    private final PromptRepository promptRepository;
    private final MemberRepository memberRepository;
    private final ConfigInfoRepository configInfoRepository;
    private final PromptUtils promptUtils;

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
        promptUtils.saveTags(promptRequestDto.getTags(), prompt);

        // example 저장 코드
        promptUtils.saveChatExample(promptRequestDto.getExample(), prompt, member);

        return "create successfully!";
    }


}
