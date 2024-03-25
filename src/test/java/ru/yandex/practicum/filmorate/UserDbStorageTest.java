package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserByIdAndUpdate() {
        User newUser = new User(1L, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUserforUpdate = new User(1L, "userForUpdate@email.ru", "userForUpdate", "userForUpdate", LocalDate.of(1995, 5, 5));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);
        User savedUser = userStorage.get(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
        User savedUser1 = userStorage.update(newUserforUpdate);


        assertThat(savedUser1)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUserforUpdate);

    }


}
