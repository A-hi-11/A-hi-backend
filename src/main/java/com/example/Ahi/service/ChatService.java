package com.example.Ahi.service;


import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Text;
import com.example.Ahi.repository.ChatRepository;
import com.example.Ahi.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;


    public void save_chat(Long chat_room_id, boolean isQuestion, String context){
        Text text = new Text();
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chat_room_id);
        text.setChat_room_id(chatRoom.get());
        text.setQuestion(isQuestion);
        text.setContent(context);
        text.setCreate_time(LocalDateTime.now());


        chatRepository.save(text);

    }
}
