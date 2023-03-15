package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUser(Long id);

    UserDto addUser(UserDto user);

    UserDto updateUser(UserDto user, Long id);

    void removeUser(Long id);
}
