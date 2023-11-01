package ru.yandex.practicum.filmorate.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> users = new HashMap();
    private int currentId = 0;

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            log.info("Validation failed: Login cannot contain spaces");
            throw new ValidationException("Login cannot contain spaces");
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList(this.users.values());
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        validate(user);
        users.put(++currentId, user);
        user.setId(currentId);
        log.info("User created: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Update failed: User not found");
            throw new DataNotFoundException("User not found");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        return user;

    }
}
