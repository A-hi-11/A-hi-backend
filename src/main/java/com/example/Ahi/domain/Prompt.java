package com.example.Ahi.domain;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prompt_id;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member_id;
    private String description;
    @Column(length = 10000)
    private String content;
    private String category;
    private boolean permission;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
    private String welcome_message;


}
