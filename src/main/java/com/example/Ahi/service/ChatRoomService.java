package com.example.Ahi.service;


import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.ChatRoomResponse;
import com.example.Ahi.repository.*;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.TrueFalseConverter;
import org.springframework.cglib.core.Local;
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
    private final PromptRepository promptRepository;


    public Long find_chatroom(String member_id, String model_type, Long chatroom_id){
        Optional<Member> member = memberRepository.findById(member_id);
        model_type = model_type;

        Optional<ChatRoom> existChatRoom = chatRoomRepository.findById(chatroom_id);
        Long chatRoomId;

        if (!existChatRoom.isPresent()){
            ChatRoom chatRoom = ChatRoom.builder()
                    .createTime(LocalDateTime.now())
                    .modelType(model_type)
                    .chatRoomName(model_type)
                    .memberId(member.get())
                    .build();

            chatRoomRepository.save(chatRoom);
            chatRoomId = chatRoom.getChatRoomId();
        }
        //존재하는 경우
        else{
            chatRoomId = existChatRoom.get().getChatRoomId();
        }

        return chatRoomId;
    }

    public Long find_promptroom(String memberId, Long promptId){
        Optional<Member> member = memberRepository.findById(memberId);
        Optional<Prompt> prompt = promptRepository.findById(promptId);

        if(prompt.isEmpty())
            throw new AhiException(ErrorCode.PROMPT_NOT_FOUND);
        if(member.isEmpty())
            throw new AhiException(ErrorCode.USER_NOT_FOUND);

        //String model_type = configInfoRepository.findByPromptId(prompt_id).get().getModel_name();

        Optional<ChatRoom> promptRoom = chatRoomRepository.findByMemberIdAndPromptId(member.get(), prompt.get());
        Long chatRoomId;

        //새로 생성-> 존재하지 않는경우
        if(promptRoom.isEmpty()){
            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomName(prompt.get().getTitle())
                    .memberId(member.get())
                    .promptId(prompt.get())
                    .createTime(LocalDateTime.now())
                    .modelType("gpt-3.5-turbo")
                    .build();
            chatRoomRepository.save(chatRoom);
            chatRoomId = chatRoom.getChatRoomId();
        }
        //존재하는 경우
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
        String result ="";

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
