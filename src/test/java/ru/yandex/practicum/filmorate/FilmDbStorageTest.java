package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDaoImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmByIdAndUpdate() {
        MpaDaoImpl mpaDao = new MpaDaoImpl(jdbcTemplate);
        Mpa mpa = mpaDao.get(1);
        Mpa mpaUpdate = mpaDao.get(2);
        GenreDaoImpl genreDao = new GenreDaoImpl(jdbcTemplate);
        Genre genre1 = genreDao.get(1);
        Genre genre2 = genreDao.get(2);
        Genre genre3 = genreDao.get(3);
        Genre genre4 = genreDao.get(4);
        List<Genre> genres = new ArrayList<>();
        List<Genre> genresUpdate = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        genresUpdate.add(genre3);
        genresUpdate.add(genre4);
        Film newFilm = new Film(1L, "terminator", "normFilm", LocalDate.parse("2000-10-01"), 120, mpa, genres);
        Film updateFilm = new Film(1L, "update", "update", LocalDate.parse("2020-10-01"), 100, mpaUpdate, genresUpdate);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        filmStorage.add(newFilm);

        Film savedFilm = filmStorage.get(1);
        savedFilm.setGenres(newFilm.getGenres());

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
        filmStorage.update(updateFilm);

        Film savedFilm1 = filmStorage.get(1);
        assertThat(savedFilm1)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateFilm);
    }


}