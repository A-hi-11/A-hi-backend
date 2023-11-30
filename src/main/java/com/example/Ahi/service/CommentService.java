package com.example.Ahi.service;

import com.example.Ahi.domain.Comment;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.CommentListResponse;
import com.example.Ahi.dto.responseDto.CommentResponse;
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
    public CommentResponse create_comment(Long prompt_id, String context){
        Comment comment = new Comment();
        Optional<Prompt> prompt = promptRepository.findById(prompt_id);
        Optional<Member> member = memberRepository.findById("test@gmail.com");
        CommentResponse response = new CommentResponse();

        if(prompt.isPresent()){
            comment.setCreate_time(LocalDateTime.now());
            comment.setContent(context);
            comment.setPromptId(prompt.get());
            // TODO : member 수정필요
            comment.setMember_id(member.get());
            commentRepository.save(comment);
            response.setMessage("성공적으로 저장하였습니다.");
            response.setId(comment.getComment_id());
        }
        else{
            response.setMessage("없는 프롬프트입니다. 저장에 실패했습니다.");
        }

        return response;
    }


    public CommentResponse delete_comment(Long id){
        Optional<Comment> comment = commentRepository.findById(id);
        CommentResponse response = new CommentResponse();

        if(comment.isPresent()){
            commentRepository.delete(comment.get());
            response.setMessage("성공적으로 삭제하였습니다.");
        }
        else{
            response.setMessage("없는 댓글입니다. 삭제에 실패했습니다.");
        }
        return response;
    }


    public List<CommentListResponse> read_comment(Long prompt_id,String member_id){
        Optional<Prompt> prompt = promptRepository.findById(prompt_id);
        List<CommentListResponse> responseList = new ArrayList<>();


        if (!prompt.isPresent()){

        }
        else{
            List<Comment> comments = commentRepository.findByPromptId(prompt.get());
            for(Comment comment:comments){
                CommentListResponse response = new CommentListResponse();
                response.setComment_id(comment.getComment_id());
                response.setContent(comment.getContent());
                response.setCreate_time(comment.getCreate_time());
                //멤버정보
                String writer_id = comment.getMember_id().getMember_id();
                Optional<Member> member = memberRepository.findById(writer_id);
                response.setMember_nickname(member.get().getNickname());
                response.setMember_profile_img(member.get().getProfile_image());
                //허가정보
                response.setPermissioned(isPermissioned(member_id,writer_id));

                responseList.add(response);
            }
        }


        return responseList;
    }

    //댓글 작성자와 현재 로그인한 사용자가 일치하는지 확인 필요
    public boolean isPermissioned(String member_id, String writer_id){
        //본인의 프롬프트에 달린 댓글이면 허가 (x)
        //본인이 작성한 댓글이면 허가
        if(member_id.equals(writer_id)) return true;
        else return false;
    }








    public CommentResponse update_comment(Long id,String context){
        Optional<Comment> comment = commentRepository.findById(id);
        CommentResponse response = new CommentResponse();
        
        if(comment.isPresent()){
            comment.get().setContent(context);
            comment.get().setCreate_time(LocalDateTime.now());
            System.out.println(comment.get());
            commentRepository.save(comment.get());
            response.setMessage("성공적으로 수정하였습니다.");
            response.setId(comment.get().getComment_id());
        }
        else{
            response.setMessage("없는 댓글입니다. 수정에 실패했습니다.");
        }
        return response;
    }
}
