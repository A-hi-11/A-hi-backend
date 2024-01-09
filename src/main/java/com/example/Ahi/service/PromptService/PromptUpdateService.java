package com.example.Ahi.service.PromptService;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Preference;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.requestDto.PreferenceRequestDto;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
import com.example.Ahi.repository.*;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PromptUpdateService {
    private final PromptRepository promptRepository;
    private final ChatExampleRepository chatExampleRepository;
    private final TagsRepository tagsRepository;
    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final PromptUtils promptUtils;

    private static final String SUCCESS_ADD_MESSAGE = "성공적으로 등록되었습니다.";
    private static final String SUCCESS_REMOVE_MESSAGE = "성공적으로 취소되었습니다.";

    public String addPreference(PreferenceRequestDto preferenceRequestDto){
        preferenceRequestDto.validate();
        Prompt prompt = promptRepository.findById(preferenceRequestDto.getPrompt_id()).orElse(null);
        Member member = memberRepository.findById(preferenceRequestDto.getMember_id()).orElse(null);
        Assert.notNull(prompt);
        Assert.notNull(member);

        List<Preference> preferenceList =  preferenceRepository.findByMemberAndPrompt(member, prompt);

        return preferenceList.isEmpty() ? addNewPreference(member, prompt, preferenceRequestDto.getStatus())
                : removeExistingPreference(preferenceList);
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
        promptUtils.saveTags(promptRequestDto.getTags(), prompt);
        // example 저장 코드
        promptUtils.saveChatExample(promptRequestDto.getExample(), prompt, member);

//        // configInfo 수정
//        if(promptRequestDto.getMediaType().equals("text")){
//            configInfoRepository.deleteByPromptId(prompt);
//            configInfoRepository.save(promptRequestDto.getGptConfigInfo().toConfigInfo(prompt));
//        }
        return "정상적으로 수정되었습니다.";
    }

    private String addNewPreference(Member member, Prompt prompt, String status) {
        Preference newPreference = Preference.builder()
                .member(member)
                .prompt(prompt)
                .status(status)
                .build();
        preferenceRepository.save(newPreference);
        return SUCCESS_ADD_MESSAGE;
    }

    private String removeExistingPreference(List<Preference> preferences) {
        Preference preference = preferences.get(0);
        preferenceRepository.delete(preference);
        return SUCCESS_REMOVE_MESSAGE;
    }
}
