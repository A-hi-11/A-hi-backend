package com.example.Ahi.dto.responseDto;

import com.example.Ahi.domain.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentListResponse {
    private List<Comment> comments;
}
