package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User firstUser;
    User secondUser;
    Item item;
    Booking booking;

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
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
    }

    @Test
    void saveBookingsTest() {
        assertEquals(bookingRepository.findAll().size(), 1);
    }
}