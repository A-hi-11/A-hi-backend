package com.example.Ahi.domain;

import com.example.Ahi.dto.responseDto.CommentListResponse;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt promptId;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member_id;
    private String content;
    private LocalDateTime Create_time;

    public CommentListResponse toCommentListResponse(Member member){
        return CommentListResponse.builder()
                .isPermissioned(true)
                .member_profile_img(member.getProfile_image())
                .member_nickname(member.getNickname())
                .comment_id(comment_id)
                .create_time(Create_time)
                .content(content)
                .build();
    }
}
