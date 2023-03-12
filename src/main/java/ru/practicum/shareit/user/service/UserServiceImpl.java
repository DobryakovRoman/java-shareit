package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailDublicate;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    final UserStorage userStorage;

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d %s", "Пользователь с id:", id, "не найден")));
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto addUser(UserDto user) {
        User tempUser = UserMapper.toUser(user);
        checkEmailUnique(tempUser);
        return UserMapper.toDto(userStorage.addUser(tempUser));
    }

    @Override
    public UserDto updateUser(UserDto user, Long id) {
        User userFromDto = UserMapper.toUser(user);
        User tempUser = userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s %s %d %s",
                        "Невозможно обновить данные пользователя. ", "Пользователь с id:", id, "не найден")));
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(tempUser.getEmail())) {
                checkEmailUnique(userFromDto);
            }
            tempUser.setEmail(userFromDto.getEmail());
        }
        if (user.getName() != null) {
            tempUser.setName(userFromDto.getName());
        }
        return UserMapper.toDto(userStorage.updateUser(tempUser));
    }

    @Override
    public void removeUser(Long id) {
        getUser(id);
        userStorage.removeUser(id);
    }

    void checkEmailUnique(User user) {
        for (User userCheck : userStorage.getUsers()) {
            if (user.getEmail().equals(userCheck.getEmail())) {
                throw new EmailDublicate("Пользователь с таким email уже зарегистрирован");
            }
        }
    }
}
