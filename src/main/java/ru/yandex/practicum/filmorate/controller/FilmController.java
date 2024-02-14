package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.yandex.practicum.filmorate.model.*;

@RestController
public class FilmController {
    HashMap<Integer, Film> films = new HashMap<>();
    List<Film> filmsList = new ArrayList<>();
    public static final LocalDate DEFAULT_DATE = LocalDate.of(1895, 12, 28);
    LocalDate releaseDate;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    protected String message;
    int generatorId;
    Gson gson = new Gson();

    @ResponseBody
    @GetMapping("/films")
    public ResponseEntity<?> findAllFilms() {
        log.debug("Текущее количество фильмов в фильмотеке: {}", films.size());
        filmsList.addAll(films.values());
        return new ResponseEntity<>(gson.toJson(filmsList), HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/films")
    public ResponseEntity<?> post(@RequestBody Film film) {
        releaseDate = LocalDate.parse(film.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            if (film.getName() == null || film.getName().isBlank() || film.getName().isEmpty()) {
                message = "название не может быть пустым";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (film.getDescription().length() > 200) {
                message = "максимальная длина описания — 200 символов";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (releaseDate.isBefore(DEFAULT_DATE)) {
                message = "дата релиза — не раньше 28 декабря 1895 года";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (film.getDuration() < 0) {
                message = "продолжительность фильма должна быть положительной";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (films.containsKey(film.getId())) {
                message = "Фильм с таким названием уже добавлен!";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        if (film.getId() == 0) {
            String stop = "пуск";
            while (stop.equals("пуск")) {
                generatorId++;
                if (!films.containsKey(generatorId)) {
                    film.setId(generatorId);
                }
                stop = "стоп";
            }
        }
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.OK);

    }

    @ResponseBody
    @PutMapping(value = "/films")

    public ResponseEntity<?> update(@RequestBody Film film) {
        releaseDate = LocalDate.parse(film.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            if (film.getName() == null || film.getName().isBlank() || film.getName().isEmpty()) {
                message = "название не может быть пустым";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (film.getDescription().length() > 200) {
                message = "максимальная длина описания — 200 символов";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (releaseDate.isBefore(DEFAULT_DATE)) {
                message = "дата релиза — не раньше 28 декабря 1895 года";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        try {
            if (film.getDuration() < 0) {
                message = "продолжительность фильма должна быть положительной";
                throw new ValidationException(HttpStatus.BAD_REQUEST, message);
            }
        } catch (ValidationException exception) {
            log.debug(message);
            System.out.println(message);
            return new ResponseEntity<>(gson.toJson(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновлён фильм: {}", film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            message = "Фильм с id: " + film.getId() + " не найден";
            log.debug(message);
            return new ResponseEntity<>(gson.toJson(message), HttpStatus.NOT_FOUND);
        }
    }

    public HashMap<Integer, Film> findAll() {
        log.debug("Текущее количество фильмов в фильмотеке: {}", films.size());
        return films;
    }
}
