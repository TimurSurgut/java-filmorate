package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
public class Film {


    private Long id;
    @NotNull(message = "У фильма должно быть имя")
    private String name;
    @NotNull(message = "У фильма должно быть описание")
    private String description;
    @NotNull(message = "У фильма должна быть указана дата релиза")
    private LocalDate releaseDate;
    @NotNull(message = "У фильма должна быть указана продолжительность")
    private Integer duration;
    @NotNull(message = "У фильма должен быть указан рейтинг MPA")
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;

    }

    public Film() {

    }
}