package com.example.Ahi.service;

import com.example.Ahi.config.ChatgptConfig;
import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Text;
import com.example.Ahi.dto.requestDto.ChatgptRequestDto;
import com.example.Ahi.dto.requestDto.Message;
import com.example.Ahi.dto.responseDto.ChatgptResponseDto;
import com.example.Ahi.entity.GptConfigInfo;
import com.example.Ahi.repository.ChatRoomRepository;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PromptRepository;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
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
    private final PromptRepository promptRepository;
    @Autowired
    ChatgptConfig config;

    @Value("${gpt-key}")
    private String key;
    private final String url = "https://api.openai.com/v1/chat/completions";


    public ChatgptResponse getGpt(String memberId,Long chatroom_id, String request, GptConfigInfo gptConfigInfo){
        ChatgptResponse response = new ChatgptResponse();
        ChatgptRequestDto requestDto = new ChatgptRequestDto();

        // 채팅방 찾기(없으면 생성)
        Long chat_room_id = chatRoomService.find_chatroom(memberId,"gpt-3.5-turbo",chatroom_id);
        //요청 메세지
        requestDto.setMessages(compositeMessage(request,chat_room_id));

        requestDto.setModel(gptConfigInfo.getModel_name());
        requestDto.setTemperature((double) gptConfigInfo.getTemperature());
        requestDto.setMaxTokens(gptConfigInfo.getMaximum_length().intValue());
        requestDto.setStop_sequences(gptConfigInfo.getStop_sequence());
        requestDto.setTopP((double) gptConfigInfo.getTop_p());
        requestDto.setFrequency_penalty((double) gptConfigInfo.getFrequency_penalty());
        requestDto.setPresence_penalty((double) gptConfigInfo.getPresence_penalty());

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
        chatService.save_chat(chat_room_id,true,request);
        //gpt 발화
        chatService.save_chat(chat_room_id,false,response.getAnswer());


        return response;
    }



    public ChatgptResponse useGpt(String memberId,Long prompt_id, ChatgptRequest request){
        //1. prompt찾아 "user" role로 세팅하기
        //2. 대화내역 찾아 추가하기 -> 없다면 새로운 채팅방으로 만들어 줘야함
        //3. 전송하기
        //4. 대화내역 저장하기
        ChatgptResponse response = new ChatgptResponse();
        ChatgptRequestDto requestDto = new ChatgptRequestDto();


        Long chat_room_id = chatRoomService.find_promptroom(memberId,prompt_id);

        List<Message> messages = compositeMessage(request.getPrompt(),chat_room_id);
        messages.add(0,setPrompt(prompt_id));
        requestDto.setMessages(messages);

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

    public Message setPrompt(Long prompt_id){
        Message message = new Message();
        Optional<Prompt> prompt = promptRepository.findById(prompt_id);

        if(prompt.isPresent()){
            message.setRole("user");
            message.setContent(prompt.get().getContent());
        }

        return message;
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

        while (getTokenSize(messages.toString())>4000){
            messages.remove(1);
        }
        //System.out.println(getTokenSize(messages.toString())+"/n"+messages);
        return messages;
    }

    public int getTokenSize(String text) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);

        List<Integer> encoded = enc.encode(text);
        return encoded.size();
    }


}
