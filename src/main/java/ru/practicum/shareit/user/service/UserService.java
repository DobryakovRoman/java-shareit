package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUser(Long id);

    UserDto addUser(User user);

    UserDto updateUser(User user, Long id);

    void removeUser(Long id);
}
