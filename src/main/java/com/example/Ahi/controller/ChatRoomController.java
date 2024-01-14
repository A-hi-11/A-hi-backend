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
    private final MemberRepository memberRepository;
    @GetMapping()
    public ResponseEntity roomList(Authentication authentication){
        String memberId = authentication.getName();
        Optional<Member> member = memberRepository.findById(memberId);
        if (!member.isPresent()){
            throw new AhiException(ErrorCode.USER_NOT_FOUND);
        }
        List<ChatRoomResponse> responseList = chatRoomService.roomList(member.get());

        return ResponseEntity.ok().body(responseList);
    }

    @DeleteMapping("{chat_room_id}")
    public ResponseEntity delete(Authentication authentication, @PathVariable("chat_room_id")Long chat_room_id){
        String memberId = authentication.getName();
        String response = chatRoomService.delete(memberId,chat_room_id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/read/{chat_room_id}")
    public ResponseEntity readChatList(Authentication authentication,@PathVariable("chat_room_id")Long chat_room_id){
        String memberId = authentication.getName();
        List<ChatItemResponse> response = chatService.show_chatList(memberId,chat_room_id);

        return ResponseEntity.ok().body(response);
    }

}
