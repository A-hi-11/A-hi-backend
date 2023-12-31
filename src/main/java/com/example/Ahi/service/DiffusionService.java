package com.example.Ahi.service;

// Service

import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Text;
import com.example.Ahi.dto.requestDto.ModelRequestDto;
import com.example.Ahi.dto.responseDto.ModelResponseDto;
import com.example.Ahi.repository.ChatRepository;
import com.example.Ahi.repository.ChatRoomRepository;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PromptRepository;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiffusionService {
    @Value("${diffusion-api-key}")
    private String key;

    private final S3Service s3Service;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final PromptRepository promptRepository;
    private final ChatRepository chatRepository;

    public ModelResponseDto getDiffusion(ModelRequestDto modelRequestDto) {
        modelRequestDto.validate();
        Member member = memberRepository.findById(modelRequestDto.getMember_id()).orElse(null);
        // 잘못된 멤버 아이디의 경우 에러 처리
        Assert.notNull(member);

        String question = modelRequestDto.getPrompt();
        ChatRoom chat_room = chatRoomRepository.findById(modelRequestDto.getChat_room_id()).orElse(null);
        // 채팅방이 없는 경우 생성해줌, 프롬프트는 null
        if(chat_room == null){
            chat_room = new ChatRoom();
            chat_room.setChat_room_name(modelRequestDto.getPrompt());
            chat_room.setCreate_time(LocalDateTime.now());
            chat_room.setPrompt_id(null);
            chat_room.setModel_type(modelRequestDto.getModel_type());
            chat_room.setMember_id(member);
            chatRoomRepository.save(chat_room);
        }

        // 질문을 채팅에 저장
        Text chat_question = makeChat(true, modelRequestDto.getPrompt(), chat_room);
        chatRepository.save(chat_question);

        // 대답을 채팅에 저장
        String imgUrl = getImage(question);
        Text chat_answer = makeChat(false, imgUrl, chat_room);
        chatRepository.save(chat_answer);

        // 응답 결과 반환
        ModelResponseDto modelResponseDto = new ModelResponseDto();
        modelResponseDto.setResponse(imgUrl);
        modelResponseDto.setChat_room_id(chat_room.getChat_room_id());

        return modelResponseDto;
    }
    public ModelResponseDto getDiffusionByPrompt(long prompt_id, ModelRequestDto modelRequestDto) {
        modelRequestDto.validate();
        Member member = memberRepository.findById(modelRequestDto.getMember_id()).orElse(null);
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        // 잘못된 멤버나 프롬프트 id의 경우 에러 처리
        Assert.notNull(prompt);
        Assert.notNull(member);
        String question = modelRequestDto.getPrompt().concat(", " + prompt.getContent());
        ChatRoom chat_room = chatRoomRepository.findById(modelRequestDto.getChat_room_id()).orElse(null);
        // 채팅방이 없는 경우 생성해줌
        if(chat_room == null){
            chat_room = new ChatRoom();
            chat_room.setChat_room_name(modelRequestDto.getPrompt());
            chat_room.setCreate_time(LocalDateTime.now());
            chat_room.setPrompt_id(prompt);
            chat_room.setModel_type(modelRequestDto.getModel_type());
            chat_room.setMember_id(member);
            chatRoomRepository.save(chat_room);
        }

        // 질문을 채팅에 저장
        Text chat_question = makeChat(true, modelRequestDto.getPrompt(), chat_room);
        chatRepository.save(chat_question);

        // 대답을 채팅에 저장
        String imgUrl = getImage(question);
        Text chat_answer = makeChat(false, imgUrl, chat_room);
        chatRepository.save(chat_answer);

        // 응답 결과 반환
        ModelResponseDto modelResponseDto = new ModelResponseDto();
        modelResponseDto.setResponse(imgUrl);
        modelResponseDto.setChat_room_id(chat_room.getChat_room_id());

        return modelResponseDto;
    }
    private String getImage(String prompt){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + key);

        // JSON 형식의 요청 본문 설정
        // 채팅방에서 대화내역 가져와서 프롬프트에 같이 넣어줌.
        // 이후 채팅방에 대화 추가
        String argument = ",realistic,best,4k";

        Map<String, String> body = new HashMap<>();
        body.put("inputs", prompt + argument);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        // POST 요청 보내기
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
        return s3Service.uploadDiffusionImage(response.getBody());
    }

    private Text makeChat(boolean isQuestion, String content, ChatRoom chat_room){
        Text text = new Text();
        text.setQuestion(isQuestion);
        text.setContent(content);
        text.setCreate_time(LocalDateTime.now());
        text.setChat_room_id(chat_room);

        return text;
    }
}