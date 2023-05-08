package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingShortDto bookingShortDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    List<BookingDto> getAllByOwner(Long userId, String state, Long from, Long size);

    List<BookingDto> getAllByUser(Long userId, String state, Long from, Long size);

    BookingDto getBooking(Long bookingId, Long userId);
}
