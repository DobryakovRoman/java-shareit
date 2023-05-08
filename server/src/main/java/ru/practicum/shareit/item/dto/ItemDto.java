package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;

    String name;

    String description;

    Boolean available;

    BookingShortDto lastBooking;

    BookingShortDto nextBooking;

    List<CommentDtoShort> comments;

    Long requestId;

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class CommentDtoShort {

        Long id;
        String text;
        String authorName;
        LocalDateTime created;
    }

    public static CommentDtoShort toCommentDtoShort(Comment comment) {
        return CommentDtoShort.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
