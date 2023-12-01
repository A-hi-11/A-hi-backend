package com.example.Ahi.domain;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long text_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_room_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chat_room_id;

    @Column(length = 10000)
    private String content;
    private boolean isQuestion;
    private LocalDateTime create_time;
}
