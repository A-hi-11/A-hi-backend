package com.example.Ahi.service;


import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Text;
import com.example.Ahi.dto.requestDto.Message;
import com.example.Ahi.dto.responseDto.ChatItemResponse;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
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
        text.setChatRoomId(chatRoom.get());
        text.setQuestion(isQuestion);
        text.setContent(context);
        text.setCreateTime(LocalDateTime.now());

        chatRepository.save(text);

    }

    public List<ChatItemResponse> readChatList(Long chatRoomId){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isEmpty())
            throw new AhiException(ErrorCode.CHATROOM_NOT_FOUND);

        List<Text> chats = chatRepository.findByChatRoomId(chatRoom.get());

        List<ChatItemResponse> chatItemList = new ArrayList<>();
        for (Text chat:chats){
            ChatItemResponse item = new ChatItemResponse();
            item.setContent(chat.getContent());
            item.setQuestion(chat.isQuestion());
            if (!chat.isQuestion() && chatRoom.get().getModelType().equals("image"))
                item.setImage(true);
            else
                item.setImage(false);

            chatItemList.add(item);
        }

        return chatItemList;
    }


    public List<Message> memorizedChat(ChatRoom chatRoomId){
        List<Text> chatList = chatRepository.findByChatRoomId(chatRoomId);
        List<Message> messages = new ArrayList<>();
        if(!chatList.isEmpty()){
            for(Text chat:chatList){
                Message message = new Message();
                if (chat.isQuestion())message.setRole("user");
                else message.setRole("assistant");

                message.setContent(chat.getContent());
                messages.add(message);
            }
        }
        return messages;
    }
}
