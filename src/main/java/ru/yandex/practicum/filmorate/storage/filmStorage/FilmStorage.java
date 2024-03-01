package ru.yandex.practicum.filmorate.storage.filmStorage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;


public interface FilmStorage {


    public ResponseEntity<?> findAllFilms();

    public ResponseEntity<?> postFilm(Film film);

    public ResponseEntity<?> updateFilm(Film film);

    HashMap<Integer, Film> findAll();

    ResponseEntity<?> getFilmById(int id);
}
