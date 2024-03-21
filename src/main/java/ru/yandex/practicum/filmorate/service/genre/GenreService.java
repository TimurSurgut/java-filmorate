package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;


public interface GenreService {


    Genre get(long genreID);

    Collection<Genre> getAll();
}
