package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("%s %d %s", "Пользователь с id:", id, "не найден")));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto user) {
        User tempUser = UserMapper.toUser(user);
        return UserMapper.toDto(userRepository.save(tempUser));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto user, Long id) {
        User userFromDto = UserMapper.toUser(user);
        User tempUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s %s %d %s",
                        "Невозможно обновить данные пользователя. ", "Пользователь с id:", id, "не найден")));
        if (user.getEmail() != null) {
            tempUser.setEmail(userFromDto.getEmail());
        }
        if (user.getName() != null) {
            tempUser.setName(userFromDto.getName());
        }
        return UserMapper.toDto(userRepository.save(tempUser));
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }
}
