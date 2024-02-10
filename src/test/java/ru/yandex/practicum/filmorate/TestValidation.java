package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestValidation {


    @Test
    void postFilm() {
        FilmController filmController = new FilmController();
        //Проверка добавления фильма при соблюдении правил;
        Film film = new Film(2, "Терминатор", "описание", "2000-01-22",90);
        filmController.post(film);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с тем же id;
        Film film2 = new Film(2, "Блэйд", "описание", "2000-01-22",90);
        filmController.post(film2);
        assertEquals("Терминатор", filmController.findAll().get(2).getName(),
                "Название должно быть Терминатор");
        //Проверка исключения при попытке добавить фильм без наименования;
        Film film3 = new Film(3, "", "описание", "2000-01-22",90);
        filmController.post(film3);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с превышением максимальной длины описания;
        Film film4 = new Film(4, "Блэйд", "ааааааааааааааааааааааааааааааааааааааааааааааааааааа" +
                "аааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа" +
                "ааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа", "2000-01-22",90);
        filmController.post(film4);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с датой релиза раньше 28 декабря 1895;
        Film film5 = new Film(5, "Терминатор", "описание", "1895-12-27",90);
        filmController.post(film5);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с отрицательной продолжительностью;
        Film film6 = new Film(5, "Терминатор", "описание", "2000-01-22",-90);
        filmController.post(film6);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
    }

    @Test
    void putFilm() {
        FilmController filmController = new FilmController();
        Film film = new Film(2, "Терминатор", "описание", "2000-01-22",90);
        filmController.post(film);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм без наименования;
        Film film3 = new Film(2, "", "описание", "2000-01-22",90);
        filmController.update(film3);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с превышением максимальной длины описания;
        Film film4 = new Film(2, "Блэйд", "ааааааааааааааааааааааааааааааааааааааааааааааааааааа" +
                "аааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа" +
                "ааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа", "2000-01-22",90);
        filmController.update(film4);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с датой релиза раньше 28 декабря 1895;
        Film film5 = new Film(2, "Терминатор", "описание", "1895-12-27",90);
        filmController.update(film5);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с отрицательной продолжительностью;
        Film film6 = new Film(2, "Терминатор", "описание", "2000-01-22",-90);
        filmController.update(film6);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка обновления фильма;
        Film film7 = new Film(2, "Блэйд", "описание", "2000-01-22",90);
        filmController.update(film7);
        assertEquals("Блэйд", filmController.findAll().get(2).getName(),
                "Название должно быть Блэйд");
        //Проверка обновления фильма c неверным id;
        Film film8 = new Film(5, "Блэйд", "описание", "2000-01-22",90);
        filmController.update(film8);
        assertEquals(1, filmController.findAll().size(), "Количество фильмов должно быть равно 1");
    }

    @Test
    void showAllFilms() {
        FilmController filmController = new FilmController();
        Film film = new Film(1, "Терминатор", "описание", "2000-01-22",90);
        filmController.post(film);
        Film film2 = new Film(2, "Терминатор2", "описание", "2000-01-22",90);
        filmController.post(film2);
        Film film3 = new Film(3, "Терминатор3", "описание", "2000-01-22",90);
        filmController.post(film3);
        assertEquals(3, filmController.findAll().size(), "Количество фильмов должно быть равно 3");
        System.out.println(filmController.findAll());
    }

    @Test
    void postUser() {
        UserController userController = new UserController();
        //Проверка добавления пользователя при соблюдении правил;
        User user = new User(1, "user@bestmail.ru", "login", "Иван",
                "2000-01-22");
        userController.post(user);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с почтой без символа @;
        User user2 = new User(2, "userbestmail.ru", "login", "Иван",
                "2000-01-22");
        userController.post(user2);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с незаполненной почтой;
        User user3 = new User(3, "", "login", "Иван",
                "2000-01-22");
        userController.post(user3);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с пустым логином;
        User user4 = new User(4, "user@bestmail.ru", "", "Иван",
                "2000-01-22");
        userController.post(user4);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с логином содержащий пробелы;
        User user5 = new User(5, "user@bestmail.ru", "log in", "Иван",
                "2000-01-22");
        userController.post(user5);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка замены имени на логин, если имя не введено;
        User user6 = new User(6, "user@bestmail.ru", "login", "",
                "2000-01-22");
        userController.post(user6);
        assertEquals("login", userController.findAll().get(6).getName(),
                "Имя должно быть login");
        //Проверка исключения при попытке добавить пользователя с датой рождения позже текущей;
        User user7 = new User(7, "user@bestmail.ru", "login", "Иван",
                "2025-01-22");
        userController.post(user7);
        assertEquals(2, userController.findAll().size(),
                "Количество пользователей должно быть равно 2");
        }
    @Test
    void putUser() {
        UserController userController = new UserController();
        User user = new User(1, "user@bestmail.ru", "login", "Иван",
                "2000-01-22");
        userController.post(user);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с почтой без символа @;
        User user2 = new User(1, "userbestmail.ru", "login", "Иван",
                "2000-01-22");
        userController.update(user2);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с незаполненной почтой;
        User user3 = new User(1, "", "login", "Иван",
                "2000-01-22");
        userController.update(user3);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с пустым логином;
        User user4 = new User(1, "user@bestmail.ru", "", "Иван",
                "2000-01-22");
        userController.update(user4);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с логином содержащий пробелы;
        User user5 = new User(1, "user@bestmail.ru", "log in", "Иван",
                "2000-01-22");
        userController.update(user5);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка замены имени на логин, если имя не введено;
        User user6 = new User(1, "user@bestmail.ru", "login", "",
                "2000-01-22");
        userController.update(user6);
        assertEquals("login", userController.findAll().get(1).getName(),
                "Имя должно быть login");
        //Проверка исключения при попытке добавить пользователя с датой рождения позже текущей;
        User user7 = new User(1, "user@bestmail.ru", "login", "Иван",
                "2025-01-22");
        userController.update(user7);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");
        //Проверка обновления пользователя при соблюдении правил;
        User user8 = new User(1, "user@bestmail.ru", "login", "Василий",
                "2000-01-22");
        userController.update(user8);
        assertEquals("Василий", userController.findAll().get(1).getName(),
                "Имя должно быть Василий");
        //Проверка обновления пользователя c неверным id;
        User user9 = new User(2, "user@bestmail.ru", "login", "Василий",
                "2000-01-22");
        userController.update(user9);
        assertEquals(1, userController.findAll().size(),
                "Количество пользователей должно быть равно 1");

    }
    @Test
    void showAllUsers() {
        UserController userController = new UserController();
        User user = new User(1, "user@bestmail.ru", "login", "Иван",
                "2000-01-22");
        User user2 = new User(2, "user@bestmail.ru", "login2", "Иван2",
                "2000-01-22");
        User user3 = new User(3, "user@bestmail.ru", "login3", "Иван3",
                "2000-01-22");
        userController.post(user);
        userController.post(user2);
        userController.post(user3);
        assertEquals(3, userController.findAll().size(),
                "Количество пользователей должно быть равно 3");
    }



}
