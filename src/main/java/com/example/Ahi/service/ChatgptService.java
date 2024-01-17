package com.example.Ahi.service;

import com.example.Ahi.config.ChatgptConfig;
import com.example.Ahi.domain.*;
import com.example.Ahi.dto.requestDto.ChatgptRequestDto;
import com.example.Ahi.dto.requestDto.Message;
import com.example.Ahi.dto.responseDto.ChatStreamResponseDto;
import com.example.Ahi.dto.responseDto.ChatgptResponseDto;
import com.example.Ahi.entity.GptConfigInfo;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import com.example.Ahi.repository.ChatRoomRepository;
import com.example.Ahi.repository.ConfigInfoRepository;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PromptRepository;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import lombok.RequiredArgsConstructor;
import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.responseDto.ChatgptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatgptService {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final PromptRepository promptRepository;
    private final ConfigInfoRepository configInfoRepository;
    private final ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatgptConfig config;
    @Value("${gpt-key}")
    private String key;
    private static int MAXTOKEN = 4000;
    private static final Long DEFAULT_TIMEOUT = 120L * 1000 * 60;
    private final String url = "https://api.openai.com/v1/chat/completions";


    public SseEmitter getGpt(String memberId,Long chatroomId, String request, GptConfigInfo gptConfigInfo){
        //ChatgptResponse responseDto = new ChatgptResponse();
        String modelType = gptConfigInfo.getModel_name();

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 채팅방 찾기(없으면 생성)
        Long chatRoomId = chatRoomService.find_chatroom(memberId,modelType,chatroomId);
        //요청 메세지
        ChatgptRequestDto requestDto = ChatgptRequestDto.builder()
                .messages(compositeMessage(request,chatRoomId))
                .model(gptConfigInfo.getModel_name())
                .temperature(gptConfigInfo.getTemperature())
                .maxTokens(gptConfigInfo.getMaximum_length())
                .stop_sequences(gptConfigInfo.getStop_sequence())
                .topP(gptConfigInfo.getTop_p())
                .frequency_penalty(gptConfigInfo.getFrequency_penalty())
                .presence_penalty(gptConfigInfo.getPresence_penalty())
                .stream(true)
                .build();

//        HttpEntity<ChatgptRequestDto> requestEntity = compositeRequest(requestDto);
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<ChatgptResponseDto> responseEntity = restTemplate.postForEntity(
//                url,
//                requestEntity,
//                ChatgptResponseDto.class);
//
//        //response 파싱
//        ChatgptResponseDto result = responseEntity.getBody();
//        String answer = result.getChoices().get(0).getMessage().getContent();
//
//        responseDto.setAnswer(answer);
//        responseDto.setChat_room_id(chatRoomId);
//
//
//        //채팅내역 저장
//        chatService.save_chat(chatRoomId,true,request);
//        chatService.save_chat(chatRoomId,false,responseDto.getAnswer());

        WebClient client = WebClient.create("https://api.openai.com/v1");

        client.post().uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key)
                .body(BodyInserters.fromValue(requestDto))
                .exchangeToFlux(response -> response.bodyToFlux(ChatStreamResponseDto.class))
                .doOnNext(line -> {
                    try {
                        if (line.getChoices().get(0).getFinishReason()!=null) {
                            sseEmitter.complete();
                            return;
                        }
                        sseEmitter.send(line.getChoices().get(0).getDelta().getContent());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnError(sseEmitter::completeWithError)
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }



    public SseEmitter useGptwithPrompt(String memberId, Long promptId, ChatgptRequest request){
        //1. prompt찾아 "user" role로 세팅하기
        //2. 대화내역 찾아 추가하기 -> 없다면 새로운 채팅방으로 만들어 줘야함
        //3. 전송하기
        //4. 대화내역 저장하기
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        ChatgptResponse response = new ChatgptResponse();

        Long chatRoomId = chatRoomService.find_promptroom(memberId,promptId);
        Optional<Prompt> prompt = promptRepository.findById(promptId);
        Optional<ConfigInfo> configInfo = configInfoRepository.findByPromptId(prompt.get());
        List<Message> messages = compositeMessage(request.getPrompt(),chatRoomId);

        if(configInfo.isEmpty())
            throw new AhiException(ErrorCode.INVALID_INPUT);


        ConfigInfo config = configInfo.get();
        ChatgptRequestDto requestDto = ChatgptRequestDto.builder()
                .messages(messages)
                .model(config.getModelName())
                .temperature(config.getTemperature())
                .maxTokens(config.getMaximumLength())
                .stop_sequences(config.getStopSequence())
                .topP(config.getTopP())
                .frequency_penalty(config.getFrequencyPenalty())
                .presence_penalty(config.getPresencePenalty())
                .stream(true)
                .build();



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
        response.setChat_room_id(chatRoomId);

        //채팅내역 저장
        chatService.save_chat(chatRoomId,true,request.getPrompt());
        chatService.save_chat(chatRoomId,false,response.getAnswer());

        return sseEmitter;
    }

    public Message setPrompt(Long promptId){
        Message message = new Message();
        Optional<Prompt> prompt = promptRepository.findById(promptId);

        if(prompt.isPresent()){
            message.setRole("user");
            message.setContent(prompt.get().getContent());
        }

        return message;
    }






    public HttpEntity<ChatgptRequestDto> compositeRequest(ChatgptRequestDto requestDto){
        HttpHeaders headers = config.gptHeader();

        return new HttpEntity<>(requestDto, headers);
    }



    public List<Message> compositeMessage(String input,Long chatRoomId){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        List<Message> messages = new ArrayList<>();

        if(chatRoom.isPresent() && chatRoom.get().getPromptId()!=null){
            Message message = setPrompt(chatRoom.get().getPromptId().getPrompt_id());
            messages.add(message);
        }

        messages.addAll(chatService.memorizedChat(chatRoom.get()));
        messages.add(new Message("user", input));

        System.out.println("----");
        System.out.println(messages);

        while (getTokenSize(messages.toString())>MAXTOKEN){
            messages.remove(1);
        }
        return messages;
    }


    public int getTokenSize(String text) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);

        List<Integer> encoded = enc.encode(text);
        return encoded.size();
    }


}
