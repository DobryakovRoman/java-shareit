package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.IllegalArgumentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {

    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(BookingShortDto bookingShortDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d",
                        "Невозможно создать бронирование - не найден пользователь с id ", userId)));
        Item item = itemRepository.findById(bookingShortDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("%s %d",
                        "Невозможно создать бронирование - не найдена вещь с id ", bookingShortDto.getItemId())));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(
                    "Невозможно создать бронирование - нельзя бронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(
                    "Невозможно создать бронирование - вещь недоступна");
        }
        Booking booking = BookingMapper.toBooking(bookingShortDto);
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BadRequestException(
                    "Невозможно создать бронирование - дата окончания бронирования не может быть раньше даты начала бронирования");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d",
                        "Невозможно подтвердить бронирование - не найдено бронирование с id ", bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(String.format("%s %d %s %d",
                    "Невозможно подтвердить бронирование - не найдено бронирование с id",
                    bookingId,
                    "у пользователя с id",
                    userId));
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException(
                    "Невозможно подтвердить бронирование - бронирование уже подтверждено или отклонено");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        final Sort sort = Sort.by(Sort.Direction.DESC, "start");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d",
                        "Невозможно найти бронирования - не существует пользователя с id", userId)));
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwner(user, sort));
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sort));
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user,
                        LocalDateTime.now(), sort));
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, sort));
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, sort));
                break;
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByUser(Long userId, String state) {
        final Sort sort = Sort.by(Sort.Direction.DESC, "start");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d",
                        "Невозможно найти бронирования - не найден пользователь с id ", userId)));
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository.findAllByBooker(user, sort));
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(
                        user,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        sort));
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndEndBefore(
                        user,
                        LocalDateTime.now(),
                        sort));
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, sort));
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, sort));
                break;
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d",
                        "Бронирование не существует с id ", bookingId)));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(
                    "Просматривать сведения о бронировании может только автор бронирования или владелец");
        }
        return BookingMapper.toBookingDto(booking);
    }
}
