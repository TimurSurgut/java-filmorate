package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.storage.dao.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.storage.dao.mpa.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.storage.film.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.storage.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.storage.film.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.storage.film.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.storage.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.storage.user.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.exception.storage.dao.genre.GenreNotFoundException.GENRE_NOT_FOUND;
import static ru.yandex.practicum.filmorate.exception.storage.dao.mpa.MpaNotFoundException.MPA_NOT_FOUND;
import static ru.yandex.practicum.filmorate.exception.storage.film.FilmAlreadyExistsException.FILM_ALREADY_EXISTS;
import static ru.yandex.practicum.filmorate.exception.storage.film.FilmNotFoundException.FILM_NOT_FOUND;
import static ru.yandex.practicum.filmorate.exception.storage.film.LikeAlreadyExistsException.LIKE_ALREADY_EXISTS;
import static ru.yandex.practicum.filmorate.exception.storage.film.LikeNotFoundException.LIKE_NOT_FOUND;
import static ru.yandex.practicum.filmorate.exception.storage.user.UserNotFoundException.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmDbService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDao genreDao;
    private final LikeDao likeDao;
    private final MpaDao mpaDao;

    @Override
    public Film add(Film film) {
        checkFilmToAdd(film);
        Film result = filmStorage.add(film);
        filmStorage.addGenres(result.getId(), film.getGenres());
        result.setGenres(filmStorage.getGenres(result.getId()));
        return result;
    }

    @Override
    public Film update(Film film) {
        checkFilmToUpdate(film);
        Film result = filmStorage.update(film);
        filmStorage.updateGenres(result.getId(), film.getGenres());
        result.setGenres(filmStorage.getGenres(result.getId()));
        result.setMpa(mpaDao.get(result.getMpa().getId()));
        return result;
    }

    @Override
    public Film get(long filmID) {
        if (!filmStorage.contains(filmID)) {
            log.warn("Не удалось вернуть фильм: {}.", String.format(FILM_NOT_FOUND, filmID));
            throw new FilmNotFoundException(String.format(FILM_NOT_FOUND, filmID));
        }
        Film film = filmStorage.get(filmID);
        film.setGenres(filmStorage.getGenres(filmID));
        film.setMpa(mpaDao.get(film.getMpa().getId()));
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        var films = filmStorage.getAll();
        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()));
            film.setMpa(mpaDao.get(film.getMpa().getId()));
        }
        return films;
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        log.debug("getPopularFilms({}).", count);
        List<Film> popularFilms = filmStorage.getAll().stream()
                .sorted(this::likeCompare)
                .limit(count)
                .collect(Collectors.toList());
        log.trace("Возвращены популярные фильмы: {}.", popularFilms);
        return popularFilms;
    }

    @Override
    public void addLike(long filmID, long userID) {
        checkLikeToAdd(filmID, userID);
        likeDao.add(filmID, userID);
    }

    @Override
    public void deleteLike(long filmID, long userID) {
        checkLikeToDelete(filmID, userID);
        likeDao.delete(filmID, userID);
    }


    private int likeCompare(Film film, Film otherFilm) {
        return Integer.compare(likeDao.count(otherFilm.getId()), likeDao.count(film.getId()));
    }


    private void checkFilmToAdd(Film film) {
        final LocalDate DEFAULT_DATE = LocalDate.of(1895, 12, 28);
        LocalDate releaseDate;
        String message;
        log.debug("checkFilmToAdd({}).", film);
        String msg = "Не удалось добавить фильм: {}.";
        if (film.getId() != null) {
            if (filmStorage.contains(film.getId())) {
                log.warn(msg, String.format(FILM_ALREADY_EXISTS, film.getId()));
                throw new FilmAlreadyExistsException(String.format(FILM_ALREADY_EXISTS, film.getId()));
            } else {
                log.warn(msg, "Запрещено устанавливать ID вручную");
                throw new IllegalArgumentException("Запрещено устанавливать ID вручную");
            }
        }
        if (!mpaDao.contains(film.getMpa().getId())) {
            log.warn(msg, String.format(MPA_NOT_FOUND, film.getMpa().getId()));
            throw new MpaNotFoundException(String.format(MPA_NOT_FOUND, film.getMpa().getId()));
        }
        for (Genre genre : film.getGenres()) {
            if (!genreDao.contains(genre.getId())) {
                log.warn(msg, String.format(GENRE_NOT_FOUND, genre.getId()));
                throw new GenreNotFoundException(String.format(GENRE_NOT_FOUND, genre.getId()));
            }
        }
        releaseDate = film.getReleaseDate();

        if (film.getName() == null || film.getName().isBlank() || film.getName().isEmpty()) {
            message = "название не может быть пустым";
            throw new ValidationException(HttpStatus.BAD_REQUEST, message);
        }

        if (film.getDescription().length() > 200) {
            message = "максимальная длина описания — 200 символов";
            throw new ValidationException(HttpStatus.BAD_REQUEST, message);
        }

        if (releaseDate.isBefore(DEFAULT_DATE)) {
            message = "дата релиза — не раньше 28 декабря 1895 года";
            throw new ValidationException(HttpStatus.BAD_REQUEST, message);
        }

        if (film.getDuration() < 0) {
            message = "продолжительность фильма должна быть положительной";
            throw new ValidationException(HttpStatus.BAD_REQUEST, message);
        }

    }


    private void checkFilmToUpdate(Film film) {
        log.debug("checkFilmToUpdate({}).", film);
        String msg = "Не удалось обновить фильм: {}.";
        if (!filmStorage.contains(film.getId())) {
            log.warn(msg, String.format(FILM_NOT_FOUND, film.getId()));
            throw new FilmNotFoundException(String.format(FILM_NOT_FOUND, film.getId()));
        }
        if (!mpaDao.contains(film.getMpa().getId())) {
            log.warn(msg, String.format(MPA_NOT_FOUND, film.getMpa().getId()));
            throw new MpaNotFoundException(String.format(MPA_NOT_FOUND, film.getMpa().getId()));
        }
        for (Genre genre : film.getGenres()) {
            if (!genreDao.contains(genre.getId())) {
                log.warn(msg, String.format(GENRE_NOT_FOUND, genre.getId()));
                throw new GenreNotFoundException(String.format(GENRE_NOT_FOUND, genre.getId()));
            }
        }
    }


    private void checkLikeToAdd(long filmID, long userID) {
        log.debug("checkLikeToAdd({}, {}).", filmID, userID);
        String msg = "Не удалось добавить лайк: {}.";
        if (!filmStorage.contains(filmID)) {
            log.warn(msg, String.format(FILM_NOT_FOUND, filmID));
            throw new FilmNotFoundException(String.format(FILM_NOT_FOUND, filmID));
        }
        if (!userStorage.contains(userID)) {
            log.warn(msg, String.format(USER_NOT_FOUND, userID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userID));
        }
        if (likeDao.contains(filmID, userID)) {
            log.warn(msg, String.format(LIKE_ALREADY_EXISTS, filmID, userID));
            throw new LikeAlreadyExistsException(String.format(LIKE_ALREADY_EXISTS, filmID, userID));
        }
    }

    private void checkLikeToDelete(long filmID, long userID) {
        log.debug("checkLikeToDelete({}, {}).", filmID, userID);
        String msg = "Не удалось удалить лайк: {}.";
        if (!filmStorage.contains(filmID)) {
            log.warn(msg, String.format(FILM_NOT_FOUND, filmID));
            throw new FilmNotFoundException(String.format(FILM_NOT_FOUND, filmID));
        }
        if (!userStorage.contains(userID)) {
            log.warn(msg, String.format(USER_NOT_FOUND, userID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userID));
        }
        if (!likeDao.contains(filmID, userID)) {
            log.warn(msg, String.format(LIKE_NOT_FOUND, filmID, userID));
            throw new LikeNotFoundException(String.format(LIKE_NOT_FOUND, filmID, userID));
        }
    }
}