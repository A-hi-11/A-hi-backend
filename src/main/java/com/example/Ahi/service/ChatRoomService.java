package com.example.Ahi.service;


import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Member;
import com.example.Ahi.repository.ChatRoomRepository;
import com.example.Ahi.repository.CommentRepository;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    public Long start_chatroom(String member_id, String model_type){
        Optional<Member> member = memberRepository.findById("test@gmail.com");
        model_type = "gpt-3.5-turbo";
        Optional<ChatRoom> exists_chatRoom = chatRoomRepository.findAllByMemberAndNull(member.get().getMember_id());
        Long chat_room_id;

        if (!exists_chatRoom.isPresent()){
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setCreate_time(LocalDateTime.now());
            chatRoom.setMember_id(member.get());
            chatRoom.setChat_room_name("???");
            chatRoom.setModel_type(model_type);

            chatRoomRepository.save(chatRoom);
            chat_room_id = chatRoom.getChat_room_id();
        }
        else{
            chat_room_id = exists_chatRoom.get().getChat_room_id();
        }

        return chat_room_id;
    }


}
