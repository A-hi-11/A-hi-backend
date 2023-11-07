package com.example.Ahi;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import com.example.Ahi.dto.responseDto.PromptRequestDto;
import com.example.Ahi.entity.Message;
import com.example.Ahi.repository.ChatExampleRepository;
import com.example.Ahi.repository.PromptRepository;
import com.example.Ahi.repository.TagsRepository;
import com.example.Ahi.service.PromptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PromptServiceCreateTest {
    @Autowired
    private PromptService promptService;

    @MockBean
    private PromptRepository promptRepository;

    @MockBean
    private ChatExampleRepository chatExampleRepository;

    @MockBean
    private TagsRepository tagsRepository;

    @Test
    public void testCreate() {
        PromptRequestDto promptRequestDto = getPromptRequestDto();
        // When
        when(promptRepository.save(any(Prompt.class))).thenReturn(new Prompt());
        when(chatExampleRepository.save(any(ChatExample.class))).thenReturn(new ChatExample());
        when(tagsRepository.save(any(Tags.class))).thenReturn(new Tags());

        String result = promptService.create(promptRequestDto);

        // Then
        assertEquals("create successfully!", result);
        verify(promptRepository, times(1)).save(any(Prompt.class));
        verify(chatExampleRepository, times(promptRequestDto.getExample().get(0).size()
        + promptRequestDto.getExample().get(1).size())).save(any(ChatExample.class));
        verify(tagsRepository, times(promptRequestDto.getTags().size())).save(any(Tags.class));
    }

    private static PromptRequestDto getPromptRequestDto() {
        Member member = new Member("test");
        member.setEmail("asdf");
        member.setPassword("asdfasdf");
        member.setNickname("test");
        member.setProfile_image("dddd");
        member.setMember_id("123456");

        // Given
        PromptRequestDto promptRequestDto = new PromptRequestDto();
        // TODO: Set the properties of promptRequestDto
        promptRequestDto.setCategory("그림");
        promptRequestDto.setDescription("테스팅입니다.");
        promptRequestDto.setContent("contetn 테스트");
        promptRequestDto.setExample(getExample());
        promptRequestDto.setTags(getSet());
        promptRequestDto.setPermission(true);
        promptRequestDto.setTitle("제목 테스트");
        promptRequestDto.setMediaType("text");
        promptRequestDto.setMember_id(member);
        promptRequestDto.setWelcome_message("웰컴 메세지 테스트");
        return promptRequestDto;
    }

    public static ArrayList<ArrayList<Message>> getExample(){
        // Message 객체 생성
        Message message1 = new Message();
        message1.setMessage("Hello");
        message1.setIs_question(false);

        Message message2 = new Message();
        message2.setMessage("How are you?");
        message2.setIs_question(true);

// ArrayList<Message> 생성
        ArrayList<Message> messageList1 = new ArrayList<>();
        messageList1.add(message1);
        messageList1.add(message2);

// 또 다른 Message 객체 생성
        Message message3 = new Message();
        message3.setMessage("Goodbye");
        message3.setIs_question(false);

// 또 다른 ArrayList<Message> 생성
        ArrayList<Message> messageList2 = new ArrayList<>();
        messageList2.add(message3);

// ArrayList<ArrayList<Message>> 생성
        ArrayList<ArrayList<Message>> example = new ArrayList<>();
        example.add(messageList1);
        example.add(messageList2);
        return example;
    }
    public static Set<String> getSet(){
// Set<String> 생성
        Set<String> tags = new HashSet<>();

// 태그 추가
        tags.add("sports");
        tags.add("news");
        tags.add("technology");

        return tags;
    }

}
