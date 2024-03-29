package ru.yandex.practicum.filmorate.storage.userStorage;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    public HashMap<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ru.yandex.practicum.filmorate.controller.FilmController.class);
    protected String message;
    int generatorId;
    Gson gson = new Gson();

    @Override
    public ResponseEntity<?> findAllUsers() {
        List<User> usersList = new ArrayList<>();
        log.debug("Текущее количество пользователей: {}", users.size());
        usersList.addAll(users.values());
        return new ResponseEntity<>(gson.toJson(usersList), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> postUser(User user) {
        try {
            if (user.getEmail() == null || user.getEmail().indexOf("@") == -1) {
                message = "электронная почта не может быть пустой и должна содержать символ @";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                message = "логин не может быть пустым и содержать пробелы";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        try {
            if (LocalDate.now().isBefore(LocalDate.parse(user.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                message = "дата рождения не может быть в будущем";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (users.containsKey(user.getId())) {
                message = "Пользователь с таким Id уже добавлен!";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        if (user.getId() == 0) {
            String stop = "пуск";
            while (stop.equals("пуск")) {
                generatorId++;
                if (!users.containsKey(generatorId)) {
                    user.setId(generatorId);
                }
                stop = "стоп";
            }
        }
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateUser(User user) {
        try {
            if (user.getEmail() == null || user.getEmail().indexOf("@") == -1) {
                message = "электронная почта не может быть пустой и должна содержать символ @";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                message = "логин не может быть пустым и содержать пробелы";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        try {
            if (LocalDate.now().isBefore(LocalDate.parse(user.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                message = "дата рождения не может быть в будущем";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Обновлён пользователь: {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            message = "Пользователь с id: " + user.getId() + " не найден";
            log.debug(message);
            return new ResponseEntity<>(gson.toJson(message), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public HashMap<Integer, User> findAll() {
        return users;
    }

    @Override
    public ResponseEntity<?> getUserById(int id) {
        if (users.containsKey(id)) {
            return new ResponseEntity<>(gson.toJson(users.get(id)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(gson.toJson("Пользователь с id" + id + " не найден"), HttpStatus.NOT_FOUND);
        }
    }
}
