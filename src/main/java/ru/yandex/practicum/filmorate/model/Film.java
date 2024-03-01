package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;


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
    private HashSet<Integer> like = new HashSet<>();
    private Integer likeQuantity;

    public Film(int id, String name, String description, String releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film() {
    }

    public Integer getLikeQuantity() {
        setLikeQuantity(getLike().size());
        return getLike().size();
    }
}
