package com.example.Ahi.domain;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="chat_text")
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long text_id;
    @ManyToOne
    @JoinColumn(name="chat_room_id")
    private ChatRoom chat_room_id;
    private String content;
}
