package ru.practicum.ewm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.CommentDto;
import ru.practicum.ewm.services.CommentAdminService;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@Validated
public class CommentAdminController {
    private final CommentAdminService commentAdminService;

    @Autowired
    public CommentAdminController(CommentAdminService commentAdminService) {
        this.commentAdminService = commentAdminService;
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable long commentId, @RequestBody CommentDto commentDto) {
        return commentAdminService.updateComment(commentId, commentDto);
    }

    @GetMapping
    public List<CommentDto> getComments(@RequestParam(value = "from", defaultValue = "0")
                                        @PositiveOrZero(message = "Значение from должно быть больше или равна 0")
                                        int from,
                                        @RequestParam(value = "size", defaultValue = "10")
                                        @Min(value = 1, message = "Минимально допустимое значение для size равно 1")
                                        int size) {
        return commentAdminService.getComments(from, size);
    }


}
