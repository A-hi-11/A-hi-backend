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
import com.example.Ahi.repository.PromptRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    public SseEmitter useGpt(String memberId,Long chatroomId, String request, GptConfigInfo gptConfigInfo){
        String modelType = gptConfigInfo.getModel_name();
        StringBuffer sb = new StringBuffer();
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


        WebClient.create()
                .post().uri(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key)
                .body(BodyInserters.fromValue(requestDto))
                .exchangeToFlux(response -> response.bodyToFlux(String.class))
                .doOnNext(data -> {
                    try {
                        if (data.equals("[DONE]")) {
                            sseEmitter.send("chat_room_id: "+chatRoomId );
                            chatService.save_chat(chatRoomId,false,sb.toString());
                            chatService.save_chat(chatRoomId,true,request);
                            sseEmitter.complete();
                        }
                        else{
                            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
                            ChatStreamResponseDto streamDto = mapper.readValue(data,ChatStreamResponseDto.class);
                            ChatStreamResponseDto.Choice.Delta delta = streamDto.getChoices().get(0).getDelta();

                            if (delta!=null && delta.getContent()!=null){
                                sb.append(delta.getContent());
                                sseEmitter.send(delta.getContent());
                            }
                        }
                    } catch (IOException e) {
                        throw new AhiException(ErrorCode.FAIL_TO_SEND);
                    }
                })
                .doOnComplete(sseEmitter::complete)
                .doOnError(sseEmitter::completeWithError)
                .subscribe();


        return sseEmitter;
    }



    public SseEmitter useGptwithPrompt(String memberId, Long promptId, ChatgptRequest request){
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        StringBuffer sb = new StringBuffer();

        Optional<Prompt> prompt = promptRepository.findById(promptId);
        if(prompt.isEmpty())
            throw new AhiException(ErrorCode.PROMPT_NOT_FOUND);

        Optional<ConfigInfo> configInfo = configInfoRepository.findByPromptId(prompt.get());
        if(configInfo.isEmpty())
            throw new AhiException(ErrorCode.INVALID_INPUT);

        Long chatRoomId = chatRoomService.find_promptroom(memberId,prompt.get(),configInfo.get().getModelName());

        ConfigInfo config = configInfo.get();

        ChatgptRequestDto requestDto = ChatgptRequestDto.builder()
                .messages(compositeMessage(request.getPrompt(),chatRoomId))
                .model(config.getModelName())
                .temperature(config.getTemperature())
                .maxTokens(config.getMaximumLength())
                .stop_sequences(config.getStopSequence())
                .topP(config.getTopP())
                .frequency_penalty(config.getFrequencyPenalty())
                .presence_penalty(config.getPresencePenalty())
                .stream(true)
                .build();
        System.out.println(requestDto);

        WebClient.create()
                .post().uri(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key)
                .body(BodyInserters.fromValue(requestDto))
                .exchangeToFlux(response -> response.bodyToFlux(String.class))
                .doOnNext(data -> {
                    try {
                        if (data.equals("[DONE]")) {
                            sseEmitter.send("chat_room_id: "+chatRoomId );
                            chatService.save_chat(chatRoomId,false,sb.toString());
                            chatService.save_chat(chatRoomId,true,request.getPrompt());
                            sseEmitter.complete();
                        }
                        else{
                            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
                            ChatStreamResponseDto streamDto = mapper.readValue(data,ChatStreamResponseDto.class);
                            ChatStreamResponseDto.Choice.Delta delta = streamDto.getChoices().get(0).getDelta();


                            if (delta!=null && delta.getContent()!=null){
                                sb.append(delta.getContent());
                                sseEmitter.send(delta.getContent());
                            }
                        }
                    } catch (IOException e) {
                        throw new AhiException(ErrorCode.FAIL_TO_SEND);
                    }
                })
                .doOnComplete(sseEmitter::complete)
                .doOnError(sseEmitter::completeWithError)
                .subscribe();


        return sseEmitter;
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

        while (getTokenSize(messages.toString())>MAXTOKEN)
            messages.remove(1);

        return messages;
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

    public int getTokenSize(String text) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);

        List<Integer> encoded = enc.encode(text);
        return encoded.size();
    }


}
