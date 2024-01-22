package com.example.Ahi.service;


import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.ConfigInfo;
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


    public Long find_chatroom(String memberId, String modelType, Long givenId){
        Optional<Member> member = memberRepository.findById(memberId);
        Optional<ChatRoom> existChatRoom = chatRoomRepository.findById(givenId);
        Long chatRoomId;

        //TODO: 프롬프트 없이 gpt를 이용하는 경우 채팅방 이름
        if (existChatRoom.isEmpty()){
            ChatRoom chatRoom = ChatRoom.builder()
                    .createTime(LocalDateTime.now())
                    .modelType(modelType)
                    .chatRoomName(modelType)
                    .memberId(member.get())
                    .build();

            chatRoomRepository.save(chatRoom);
            chatRoomId = chatRoom.getChatRoomId();
        }
        else{
            chatRoomId = existChatRoom.get().getChatRoomId();
        }

        return chatRoomId;
    }

    public Long find_promptroom(String memberId, Prompt prompt, String modelName){
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isEmpty())
            throw new AhiException(ErrorCode.USER_NOT_FOUND);

        Optional<ChatRoom> promptRoom = chatRoomRepository.findByMemberIdAndPromptId(member.get(), prompt);
        Long chatRoomId;

        if(promptRoom.isEmpty()){
            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomName(prompt.getTitle())
                    .memberId(member.get())
                    .promptId(prompt)
                    .createTime(LocalDateTime.now())
                    .modelType(modelName)
                    .build();
            chatRoomRepository.save(chatRoom);
            chatRoomId = chatRoom.getChatRoomId();
        }
        else{
            chatRoomId = promptRoom.get().getChatRoomId();
        }

        return chatRoomId;
    }


    public List<ChatRoomResponse> readRoomList(String memberId){
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty())
            throw new AhiException(ErrorCode.USER_NOT_FOUND);

        List<ChatRoom> chatRooms = chatRoomRepository.findByMemberId(member.get());
        List<ChatRoomResponse> lists = new ArrayList<>();

        for (ChatRoom chatRoom:chatRooms){
            ChatRoomResponse response = ChatRoomResponse.builder()
                    .chat_room_id(chatRoom.getChatRoomId())
                    .chat_room_name(chatRoom.getChatRoomName())
                    .create_time(chatRoom.getCreateTime())
                    .model_type(chatRoom.getModelType())
                    .last_message(findLastMessage(chatRoom.getChatRoomId()))
                    .build();

            lists.add(response);
        }

        return lists;
    }

    public String findLastMessage(Long chatRoomId){
        String lastMessage = "";
        Optional<String> last_message = chatRepository.findLastMessage(chatRoomId);
        if(last_message.isPresent())
            lastMessage = last_message.get();

        return lastMessage;
    }


    public String deleteChatRoom(String memberId, Long chatRoomId){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        String result = "";

        if(chatRoom.isEmpty())
            throw new AhiException(ErrorCode.CHATROOM_NOT_FOUND);

        if(!isPermissioned(memberId,chatRoom.get().getMemberId().getMemberId()))
            throw new AhiException(ErrorCode.INVALID_PERMISSION);

        chatRoomRepository.delete(chatRoom.get());
        result = "채팅방을 삭제하였습니다.";

        return result;

    }

    public boolean isPermissioned(String memberId, String writerId){
        if (memberId.equals(writerId)) return true;
        else return false;
    }


}
