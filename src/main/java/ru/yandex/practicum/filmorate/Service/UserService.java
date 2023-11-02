package ru.yandex.practicum.filmorate.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        if (!userStorage.getUsers().contains(userStorage.getUserById(friendId))) {
            log.info("Add friend failed: Incorrect friend id");
            throw new DataNotFoundException("Incorrect friend id");
        }
        if (!userStorage.getUsers().contains(userStorage.getUserById(friendId))) {
            log.info("Add friend failed: Incorrect user id");
            throw new DataNotFoundException("Incorrect user id");
        }
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
        log.info("Friend added: {}", userStorage.getUserById(userId));
        return userStorage.getUserById(userId);
    }

    public User deleteFriend(int userId, int friendId) {
        if (!userStorage.getUsers().contains(userStorage.getUserById(friendId))) {
            log.info("Delete friend failed: Incorrect friend id");
            throw new DataNotFoundException("Incorrect friend id");
        }
        if (!userStorage.getUsers().contains(userStorage.getUserById(friendId))) {
            log.info("Delete friend failed: Incorrect user id");
            throw new DataNotFoundException("Incorrect user id");
        }
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
        log.info("Friend deleted: {}", userStorage.getUserById(userId));
        return userStorage.getUserById(userId);
    }

    public List<User> getMutualFriends(int userId, int friendId) {
        final List<Integer> mutualFriendsIds = new ArrayList<>();
        for (int id : userStorage.getUserById(userId).getFriends()) {
            if (userStorage.getUserById(friendId).getFriends().contains(id)) {
                mutualFriendsIds.add(id);
            }
        }
        List<User> mutualFriends = mutualFriendsIds.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        log.info("Mutual friends received: {}", mutualFriends);
        return mutualFriends;
    }

    public List<User> getFriends(int id) {
        List<User> friends = userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        log.info("Friends received: {}", friends);
        return friends;
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            log.info("Validation failed: Login cannot contain spaces");
            throw new ValidationException("Login cannot contain spaces");
        }
    }

    public User addUser(User user) {
        validate(user);
        log.info("User added: {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.getUsers().contains(user)) {
            log.info("Update failed: User not found");
            throw new DataNotFoundException("User not found");
        }
        log.info("User updated: {}", user);
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        log.info("Users received: {}", userStorage.getUsers());
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        if (!userStorage.getUsers().contains(userStorage.getUserById(id))) {
            log.info("Get user failed: User not found");
            throw new DataNotFoundException("User not found");
        }
        log.info("User by id received: {}", userStorage.getUserById(id));
        return userStorage.getUserById(id);
    }


}