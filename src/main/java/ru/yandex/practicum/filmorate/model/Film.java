package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;


/**
 * Film.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class Film {
    int id = 0;
    String name;
    String description;
    String releaseDate;
    int duration;
    HashSet<Integer> like = new HashSet<>();
    Integer likeQuantity;

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
