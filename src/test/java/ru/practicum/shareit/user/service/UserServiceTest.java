package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userServiceImpl;
    User user;

    @BeforeEach
    void beforeEach() {
        when(userRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        user = User.builder().name("firstUser").email("firstUser@ya.ru").build();
        user.setId(1L);
    }

    @Test
    void addUserTest() {
        when(userRepository.save(user)).thenReturn(user);

        UserDto userDto = userServiceImpl.addUser(UserMapper.toDto(user));

        verify(userRepository).save(any());
        assertEquals(UserMapper.toDto(user).getName(), userDto.getName());
    }

    @Test
    void updateUserTest() {
        long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userServiceImpl.updateUser(UserMapper.toDto(user), userId);

        verify(userRepository).save(any());
    }

    @Test
    void updateUserTestNotFound() {
        long userId = 5L;
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());
                    userServiceImpl.getUser(userId);
                }
        );
    }

    @Test
    void getUsersTest() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> usersDto = userServiceImpl.getUsers();
        verify(userRepository).findAll();
        assertEquals(1, usersDto.size());
        assertEquals(UserMapper.toDto(users.get(0)).getId(), usersDto.get(0).getId());
        assertEquals(UserMapper.toDto(users.get(0)).getName(), usersDto.get(0).getName());
        assertEquals(UserMapper.toDto(users.get(0)).getEmail(), usersDto.get(0).getEmail());
    }

    @Test
    void getUserTest() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = UserMapper.toDto(user);
        UserDto userDtoFromDb = userServiceImpl.getUser(userId);

        verify(userRepository, times(1)).findById(userId);
        assertEquals(userDto.getEmail(), userDtoFromDb.getEmail());
    }

    @Test
    void getUserTestNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userServiceImpl.getUser(userId));
    }

    @Test
    void removeUserTest() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userServiceImpl.removeUser(userId);

        verify(userRepository).deleteById(userId);
    }
}
