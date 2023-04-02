package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    public void toDtoTest() {
        User user = User.builder().name("firstUser").email("firstUser@ya.ru").build();
        user.setId(1L);

        UserDto userDto = UserMapper.toDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals("firstUser", userDto.getName());
        assertEquals("firstUser@ya.ru", userDto.getEmail());
    }
}
