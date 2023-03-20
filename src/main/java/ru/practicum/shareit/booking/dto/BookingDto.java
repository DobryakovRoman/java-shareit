package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.service.BookingStatus;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {

    Long id;

    @NotNull
    @Future
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;

    BookingStatus status;

    UserShortDto booker;

    ItemShortDto item;

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class ItemShortDto {

        Long id;
        String name;
    }

    public static ItemShortDto toItemShortDto(ru.practicum.shareit.item.model.Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class UserShortDto {

        Long id;
        String name;
    }

    public static UserShortDto toUserShortDto(ru.practicum.shareit.user.model.User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}