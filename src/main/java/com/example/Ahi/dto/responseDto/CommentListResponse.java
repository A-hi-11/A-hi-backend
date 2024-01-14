package com.example.Ahi.dto.responseDto;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentListResponse {
    private String content;
    private LocalDateTime create_time;
    private Long comment_id;
    private String member_nickname;
    private String member_profile_img;
    private boolean isPermissioned;
}
