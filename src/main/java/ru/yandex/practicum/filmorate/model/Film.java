package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;


/**
 * Film.
 */
@Data
public class Film {
    private int id = 0;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private String releaseDate;
    @NonNull
    private int duration;

    public Film(int id, String name, String description, String releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film() {
    }


}
