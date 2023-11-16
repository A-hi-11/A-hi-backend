package com.example.Ahi.repository;

import com.example.Ahi.domain.ChatRoom;
import com.example.Ahi.domain.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Text,Long> {
    //TODO : 채팅방마다 마지막 메세지 가져오기
    @Query(value = "SELECT content from text WHERE chat_room_id = :chatRoomId order by create_time desc limit 1",nativeQuery = true)
    Optional<String> findLastMessage(@Param("chatRoomId")Long chatRoomId);
}
