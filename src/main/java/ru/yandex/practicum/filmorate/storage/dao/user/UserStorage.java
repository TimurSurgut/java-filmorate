package ru.yandex.practicum.filmorate.storage.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserStorage {
    User add(User user);

    User update(User user);

    User get(long userID);

    Collection<User> getAll();

    boolean contains(long userID);
}
