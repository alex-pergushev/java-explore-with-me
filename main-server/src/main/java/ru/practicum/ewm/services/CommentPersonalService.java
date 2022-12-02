package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.*;

import java.util.List;

public interface CommentPersonalService {

    List<CommentDto> getComments(long userId, int from, int size);

    CommentDto getCommentById(long userId, long id);

    CommentDto updateComment(long userId, long commentId, CommentDto commentDto);

    CommentDto createComment(long userId, CommentDto commentInDto);
}
