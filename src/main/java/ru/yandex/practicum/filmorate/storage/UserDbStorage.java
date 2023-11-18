package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

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
        List<Integer> userId = jdbcTemplate.query("SELECT * FROM \"user\" WHERE login = ?;",
                ((rs, rowNum) -> rs.getInt("user_id")), user.getLogin());
        if (userId.size() != 0) {
            throw new DataNotFoundException("Add user failed: user already exists");
        }
        jdbcTemplate.update("INSERT INTO \"user\" (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        List<Integer> id = jdbcTemplate.query("SELECT user_id FROM \"user\" WHERE login = ?",
                (rs, rowNum) -> rs.getInt("user_id"), user.getLogin());
        user.setId(id.get(0));
        return user;
    }

    @Override
    public User updateUser(User user) {
        List<Integer> userId = jdbcTemplate.query("SELECT * FROM \"user\" WHERE user_id = ?;",
                ((rs, rowNum) -> rs.getInt("user_id")), user.getId());
        if (userId.size() != 1) {
            throw new DataNotFoundException("Update user failed: user not found");
        }
        jdbcTemplate.update("UPDATE \"user\" SET email = ?, login = ?, name = ?, birthday = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM \"user\"", userRowMapper());
        enrichUsers(users);
        return users;
    }

    @Override
    public User getUserById(int id) {
        List<User> user = jdbcTemplate.query("SELECT * FROM \"user\" WHERE user_id = ?",
                userRowMapper(), id);
        enrichUsers(user);
        if (user.size() != 1) {
            throw new DataNotFoundException("Film not found: Incorrect id");
        }
        return user.get(0);
    }

    @Override
    public boolean containsUserById(int id) {
        List<Integer> user = jdbcTemplate.query("SELECT * FROM \"user\" WHERE user_id = ?",
                (rs, rowNum) -> rs.getInt("user_id"), id);
        return user.size() == 1;
    }

    private RowMapper<User> userRowMapper() {
        return ((rs, rowNum) -> new User(rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()));
    }

    private void enrichUsers(Collection<User> users) {
        for (User u : users) {
            List<Integer> friends = jdbcTemplate.query("SELECT * FROM friendship WHERE user_id = ?",
                    (rs, rowNum) -> rs.getInt("friend_id"), u.getId());
            u.setFriends(new HashSet<>(friends));
        }
    }
}
