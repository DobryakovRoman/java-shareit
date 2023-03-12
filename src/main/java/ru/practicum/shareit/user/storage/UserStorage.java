package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getUsers();

    Optional<User> getUser(Long id);

    User addUser(User user);

    User updateUser(User user);

    void removeUser(Long id);
}
