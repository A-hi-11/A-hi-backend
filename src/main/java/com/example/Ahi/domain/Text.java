package com.example.Ahi.domain;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long text_id;
    @ManyToOne
    @JoinColumn(name="chat_room_id")
    private ChatRoom chat_room_id;
    private String content;
    private boolean isQuestion;
    private LocalDateTime create_time;
}
