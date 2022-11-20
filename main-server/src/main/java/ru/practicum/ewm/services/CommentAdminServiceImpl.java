package ru.practicum.ewm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.entities.Comment;
import ru.practicum.ewm.exceptions.CommentNotFoundException;
import ru.practicum.ewm.mappers.CommentMapper;
import ru.practicum.ewm.repositories.CommentRepository;
import ru.practicum.ewm.utils.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentAdminServiceImpl implements CommentAdminService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentAdminServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto updateComment(long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(String.format("комментарий с id=%s не найден", commentId)));

        if (commentDto.getState() != null) {
            comment.setState(commentDto.getState());
        }
        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
        }

        Comment updated = commentRepository.save(comment);
        log.info("комментарий id={}, оставленный пользователем id={} обновлен статус комментария {}", updated.getId(),
                updated.getAuthor().getId(), updated.getState());
        return CommentMapper.toCommentOut(updated);

    }

    @Override
    public List<CommentDto> getComments(int from, int size) {
        return commentRepository.findAll(Pagination.of(from, size)).stream()
                .map(CommentMapper::toCommentOut)
                .collect(Collectors.toList());
    }
}
