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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String question = "";

        Member member = memberRepository.findById(modelRequestDto.getMember_id()).orElse(null);
        // 잘못된 멤버 아이디의 경우 에러 처리
        if(member == null){
            return null;
        }
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
        else{
            // 있을 경우 채팅방에 있는 모든 질문을 합치기
            question = getQuestion(question, chat_room.getChat_room_id());
        }
         question = question.concat("," + modelRequestDto.getPrompt());
        // 75 token을 초과하면 잘라줌
        question = preProcessing(question);

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
        Member member = memberRepository.findById(modelRequestDto.getMember_id()).orElse(null);
        Prompt prompt = promptRepository.findById(prompt_id).orElse(null);
        String question = "";
        // 잘못된 멤버나 프롬프트 id의 경우 에러 처리
        if(member == null || prompt == null){
            return null;
        }
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
        else{
            // 있을 경우 채팅방에 있는 모든 질문을 합치기
            question = getQuestion(question, chat_room.getChat_room_id());
        }
        
        // 마지막으로 프롬프트 붙이기
        question =  question.concat("," + prompt.getContent());
        question = question.concat("," + modelRequestDto.getPrompt());
        // 토큰 제한에 걸리지 않게 전처리.
        question =  preProcessing(question);

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


    private String getQuestion(String question, long chat_room_id){
        List<Text> textArrayList = chatRepository.findByChatRoomId(chat_room_id);
        for(Text chat : textArrayList){
            if(chat.isQuestion()){
                question = question.concat("," +chat.getContent());
            }
        }
        return question;
    }

    private Text makeChat(boolean isQuestion, String content, ChatRoom chat_room){
        Text text = new Text();
        text.setQuestion(isQuestion);
        text.setContent(content);
        text.setCreate_time(LocalDateTime.now());
        text.setChat_room_id(chat_room);

        return text;
    }

    private int countToken(String question){
        Pattern hangul = Pattern.compile("[가-힣]");
        Matcher hangulMatcher = hangul.matcher(question);
        int hangulCount = 0;
        while (hangulMatcher.find()) hangulCount++;

        Pattern english = Pattern.compile("[a-zA-Z]");
        Matcher englishMatcher = english.matcher(question);
        int englishCount = 0;
        while (englishMatcher.find()) englishCount++;

        Pattern etc = Pattern.compile("[^가-힣a-zA-Z]");
        Matcher etcMatcher = etc.matcher(question);
        int etcCount = 0;
        while (etcMatcher.find()) etcCount++;

        return hangulCount + (englishCount / 4) + etcCount;
    }

    private String preProcessing(String question){
        int tokenCount = countToken(question);

        while(true){
            if(tokenCount > 75){
                question = question.substring(1);
            }
            else{
                break;
            }
        }
        return question;
    }
}