package ru.practicum.shareit.user.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInMemoryStorage implements UserStorage {
    final Map<Long, User> users = new HashMap<>();

    Long id = 0L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(Long id) {
        if (users.get(id) == null) {
            return Optional.empty();
        }
        return Optional.of(users.get(id));
    }

    @Override
    public User addUser(User user) {
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
    }
}
