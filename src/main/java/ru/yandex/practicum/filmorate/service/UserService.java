package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
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
        User user = userStorage.getUserById(userId);
        addFeed(userId, friendId, EventType.FRIEND, Operation.ADD);
        log.info("Friend added: {}", user);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Delete friend failed: Incorrect friend id");
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new DataNotFoundException("Delete friend failed: Incorrect user id");
        }
        userStorage.deleteFriend(userId, friendId);
        User user = userStorage.getUserById(userId);
        addFeed(userId, friendId, EventType.FRIEND, Operation.REMOVE);
        log.info("Friend deleted: {}", user);
        return user;
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
        if (userStorage.containsUserById(user.getId())) {
            throw new DataNotFoundException("Add user failed: user already exists");
        }
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User resultUser = userStorage.addUser(user);
        log.info("User added: {}", resultUser);
        return resultUser;
    }

    public User updateUser(User user) {
        if (!userStorage.containsUserById(user.getId())) {
            throw new DataNotFoundException("Update failed: User not found");
        }
        User resultUser = userStorage.updateUser(user);
        log.info("User updated: {}", resultUser);
        return resultUser;
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
        User resultUser = userStorage.getUserById(id);
        log.info("User by id received: {}", resultUser);
        return resultUser;
    }

    public void deleteUser(int userId) {
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Delete user failed: User not found");
        }
        userStorage.deleteUser(userId);
        log.info("User deleted: {}", userId);
    }

    public List<Feed> getUserFeeds(int id) {
        if (!userStorage.containsUserById(id)) {
            throw new DataNotFoundException("Get user feeds failed: Incorrect id");
        }
        return userStorage.getUserFeeds(id);
    }

    public void addFeed(int userId, int entityId, EventType eventType, Operation operation) {
        userStorage.addFeed(userId, entityId, eventType, operation);
    }


}