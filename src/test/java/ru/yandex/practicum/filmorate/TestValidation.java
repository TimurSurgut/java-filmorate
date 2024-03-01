package ru.yandex.practicum.filmorate;


import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.filmStorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestValidation {
    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService();
    FilmService filmService;

    Gson gson = new Gson();

    @Test
    void postFilmFilm() {

        FilmController filmController = new FilmController(inMemoryFilmStorage, filmService);
        //Проверка добавления фильма при соблюдении правил;
        Film film = new Film(2, "Терминатор", "описание", "2000-01-22", 90);
        filmController.postFilm(film);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с тем же id;
        Film film2 = new Film(2, "Блэйд", "описание", "2000-01-22", 90);
        filmController.postFilm(film2);
        assertEquals("Терминатор", inMemoryFilmStorage.findAll().get(2).getName(), "Название должно быть Терминатор");
        //Проверка исключения при попытке добавить фильм без наименования;
        Film film3 = new Film(3, "", "описание", "2000-01-22", 90);
        filmController.postFilm(film3);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с превышением максимальной длины описания;
        Film film4 = new Film(4, "Блэйд", "ааааааааааааааааааааааааааааааааааааааааааааааааааааа" + "аааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа" + "ааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа", "2000-01-22", 90);
        filmController.postFilm(film4);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с датой релиза раньше 28 декабря 1895;
        Film film5 = new Film(5, "Терминатор", "описание", "1895-12-27", 90);
        filmController.postFilm(film5);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с отрицательной продолжительностью;
        Film film6 = new Film(5, "Терминатор", "описание", "2000-01-22", -90);
        filmController.postFilm(film6);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
    }

    @Test
    void putFilm() {
        FilmController filmController = new FilmController(inMemoryFilmStorage, filmService);
        Film film = new Film(2, "Терминатор", "описание", "2000-01-22", 90);
        filmController.postFilm(film);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм без наименования;
        Film film3 = new Film(2, "", "описание", "2000-01-22", 90);
        filmController.updateFilm(film3);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с превышением максимальной длины описания;
        Film film4 = new Film(2, "Блэйд", "ааааааааааааааааааааааааааааааааааааааааааааааааааааа" + "аааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа" + "ааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа", "2000-01-22", 90);
        filmController.updateFilm(film4);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с датой релиза раньше 28 декабря 1895;
        Film film5 = new Film(2, "Терминатор", "описание", "1895-12-27", 90);
        filmController.updateFilm(film5);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка исключения при попытке добавить фильм с отрицательной продолжительностью;
        Film film6 = new Film(2, "Терминатор", "описание", "2000-01-22", -90);
        filmController.updateFilm(film6);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
        //Проверка обновления фильма;
        Film film7 = new Film(2, "Блэйд", "описание", "2000-01-22", 90);
        filmController.updateFilm(film7);
        assertEquals("Блэйд", inMemoryFilmStorage.findAll().get(2).getName(), "Название должно быть Блэйд");
        //Проверка обновления фильма c неверным id;
        Film film8 = new Film(5, "Блэйд", "описание", "2000-01-22", 90);
        filmController.updateFilm(film8);
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 1");
    }

    @Test
    void showAllFilms() {
        FilmController filmController = new FilmController(inMemoryFilmStorage, filmService);
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        Film film = new Film(1, "Терминатор", "описание", "2000-01-22", 90);
        filmController.postFilm(film);
        Film film2 = new Film(2, "Терминатор2", "описание", "2000-01-22", 90);
        filmController.postFilm(film2);
        Film film3 = new Film(3, "Терминатор3", "описание", "2000-01-22", 90);
        filmController.postFilm(film3);
        assertEquals(3, inMemoryFilmStorage.findAll().size(), "Количество фильмов должно быть равно 3");
        System.out.println(inMemoryFilmStorage.findAll());
    }

    @Test
    void postFilmUser() {
        UserController userController = new UserController(inMemoryUserStorage, userService);
        //Проверка добавления пользователя при соблюдении правил;
        User user = new User(1, "user@bestmail.ru", "login", "Иван", "2000-01-22");
        userController.postUser(user);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с почтой без символа @;
        User user2 = new User(2, "userbestmail.ru", "login", "Иван", "2000-01-22");
        userController.postUser(user2);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с незаполненной почтой;
        User user3 = new User(3, "", "login", "Иван", "2000-01-22");
        userController.postUser(user3);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с пустым логином;
        User user4 = new User(4, "user@bestmail.ru", "", "Иван", "2000-01-22");
        userController.postUser(user4);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с логином содержащий пробелы;
        User user5 = new User(5, "user@bestmail.ru", "log in", "Иван", "2000-01-22");
        userController.postUser(user5);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка замены имени на логин, если имя не введено;
        User user6 = new User(6, "user@bestmail.ru", "login", "", "2000-01-22");
        userController.postUser(user6);
        assertEquals("login", inMemoryUserStorage.findAll().get(6).getName(), "Имя должно быть login");
        //Проверка исключения при попытке добавить пользователя с датой рождения позже текущей;
        User user7 = new User(7, "user@bestmail.ru", "login", "Иван", "2025-01-22");
        userController.postUser(user7);
        assertEquals(2, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 2");

    }

    @Test
    void putUser() {
        UserController userController = new UserController(inMemoryUserStorage, userService);
        User user = new User(1, "user@bestmail.ru", "login", "Иван", "2000-01-22");
        userController.postUser(user);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с почтой без символа @;
        User user2 = new User(1, "userbestmail.ru", "login", "Иван", "2000-01-22");
        userController.updateUser(user2);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с незаполненной почтой;
        User user3 = new User(1, "", "login", "Иван", "2000-01-22");
        userController.updateUser(user3);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с пустым логином;
        User user4 = new User(1, "user@bestmail.ru", "", "Иван", "2000-01-22");
        userController.updateUser(user4);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка исключения при попытке добавить пользователя с логином содержащий пробелы;
        User user5 = new User(1, "user@bestmail.ru", "log in", "Иван", "2000-01-22");
        userController.updateUser(user5);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка замены имени на логин, если имя не введено;
        User user6 = new User(1, "user@bestmail.ru", "login", "", "2000-01-22");
        userController.updateUser(user6);
        assertEquals("login", inMemoryUserStorage.findAll().get(1).getName(), "Имя должно быть login");
        //Проверка исключения при попытке добавить пользователя с датой рождения позже текущей;
        User user7 = new User(1, "user@bestmail.ru", "login", "Иван", "2025-01-22");
        userController.updateUser(user7);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");
        //Проверка обновления пользователя при соблюдении правил;
        User user8 = new User(1, "user@bestmail.ru", "login", "Василий", "2000-01-22");
        userController.updateUser(user8);
        assertEquals("Василий", inMemoryUserStorage.findAll().get(1).getName(), "Имя должно быть Василий");
        //Проверка обновления пользователя c неверным id;
        User user9 = new User(2, "user@bestmail.ru", "login", "Василий", "2000-01-22");
        userController.updateUser(user9);
        assertEquals(1, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 1");

    }

    @Test
    void showAllUsers() {
        UserController userController = new UserController(inMemoryUserStorage, userService);
        User user = new User(1, "user@bestmail.ru", "login", "Иван", "2000-01-22");
        User user2 = new User(2, "user@bestmail.ru", "login2", "Иван2", "2000-01-22");
        User user3 = new User(3, "user@bestmail.ru", "login3", "Иван3", "2000-01-22");
        userController.postUser(user);
        userController.postUser(user2);
        userController.postUser(user3);
        assertEquals(3, inMemoryUserStorage.findAll().size(), "Количество пользователей должно быть равно 3");
    }

    @Test
    void getUserByID() {
        UserController userController = new UserController(inMemoryUserStorage, userService);
        User user = new User(1, "user@bestmail.ru", "login", "Иван", "2000-01-22");
        User user2 = new User(2, "user@bestmail.ru", "login2", "Иван2", "2000-01-22");
        User user3 = new User(3, "user@bestmail.ru", "login3", "Иван3", "2000-01-22");
        userController.postUser(user);
        userController.postUser(user2);
        userController.postUser(user3);
        assertEquals(new ResponseEntity<>(gson.toJson(user), HttpStatus.OK), userController.getUserByID(1), "");
    }

    @Test
    void addFriend() {
        UserController userController = new UserController(inMemoryUserStorage, userService);
        User user = new User(1, "user@bestmail.ru", "login", "Иван", "2000-01-22");
        User user2 = new User(2, "user@bestmail.ru", "login2", "Иван2", "2000-01-22");
        User user3 = new User(3, "user@bestmail.ru", "login3", "Иван3", "2000-01-22");
        userController.postUser(user);
        userController.postUser(user2);
        userController.postUser(user3);
        assertEquals(new ResponseEntity<>(gson.toJson("Пользователь с id " + 2 + " успешно добавлен в друзья"), HttpStatus.OK), userService.addFriend(1, 2), "");
    }


}
