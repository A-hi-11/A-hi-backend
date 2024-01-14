package com.example.Ahi.service;

import com.example.Ahi.domain.Comment;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.CommentListResponse;
import com.example.Ahi.dto.responseDto.CommentResponse;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import com.example.Ahi.repository.CommentRepository;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PromptRepository promptRepository;
    private final MemberRepository memberRepository;

    public CommentResponse create_comment(String memberId,Long promptId, String context){
        Optional<Prompt> prompt = promptRepository.findById(promptId);
        Optional<Member> member = memberRepository.findById(memberId);
        CommentResponse response = new CommentResponse();

        if(prompt.isEmpty())
            throw new AhiException(ErrorCode.PROMPT_NOT_FOUND);

        Comment comment = Comment.builder()
                .content(context)
                .promptId(prompt.get())
                .memberId(member.get())
                .createTime(LocalDateTime.now())
                .build();
        commentRepository.save(comment);

        response.setMessage("성공적으로 저장하였습니다.");
        response.setId(comment.getCommentId());

        return response;
    }


    public CommentResponse delete_comment(String memberId, Long commentId){
        Optional<Comment> comment = commentRepository.findById(commentId);
        CommentResponse response = new CommentResponse();

        if(comment.isEmpty())
            throw new AhiException(ErrorCode.COMMENT_NOT_FOUND);

        Member writer = comment.get().getMemberId();
        if(!isPermissioned(memberId,writer.getMemberId()))
            throw new AhiException(ErrorCode.INVALID_PERMISSION);

        commentRepository.delete(comment.get());
        response.setMessage("성공적으로 삭제하였습니다.");
        response.setId(commentId);

        return response;
    }


    public List<CommentListResponse> read_comment(Long promptId,String memberId){
        Optional<Prompt> prompt = promptRepository.findById(promptId);

        if (prompt.isEmpty())
            throw new AhiException(ErrorCode.PROMPT_NOT_FOUND);

        List<Comment> comments = commentRepository.findByPromptId(prompt.get());
        List<CommentListResponse> responseList = new ArrayList<>();

        for(Comment comment:comments){
            Member writer = comment.getMemberId();
            CommentListResponse response = CommentListResponse.builder()
                    .comment_id(comment.getCommentId())
                    .content(comment.getContent())
                    .create_time(comment.getCreateTime())
                    .member_nickname(writer.getNickname())
                    .member_profile_img(writer.getProfileImage())
                    .isPermissioned(isPermissioned(memberId,writer.getMemberId()))
                    .build();
            responseList.add(response);
        }

        return responseList;
    }


    //댓글 작성자와 현재 로그인한 사용자가 일치하는지 확인 필요
    public boolean isPermissioned(String memberId, String writerId){
        //본인이 작성한 댓글이면 허가
        if(memberId.equals(writerId)) return true;
        else return false;
    }


    public CommentResponse update_comment(String memberId,Long commentId,String context){
        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<Member> member = memberRepository.findById(memberId);
        CommentResponse response = new CommentResponse();

        if(comment.isEmpty())
            throw new AhiException(ErrorCode.COMMENT_NOT_FOUND);
        if(member.isEmpty())
            throw new AhiException(ErrorCode.USER_NOT_FOUND);

        Member writer = comment.get().getMemberId();
        if(!isPermissioned(memberId,writer.getMemberId()))
            throw new AhiException(ErrorCode.INVALID_PERMISSION);

        Comment modifiedComment = Comment.builder()
                .commentId(commentId)
                .memberId(member.get())
                .promptId(comment.get().getPromptId())
                .content(context)
                .createTime(LocalDateTime.now())
                .build();
        commentRepository.save(modifiedComment);


        response.setMessage("성공적으로 수정하였습니다.");
        response.setId(modifiedComment.getCommentId());
        return response;
    }
}
