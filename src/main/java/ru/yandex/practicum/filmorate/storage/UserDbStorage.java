package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
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
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM \"user\" WHERE user_id = ?", Long.class, id);
        return count == 1;
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
