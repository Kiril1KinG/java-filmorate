package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class FriendshipDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void addFriend(int userId, int friendId) {
        List<Integer> friendship = jdbcTemplate.query("SELECT * FROM friendship WHERE user_id = ? AND friend_id = ?",
                (rs, rowNum) -> rs.getInt("user_id"),
                userId, friendId);
        if (friendship.size() != 0) {
            throw new RuntimeException("Add friend failed: friendship already exists");
        }
        if (isMutualFriendship(userId, friendId)) {
            jdbcTemplate.update("UPDATE friendship SET friendship_status = ? ", isMutualFriendship(userId, friendId));
        }
        jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, friendship_status) VALUES (?, ?, ?)",
                userId, friendId, isMutualFriendship(userId, friendId));
    }

    public void deleteFriend(int userId, int friendId) {
        if (isMutualFriendship(userId, friendId)) {
            jdbcTemplate.update("UPDATE friendship SET friendship_status = ? WHERE user_id = ?", false, friendId);
        }
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?", userId, friendId);


    }

    private boolean isMutualFriendship(int userId, int friendId) {
        List<Integer> mutualFriendship = jdbcTemplate.query("SELECT * FROM friendship WHERE user_id = ? AND friend_id = ?",
                (rs, rowNum) -> rs.getInt("user_id"),
                friendId, userId);
        return mutualFriendship.size() == 1;

    }
}
