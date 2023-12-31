package com.example.Ahi.dto.responseDto;

import com.example.Ahi.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CommentListResponse {
    private String content;
    private LocalDateTime create_time;
    private Long comment_id;
    private String member_nickname;
    private String member_profile_img;
    private boolean isPermissioned;

    public CommentListResponse() {

    }
}
