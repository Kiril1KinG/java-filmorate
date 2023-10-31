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

    Map<Integer, User> data = new HashMap();
    private int currentId = 0;

    private void validate(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getLogin().contains(" ")) {
            log.info("Не пройдена валидация: Логин не может сожержать пробелы");
            throw new ValidationException("Логин не может сожержать пробелы");
        }
    }

    @GetMapping
    public List<User> getData() {
        return new ArrayList(this.data.values());
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        validate(user);
        data.put(++currentId, user);
        user.setId(currentId);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if (data.containsKey(user.getId())) {
            data.put(user.getId(), user);
            log.info("Изменен пользователь: {}", user);
            return user;
        } else {
            log.info("Ошибка обновления: Пользователь не найден");
            throw new DataNotFoundException("Пользователь не найден");
        }
    }
}
