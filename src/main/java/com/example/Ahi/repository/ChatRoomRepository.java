package com.example.Ahi.repository;

import com.example.Ahi.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query(value = "SELECT * from chatroom WHERE member_id = :memberId and prompt_id is null",nativeQuery = true)
    Optional<ChatRoom> findAllByMemberAndNull(@Param("memberId") String memberId);
    List<ChatRoom> findByMemberId(String member_id);
}
