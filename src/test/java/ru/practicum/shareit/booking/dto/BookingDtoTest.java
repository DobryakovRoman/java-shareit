package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> jacksonTester;

    @Test
    @SneakyThrows
    void bookingDtoJsonTest() {
        User firstUser = User.builder().id(1L).name("firstUser").email("firstUser@ya.ru").build();
        User secondUser = User.builder().id(2L).name("secondUser").email("secondUser@ya.ru").build();
        Item item = Item.builder().id(1L).name("firstName").description("Description").available(true).owner(firstUser).build();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .booker(secondUser)
                .status(BookingStatus.WAITING)
                .build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathMapValue("$.item").isNotEmpty();
        assertThat(result).extractingJsonPathMapValue("$.booker").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}