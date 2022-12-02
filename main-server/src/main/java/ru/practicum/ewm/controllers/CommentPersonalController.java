package ru.practicum.ewm.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.CommentDto;
import ru.practicum.ewm.services.CommentPersonalService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping("/users/{userId}/comments")
public class CommentPersonalController {
    private final CommentPersonalService commentPersonalService;

    public CommentPersonalController(CommentPersonalService commentPersonalService) {
        this.commentPersonalService = commentPersonalService;
    }


    @PostMapping
    public CommentDto addComment(@RequestBody @Valid CommentDto commentInDto,
                                 @PathVariable @Positive(message = "Значение {userId} должно быть больше 0") long userId) {
        return commentPersonalService.createComment(userId, commentInDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid CommentDto commentDto,
                                    @PathVariable @Positive(message = "Значение {userId} должно быть больше 0") long userId,
                                    @PathVariable @Positive(message = "Значение {commentId} должно быть больше 0") long commentId) {
        return commentPersonalService.updateComment(userId, commentId, commentDto);
    }

    @GetMapping
    public List<CommentDto> getComments(@RequestParam(value = "from", defaultValue = "0")
                                        @PositiveOrZero(message = "Значение from должно быть больше или равна 0")
                                        int from,
                                        @RequestParam(value = "size", defaultValue = "10")
                                        @Min(value = 1, message = "Минимально допустимое значение для size равно 1")
                                        int size,
                                        @PathVariable @Positive(message = "Значение {userId} должно быть больше 0") long userId) {
        return commentPersonalService.getComments(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable @Positive(message = "Значение {commentId} должно быть больше 0") long commentId,
                                 @PathVariable @Positive(message = "Значение {userId} должно быть больше 0") long userId) {
        return commentPersonalService.getCommentById(commentId, userId);
    }
}
