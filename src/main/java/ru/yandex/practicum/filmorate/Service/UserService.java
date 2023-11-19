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
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Add friend failed: Incorrect friend id");
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new DataNotFoundException("Add friend failed: Incorrect user id");
        }
        userStorage.addFriend(userId, friendId);
        User u = userStorage.getUserById(userId);
        log.info("Friend added: {}", u);
        return u;
    }

    public User deleteFriend(int userId, int friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Delete friend failed: Incorrect friend id");
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new DataNotFoundException("Delete friend failed: Incorrect user id");
        }
        userStorage.deleteFriend(userId, friendId);
        User u = userStorage.getUserById(userId);
        log.info("Friend deleted: {}", u);
        return u;
    }

    public List<User> getMutualFriends(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        List<Integer> mutualFriendsIds = new ArrayList<>(user.getFriends());
        mutualFriendsIds.retainAll(friend.getFriends());
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
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Validation failed: Login cannot contain spaces");
        }
    }

    public User addUser(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User u = userStorage.addUser(user);
        log.info("User added: {}", u);
        return u;
    }

    public User updateUser(User user) {
        if (!userStorage.containsUserById(user.getId())) {
            throw new DataNotFoundException("Update failed: User not found");
        }
        User u = userStorage.updateUser(user);
        log.info("User updated: {}", u);
        return u;
    }

    public List<User> getUsers() {
        List<User> users = userStorage.getUsers();
        log.info("Users received: {}", users);
        return users;
    }

    public User getUserById(int id) {
        if (!userStorage.containsUserById(id)) {
            throw new DataNotFoundException("Get user failed: User not found");
        }
        User u = userStorage.getUserById(id);
        log.info("User by id received: {}", u);
        return u;
    }


}