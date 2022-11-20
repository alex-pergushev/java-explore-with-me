package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.*;

import java.util.List;

public interface CommentAdminService {


    CommentDto updateComment(long commentId, CommentDto commentDto);

    List<CommentDto> getComments(int from, int size);

}
