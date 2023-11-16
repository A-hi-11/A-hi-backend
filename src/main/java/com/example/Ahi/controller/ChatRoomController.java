package com.example.Ahi.controller;


import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.responseDto.ChatItemResponse;
import com.example.Ahi.dto.responseDto.ChatRoomResponse;
import com.example.Ahi.service.ChatRoomService;
import com.example.Ahi.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("my-page/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    @GetMapping()
    public ResponseEntity roomList(){
        Member member = new Member();
        List<ChatRoomResponse> responseList = chatRoomService.roomList(member);

        return ResponseEntity.ok().body(responseList);
    }

    @DeleteMapping("{chat_room_id}")
    public ResponseEntity delete(@PathVariable("chat_room_id")Long chat_room_id){
        String response = chatRoomService.delete(chat_room_id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/read/{chat_room_id}")
    public ResponseEntity readChatList(@PathVariable("chat_room_id")Long chat_room_id){
        List<ChatItemResponse> response = chatService.show_chatList(chat_room_id);

        return ResponseEntity.ok().body(response);
    }

}
