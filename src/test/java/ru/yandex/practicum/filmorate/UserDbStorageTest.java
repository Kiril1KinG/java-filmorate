package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;


@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testCrud() {
        User newUser = new User(1, "user@email.ru", "kirillking01", "Kirill Sharapov", LocalDate.of(2004, 7, 2));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.addUser(newUser);

        User savedUser = userStorage.getUserById(1);

        AssertionsForClassTypes.assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);

        User newUser2 = new User(2, "user2@yandex.ru", "semenchuksu", "Stanislav Semenchuk", LocalDate.of(1990, 1, 1));
        userStorage.addUser(newUser2);
        List<User> users = List.of(newUser, newUser2);
        AssertionsForClassTypes.assertThat(userStorage.getUsers())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(users);
    }
}
