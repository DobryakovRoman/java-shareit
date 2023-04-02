package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingServiceImpl;
    User firstUser;
    User secondUser;
    Item item;
    Booking booking;
    BookingShortDto bookingShortDto;
    BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder().id(1L).name("firstUser").email("firstUser@ya.ru").build();
        secondUser = User.builder().id(2L).name("secondUser").email("secondUser@ya.ru").build();
        item = Item.builder().id(1L).name("firstName").description("Description").available(true).owner(firstUser).build();
        booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .booker(secondUser)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingShortDto = BookingMapper.toBookingShortDto(booking);
    }

    @Test
    void addBookingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        bookingServiceImpl.addBooking(bookingShortDto, secondUser.getId());

        verify(bookingRepository).save(any());
    }

    @Test
    void approveTest() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingServiceImpl.approve(booking.getId(), firstUser.getId(), true);

        verify(bookingRepository).save(any());
    }

    @Test
    void approveTest_reject() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingServiceImpl.approve(booking.getId(), firstUser.getId(), false);

        verify(bookingRepository).save(any());
    }

    @Test
    void getAllByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findAllByItemOwner(any(), any())).thenReturn(Page.empty());

        List<BookingDto> result = bookingServiceImpl.getAllByOwner(firstUser.getId(), "ALL", 0L, 10L);

        verify(bookingRepository).findAllByItemOwner(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getAllByUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.findAllByBooker(any(), any())).thenReturn(Page.empty());

        List<BookingDto> result = bookingServiceImpl.getAllByUser(secondUser.getId(), "ALL", 0L, 10L);

        verify(bookingRepository).findAllByBooker(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingDto result = bookingServiceImpl.getBooking(booking.getId(), firstUser.getId());

        verify(bookingRepository).findById(any());
        assertEquals(result.getId(), booking.getId());
    }
}