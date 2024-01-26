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
    List<ChatRoom> findByMemberId(Member memberId);
    Optional<ChatRoom> findByMemberIdAndPromptId(Member memberId, Prompt promptId);
}
