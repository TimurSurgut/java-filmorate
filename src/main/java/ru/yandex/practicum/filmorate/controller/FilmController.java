package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.filmStorage.InMemoryFilmStorage;


@RestController
public class FilmController {
    final InMemoryFilmStorage inMemoryFilmStorage;
    final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @ResponseBody
    @GetMapping("/films")
    public ResponseEntity<?> findAllFilms() {
        return inMemoryFilmStorage.findAllFilms();
    }

    @ResponseBody
    @PostMapping(value = "/films")
    public ResponseEntity<?> postFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.postFilm(film);
    }

    @ResponseBody
    @PutMapping(value = "/films")
    public ResponseEntity<?> updateFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @ResponseBody
    @PutMapping(value = "/films/{id}/like/{userId}")
    public ResponseEntity<?> setLikeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.setLikeFilm(id, userId);
    }

    @ResponseBody
    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public ResponseEntity<?> deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLikeFilm(id, userId);
    }

    @ResponseBody
    @GetMapping(value = "/films/popular")
    public ResponseEntity<?> getPopularFilm(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilm(count);
    }

    @ResponseBody
    @GetMapping("/films/{id}")
    public ResponseEntity<?> getFilmByID(@PathVariable int id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

}
