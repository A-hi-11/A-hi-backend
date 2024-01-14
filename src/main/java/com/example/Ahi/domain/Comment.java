package com.example.Ahi.domain;
import com.example.Ahi.dto.responseDto.CommentListResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="prompt_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prompt promptId;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member memberId;
    private String content;
    private LocalDateTime createTime;

    public CommentListResponse toCommentListResponse(Member member, boolean isPermission){
        return CommentListResponse.builder()
                .isPermissioned(isPermission)
                .member_profile_img(member.getProfileImage())
                .member_nickname(member.getNickname())
                .comment_id(commentId)
                .create_time(createTime)
                .content(content)
                .build();
    }
}
