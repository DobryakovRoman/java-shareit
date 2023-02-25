package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserController {

    final UserServiceImpl userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) {
        return userService.addUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable Long id) {
        return userService.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userService.removeUser(id);
    }
}
