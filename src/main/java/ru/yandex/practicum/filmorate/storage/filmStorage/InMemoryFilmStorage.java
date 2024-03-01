package ru.yandex.practicum.filmorate.storage.filmStorage;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    HashMap<Integer, Film> films = new HashMap<>();
    public static final LocalDate DEFAULT_DATE = LocalDate.of(1895, 12, 28);
    LocalDate releaseDate;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    protected String message;
    int generatorId;
    Gson gson = new Gson();

    @Override
    public ResponseEntity<?> findAllFilms() {
        log.debug("Текущее количество фильмов в фильмотеке: {}", films.size());
        List<Film> filmsList = new ArrayList<>(films.values());
        return new ResponseEntity<>(gson.toJson(filmsList), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> postFilm(Film film) {
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


    @Override
    public ResponseEntity<?> updateFilm(Film film) {
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

    @Override
    public HashMap<Integer, Film> findAll() {
        log.debug("Текущее количество фильмов в фильмотеке: {}", films.size());
        return films;
    }

    @Override
    public ResponseEntity<?> getFilmById(int id) {
        if (films.containsKey(id)) {
            return new ResponseEntity<>(gson.toJson(films.get(id)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(gson.toJson("Пользователь с id" + id + " не найден"), HttpStatus.NOT_FOUND);
        }
    }
}
