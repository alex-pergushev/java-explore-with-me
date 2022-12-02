package ru.practicum.ewm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.entities.Comment;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.entities.User;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.mappers.CommentMapper;
import ru.practicum.ewm.repositories.CommentRepository;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.repositories.UserRepository;
import ru.practicum.ewm.utils.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentPersonalServiceImpl implements CommentPersonalService {

    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    @Autowired
    public CommentPersonalServiceImpl(EventRepository eventRepository, CommentRepository commentRepository,
                                      UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CommentDto> getComments(long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }

        return commentRepository.findAllByAuthorId(userId, Pagination.of(from, size)).stream()
                .map(CommentMapper::toCommentOut)
                .collect(Collectors.toList());
    }


    @Override
    public CommentDto getCommentById(long commentId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id=%s не найден", userId))
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException(String.format("Комментарий с id=%s не найден", commentId)));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new NoAccessRightsException(String.format("Пользователь с id=%s не является автором комментария id=%s", userId, commentId));
        }
        return CommentMapper.toCommentOut(comment);
    }

    @Override
    public CommentDto updateComment(long userId, long commentId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        Event event = eventRepository.findById(commentDto.getEvent()).orElseThrow(() ->
                new EventNotFoundException(String.format("Событие с id=%s не найден", commentDto.getEvent())));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException(String.format("Комментарий с id=%s не найден", commentId)));
        if (!(userId == comment.getAuthor().getId())) {
            throw new NoAccessRightsException(String.format("Пользователь с id=%s не является автором комментария id=%s", userId, commentId));
        }
        comment.setAuthor(user);
        comment.setEvent(event);
        if (commentDto.getState() != State.PUBLISHED) {
            comment.setState(commentDto.getState());
        }
        if (!comment.getText().equals(commentDto.getText())) {
            comment.setState(State.PENDING);
        }
        comment.setText(commentDto.getText());
        return CommentMapper.toCommentOut(commentRepository.save(comment));
    }

    @Override
    public CommentDto createComment(long userId, CommentDto commentInDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        Event event = eventRepository.findById(commentInDto.getEvent()).orElseThrow(() ->
                new EventNotFoundException(String.format("Событие с id=%s не найден", commentInDto.getEvent())));
        if (event.getState() != State.PUBLISHED) {
            throw new InvalidRequestException(String.format("Событие с id=%s не опубликовано", event.getId()));
        }
        Comment comment = CommentMapper.toComment(commentInDto);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setState(State.PENDING);
        return CommentMapper.toCommentOut(commentRepository.save(comment));
    }
}
