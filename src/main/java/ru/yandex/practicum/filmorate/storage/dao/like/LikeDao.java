package ru.yandex.practicum.filmorate.storage.dao.like;

public interface LikeDao {


    void add(long filmID, long userID);

    void delete(long filmID, long userID);

    int count(long filmID);

    boolean contains(long filmID, long userID);
}
