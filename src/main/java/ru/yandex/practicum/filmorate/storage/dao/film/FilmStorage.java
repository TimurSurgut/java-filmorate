package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {


    Film add(Film film);

    Film update(Film film);

    Film get(long filmID);

    Collection<Film> getAll();

    boolean contains(long filmID);

    void addGenres(long filmID, List<Genre> genres);

    void updateGenres(long filmID, List<Genre> genres);

    List<Genre> getGenres(long filmID);
}
