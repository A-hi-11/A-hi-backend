package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="chat_image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id;
    @ManyToOne
    @JoinColumn(name="chat_room_id")
    private ChatRoom chat_room_id;
    private String content;
}
