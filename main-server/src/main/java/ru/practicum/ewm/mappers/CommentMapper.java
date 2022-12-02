package ru.practicum.ewm.mappers;

import ru.practicum.ewm.dtos.CommentDto;
import ru.practicum.ewm.entities.Comment;

public class CommentMapper {

    public static CommentDto toCommentOut(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .event(comment.getEvent().getId())
                .state(comment.getState())
                .build();
    }

    public static Comment toComment(CommentDto comment) {
        return Comment.builder()
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}
