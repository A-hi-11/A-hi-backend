package com.example.Ahi.service;

import com.example.Ahi.config.ChatgptConfig;
import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Text;
import com.example.Ahi.dto.requestDto.ChatgptRequestDto;
import com.example.Ahi.dto.requestDto.Message;
import com.example.Ahi.dto.responseDto.ChatgptResponseDto;
import com.example.Ahi.repository.ChatRoomRepository;
import com.example.Ahi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.responseDto.ChatgptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatgptService {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    @Autowired
    ChatgptConfig config;

    @Value("${gpt-key}")
    private String key;
    private final String url = "https://api.openai.com/v1/chat/completions";


    public ChatgptResponse getGpt(ChatgptRequest request){
        ChatgptResponse response = new ChatgptResponse();
        ChatgptRequestDto requestDto = new ChatgptRequestDto();

        // 채팅방 찾기(없으면 생성)
        Long chat_room_id = chatRoomService.find_chatroom("member_id","gpt-3.5-turbo");
        //요청 메세지
        requestDto.setMessages(compositeMessage(request.getPrompt(),chat_room_id));

        HttpEntity<ChatgptRequestDto> requestEntity = compositeRequest(requestDto);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ChatgptResponseDto> responseEntity = restTemplate.postForEntity(
                url,
                requestEntity,
                ChatgptResponseDto.class);

        //response 파싱
        ChatgptResponseDto result = responseEntity.getBody();
        String answer = result.getChoices().get(0).getMessage().getContent();

        response.setAnswer(answer);


        //채팅내역 저장
        //사용자 발화
        chatService.save_chat(chat_room_id,true,request.getPrompt());
        //gpt 발화
        chatService.save_chat(chat_room_id,false,response.getAnswer());


        return response;
    }



    public HttpEntity<ChatgptRequestDto> compositeRequest(ChatgptRequestDto requestDto){
        HttpHeaders headers = config.gptHeader();
        //TODO: config클래스에 분리하기
        ChatgptRequestDto body = config.gptBody();

        requestDto.setModel("gpt-3.5-turbo");

        return new HttpEntity<>(requestDto, headers);
    }



    public List<Message> compositeMessage(String input,Long chat_room_id){

        List<Message> messages = chatService.memorizedChat(chat_room_id);
        messages.add(new Message("user", input));

        return messages;
    }



}
