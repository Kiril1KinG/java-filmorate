package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int currentId = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(++currentId, user);
        user.setId(currentId);
        log.info("User created: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsValue(user)) {
            log.info("Update failed: User not found");
            throw new DataNotFoundException("User not found");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public boolean containsUser(User user) {
        return users.containsValue(user);
    }

    @Override
    public boolean containsUserById(int id) {
        return users.containsKey(id);
    }
}
