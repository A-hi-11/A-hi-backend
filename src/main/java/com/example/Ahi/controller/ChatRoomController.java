package com.example.Ahi.controller;


import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.responseDto.ChatRoomResponse;
import com.example.Ahi.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("my-page/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    @GetMapping()
    public ResponseEntity roomList(){
        Member member = new Member();
        List<ChatRoomResponse> responseList = chatRoomService.roomList(member);

        return ResponseEntity.ok().body(responseList);
    }

    @DeleteMapping("{chat_room_id}")
    public ResponseEntity delete(@PathVariable("chat_room_id")Long chat_room_id){


        return ResponseEntity.ok().body(null);
    }

}
