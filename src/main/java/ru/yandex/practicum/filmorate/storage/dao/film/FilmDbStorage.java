package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        log.debug("add({}).", film);
        jdbcTemplate.update(""
                        + "INSERT INTO film (name, description, release_date, duration, rating_id) "
                        + "VALUES(?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        Film result = jdbcTemplate.queryForObject(format(""
                        + "SELECT film_id, name, description, release_date, duration, rating_id "
                        + "FROM film "
                        + "WHERE name='%s' "
                        + "AND description='%s' "
                        + "AND release_date='%s' "
                        + "AND duration=%d "
                        + "AND rating_id=%d",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()), new FilmMapper());
                addGenres(film.getId(), film.getGenres());
        log.trace("В хранилище добавлен фильм: {}.", result);
        return result;
    }

    @Override
    public Film update(Film film) {
        log.debug("update({}).", film);
        jdbcTemplate.update(""
                        + "UPDATE film "
                        + "SET name=?, description=?, release_date=?, duration=?, rating_id=? "
                        + "WHERE film_id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        Film result = get(film.getId());
        result.setGenres(film.getGenres());
        updateGenres(film.getId(), film.getGenres());
        log.trace("Обновлён фильм: {}.", result);
        return result;
    }

    @Override
    public Film get(long filmID) {
        log.debug("get({}).", filmID);
        Film film = jdbcTemplate.queryForObject(format(""
                + "SELECT film_id, name, description, release_date, duration, rating_id FROM film "
                + "WHERE film_id=%d", filmID), new FilmMapper());
                Mpa mpa = jdbcTemplate.queryForObject(format(""
                + "SELECT rating_id, name "
                + "FROM rating "
                + "WHERE rating_id=%d", film.getMpa().getId()), new MpaMapper());
        film.setMpa(mpa);
        film.setGenres(getGenres(filmID));
        log.trace("Возвращён фильм: {}", film);
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        log.debug("getAll().");
        List<Film> films = jdbcTemplate.query(""
                + "SELECT film_id, name, description, release_date, duration, rating_id "
                + "FROM film", new FilmMapper());
        log.trace("Возвращены все фильмы: {}.", films);
        return films;
    }

    @Override
    public boolean contains(long filmID) {
        log.debug("contains({}).", filmID);
        try {
            get(filmID);
            log.trace("Найден фильм ID_{}.", filmID);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            log.trace("Не найден фильм ID_{}.", filmID);
            return false;
        }
    }

    @Override
    public void addGenres(long filmID, List<Genre> genres) {
        log.debug("add({}, {})", filmID, genres);
        for (Genre genre : genres) {
            jdbcTemplate.update(""
                    + "INSERT INTO genre_film (film_id, genre_id) "
                    + "VALUES (?, ?)", filmID, genre.getId());
            log.trace("Фильму ID_{} добавлен жанр ID_{}.", filmID, genre.getId());
        }
    }

    @Override
    public void updateGenres(long filmID, List<Genre> genres) {
        log.debug("update({}, {})", filmID, genres);
        delete(filmID);
        addGenres(filmID, genres);
    }

    @Override
    public List<Genre> getGenres(long filmID) {
        log.debug("getGenres({}).", filmID);
        List<Genre> genres = new ArrayList<>(jdbcTemplate.query(format(""
                + "SELECT f.genre_id, g.name "
                + "FROM genre_film AS f "
                + "LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id "
                + "WHERE f.film_id=%d "
                + "ORDER BY g.genre_id ASC", filmID), new GenreMapper()));
        log.trace("Возвращены все жанры для фильма ID_{}: {}.", filmID, genres);
        return genres;
    }

    private void delete(long filmID) {
        log.debug("delete({}).", filmID);
        jdbcTemplate.update(""
                + "DELETE "
                + "FROM genre_film "
                + "WHERE film_id=?", filmID);
        log.debug("Удалены все жанры у фильма {}.", filmID);
    }
}

