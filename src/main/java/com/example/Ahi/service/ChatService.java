package com.example.Ahi.service;


import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Text;
import com.example.Ahi.dto.responseDto.ChatItemResponse;
import com.example.Ahi.repository.ChatRepository;
import com.example.Ahi.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public List<ChatItemResponse> show_chatList(Long chat_room_id){
        List<Text> chats = chatRepository.findByChatRoomId(chat_room_id);

        List<ChatItemResponse> chatItemList = new ArrayList<>();

        if(chats.isEmpty()){
            //TODO: 예외처리
            ChatItemResponse item = new ChatItemResponse();
            item.setContent(null);
        }
        else{
            for (Text chat:chats){
                ChatItemResponse item = new ChatItemResponse();
                item.setContent(chat.getContent());
                item.setQuestion(chat.isQuestion());
                chatItemList.add(item);
            }

        }


        return chatItemList;

    }
}
