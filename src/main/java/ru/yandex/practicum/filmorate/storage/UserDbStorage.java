package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        if (containsUserById(user.getId())) {
            throw new DataNotFoundException("Add user failed: user already exists");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement("INSERT INTO \"user\" (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        try {
            Integer userId = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM \"user\" WHERE user_id = ?;",
                    Integer.class, user.getId());
            jdbcTemplate.update("UPDATE \"user\" SET email = ?, login = ?, name = ?, birthday = ?",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Update user failed: user not found");
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM \"user\"", this::mapUser);
        enrichUsers(users);
        return users;
    }

    @Override
    public User getUserById(int id) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE user_id = ?", this::mapUser, id);
            enrichUsers(List.of(user));
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("User not found: Incorrect id");
        }
    }

    @Override
    public boolean containsUserById(int id) {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM \"user\" WHERE user_id = ?", Long.class, id);
            return count == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private User mapUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private void enrichUsers(Collection<User> users) {
        for (User u : users) {
            List<Integer> friends = jdbcTemplate.query("SELECT * FROM friendship WHERE user_id = ?",
                    (rs, rowNum) -> rs.getInt("friend_id"), u.getId());
            u.setFriends(new HashSet<>(friends));
        }
    }

    public void addFriend(int userId, int friendId) {
        try {
            Integer friendship = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?",
                    Integer.class, userId, friendId);
            if (isMutualFriendship(userId, friendId)) {
                jdbcTemplate.update("UPDATE friendship SET friendship_status = ? ", isMutualFriendship(userId, friendId));
            }
            jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, friendship_status) VALUES (?, ?, ?)",
                    userId, friendId, isMutualFriendship(userId, friendId));
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Add friend failed: friendship already exists");
        }
    }

    public void deleteFriend(int userId, int friendId) {
        if (isMutualFriendship(userId, friendId)) {
            jdbcTemplate.update("UPDATE friendship SET friendship_status = ? WHERE user_id = ?", false, friendId);
        }
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?", userId, friendId);


    }

    private boolean isMutualFriendship(int userId, int friendId) {
        Integer mutualFriendship = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?",
                Integer.class, friendId, userId);
        return mutualFriendship != null;

    }
}
