package ru.yandex.practicum.filmorate.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class FilmService {
    final FilmStorage filmStorage;
    final UserStorage userStorage;
    Gson gson = new Gson();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public ResponseEntity<?> setLikeFilm(int idFilm, int idUser) {
        if (!filmStorage.findAll().containsKey(idFilm)) {
            return new ResponseEntity<>(gson.toJson("Фильм с id " + idFilm + " не найден"), HttpStatus.NOT_FOUND);
        } else if (!userStorage.findAll().containsKey(idUser)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + idUser + " не зарегистрирован"), HttpStatus.NOT_FOUND);
        } else {
            filmStorage.findAll().get(idFilm).getLike().add(idUser);
            filmStorage.findAll().get(idFilm).getLikeQuantity();
            return new ResponseEntity<>(gson.toJson("Лайк поставлен"), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> deleteLikeFilm(int idFilm, int idUser) {
        if (!filmStorage.findAll().containsKey(idFilm)) {
            return new ResponseEntity<>
                    (gson.toJson("Фильм с id " + idFilm + " не найден"), HttpStatus.NOT_FOUND);
        } else if (!userStorage.findAll().containsKey(idUser)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + idUser + " не зарегистрирован"), HttpStatus.NOT_FOUND);
        } else if (!filmStorage.findAll().get(idFilm).getLike().contains(idUser)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + idUser + " не ставил лайк фильму с id " + idFilm),
                            HttpStatus.NOT_FOUND);
        } else {
            filmStorage.findAll().get(idFilm).getLike().remove(idUser);
            return new ResponseEntity<>(gson.toJson("Лайк удалён"), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getPopularFilm(int count) {
        ArrayList<Film> films = new ArrayList<>(filmStorage.findAll().values());
        ArrayList<Film> sortedFilm = films.stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity<>(gson.toJson(sortedFilm), HttpStatus.OK);
    }

    private int compare(Film p0, Film p1) {
        return p0.getLikeQuantity().compareTo(p1.getLikeQuantity()) * -1;
    }
}


