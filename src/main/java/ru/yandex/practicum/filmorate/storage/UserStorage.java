package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(int id);

    boolean containsUserById(int id);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<Feed> getUserFeeds(int id);

    void addFeed(int userId, int entityId, EventType eventType, Operation operation);

    void deleteUser(int userId);
}
