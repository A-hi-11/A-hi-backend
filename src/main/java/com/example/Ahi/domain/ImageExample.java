package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ImageExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long example_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt_id;
    private String generated_image;
    private String content;

}
