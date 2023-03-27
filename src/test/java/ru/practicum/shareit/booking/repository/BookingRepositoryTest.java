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

    @BeforeEach
    void beforeEach() {
        userRepository.save(User.builder().name("firstUser").email("firstUser@ya.ru").build());
        userRepository.save(User.builder().name("secondUser").email("secondUser@ya.ru").build());
        itemRepository.save(Item.builder().name("firstName").description("Description").available(true).owner(userRepository.findAll().get(0)).request(null).build());
        Booking booking = Booking.builder()
                .id(1L)
                .item(itemRepository.findAll().get(0))
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .booker(userRepository.findAll().get(1))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void saveBookingsTest() {
        assertEquals(bookingRepository.findAll().size(), 1);
    }
}