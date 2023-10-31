package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        this.userController = new UserController();
    }

    @Test
    public void ValidationShouldReturnEqualsLoginAndNameIfNameIsEmpty() {
        User user1 = new User(1, "bvbc@mail.ru", "log", "", LocalDate.of(1990, 12, 12));
        this.userController.addUser(user1);
        Assertions.assertEquals(user1.getLogin(), user1.getName());
    }

    @Test
    public void ValidationShouldReturnExceptionIfLoginContainsSpaces() {
        User user1 = new User(1, "bvbc@mail.ru", "lo g", "", LocalDate.of(1990, 12, 12));
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user1));
    }

    @Test
    public void MethodAddUserShouldCreateExpectedUser() {
        User user1 = new User(1, "bvbc@mail.ru", "log", "", LocalDate.of(1990, 12, 12));
        User expected = new User(1, "bvbc@mail.ru", "log", "log", LocalDate.of(1990, 12, 12));
        Assertions.assertEquals(userController.addUser(user1), expected);
    }
}
