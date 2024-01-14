package com.example.Ahi.repository;

import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Comment;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query(value = "SELECT * from chatroom WHERE member_id = :memberId and prompt_id is null",nativeQuery = true)
    Optional<ChatRoom> findAllByMemberAndNull(@Param("memberId") String memberId);

//    @Query(value = "SELECT * from chatroom WHERE member_id = :memberId",nativeQuery = true)
//    List<ChatRoom> findByMemberId(@Param("memberId") String memberId);

    List<ChatRoom> findByMemberId(Member memberId);

//    @Query(value = "SELECT * from chatroom WHERE member_id = :memberId and prompt_id = :promptId",nativeQuery = true)
//    Optional<ChatRoom> findAllByMemberAndPrompt(@Param("memberId") String memberId, @Param("promptId") Long promptId);

    Optional<ChatRoom> findByMemberIdAndPromptId(Member memberId, Prompt promptId);
}
