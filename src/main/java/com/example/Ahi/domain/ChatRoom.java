package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_room_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt_id;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member_id;
    private LocalDateTime create_time;
    private String chat_room_name;
    private String model_type;
}
