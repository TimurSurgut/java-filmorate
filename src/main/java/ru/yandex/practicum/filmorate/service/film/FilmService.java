package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Film add(Film film);

    Film update(Film film);

    Film get(long filmID);

    Collection<Film> getAll();

    Collection<Film> getPopularFilms(int count);

    void addLike(long filmID, long userID);

    void deleteLike(long filmID, long userID);
}