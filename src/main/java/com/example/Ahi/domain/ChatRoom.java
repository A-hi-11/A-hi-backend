package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chatroom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_room_id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="prompt_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prompt prompt_id;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member_id;
    private LocalDateTime create_time;
    private String chat_room_name;
    private String model_type;
}
