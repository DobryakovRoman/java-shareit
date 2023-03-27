package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User firstUser;
    User secondUser;
    Booking booking;
    Item item;

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
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByItemIdOrderByStartAsc() {
        assertThat(bookingRepository.findAllByItemIdOrderByStartAsc(item.getId()).size(), equalTo(1));
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore() {
        assertThat(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                        secondUser.getId(),
                        item.getId(),
                        BookingStatus.WAITING,
                        LocalDateTime.now().plusDays(5)).size(),
                equalTo(1));
    }

    @Test
    void findAllByBookerAndEndBefore() {
        assertThat(bookingRepository.findAllByBookerAndEndBefore(
                secondUser,
                LocalDateTime.now().plusDays(5),
                Pageable.ofSize(3)).getTotalPages(), equalTo(1));
    }
}