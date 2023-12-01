package com.example.Ahi.service;


import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.ChatRoomResponse;
import com.example.Ahi.repository.*;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ConfigInfoRepository configInfoRepository;
    private final PromptRepository promptRepository;


    public Long find_chatroom(String member_id, String model_type, Long chatroom_id){
        Optional<Member> member = memberRepository.findById("test@gmail.com");
        model_type = "gpt-3.5-turbo";

        Optional<ChatRoom> exists_chatRoom = chatRoomRepository.findById(chatroom_id);
        Long chat_room_id;

        if (!exists_chatRoom.isPresent()){
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setCreate_time(LocalDateTime.now());
            chatRoom.setMember_id(member.get());
            chatRoom.setChat_room_name(model_type);
            chatRoom.setModel_type(model_type);

            chatRoomRepository.save(chatRoom);
            chat_room_id = chatRoom.getChat_room_id();
        }
        else{
            chat_room_id = exists_chatRoom.get().getChat_room_id();
        }

        return chat_room_id;
    }

    public Long find_promptroom(String member_id, Long prompt_id){
        Optional<Member> member = memberRepository.findById("test@gmail.com");
        Optional<Prompt> prompt = promptRepository.findById(prompt_id);
        //String model_type = configInfoRepository.findByPromptId(prompt_id).get().getModel_name();

        Optional<ChatRoom> promptRoom = chatRoomRepository.findAllByMemberAndPrompt(member.get().getMember_id(), prompt_id);
        Long chat_room_id;

        //새로 생성
        if(!promptRoom.isPresent()){
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setCreate_time(LocalDateTime.now());
            chatRoom.setMember_id(member.get());
            chatRoom.setChat_room_name(prompt.get().getTitle());
            chatRoom.setModel_type("gpt-3.5-turbo");
            chatRoom.setPrompt_id(prompt.get());

            chatRoomRepository.save(chatRoom);
            chat_room_id = chatRoom.getChat_room_id();
        }
        else{
            chat_room_id = promptRoom.get().getChat_room_id();
        }

        return chat_room_id;
    }

    public List<ChatRoomResponse> roomList(Member member){
        String member_id = "test@gmail.com";
        //member = memberRepository.findById(member_id).get();
        List<ChatRoom> chatRooms = chatRoomRepository.findByMemberId(member_id);
        List<ChatRoomResponse> lists = new ArrayList<>();

        if (chatRooms == null || chatRooms.isEmpty()) {
            throw new AhiException(ErrorCode.GPT_TOKEN_ERROR);
        }


        for (ChatRoom chatRoom:chatRooms){
            ChatRoomResponse response = new ChatRoomResponse();
            response.setChat_room_id(chatRoom.getChat_room_id());
            response.setCreate_time(chatRoom.getCreate_time());
            response.setChat_room_name(chatRoom.getChat_room_name());
            response.setModel_type(chatRoom.getModel_type());
            Optional<String> last_message = chatRepository.findLastMessage(chatRoom.getChat_room_id());
            if(last_message.isPresent())
                response.setLast_message(last_message.get());
            lists.add(response);
        }

        return lists;
    }


    public String delete(Long chat_room_id){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chat_room_id);
        String result ="";
        if(chatRoom.isPresent()){
            chatRoomRepository.delete(chatRoom.get());
            result = "채팅방을 삭제하였습니다.";
        }
        else{
            result = "없는 채팅방입니다. 삭제에 실패하였습니다.";
        }

        return result;

    }


}
