package com.example.Ahi.controller;


import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.responseDto.ChatItemResponse;
import com.example.Ahi.dto.responseDto.ChatRoomResponse;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.service.ChatRoomService;
import com.example.Ahi.service.ChatService;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("my-page/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @GetMapping()
    public ResponseEntity<List<ChatRoomResponse>> roomList(Authentication authentication){

        String memberId = authentication.getName();
        List<ChatRoomResponse> responseList = chatRoomService.readRoomList(memberId);

        return ResponseEntity.ok().body(responseList);
    }

    @DeleteMapping("{chat_room_id}")
    public ResponseEntity<String> delete(Authentication authentication,
                                 @PathVariable("chat_room_id")Long chat_room_id){

        String memberId = authentication.getName();
        String response = chatRoomService.deleteChatRoom(memberId,chat_room_id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/read/{chat_room_id}")
    public ResponseEntity<List<ChatItemResponse>> readChatList(Authentication authentication,
                                       @PathVariable("chat_room_id")Long chat_room_id){

        String memberId = authentication.getName();
        List<ChatItemResponse> response = chatService.readChatList(chat_room_id);

        return ResponseEntity.ok().body(response);
    }

}
