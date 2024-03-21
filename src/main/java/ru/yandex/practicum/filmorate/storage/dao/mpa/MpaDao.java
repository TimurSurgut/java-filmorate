package ru.yandex.practicum.filmorate.storage.dao.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {


    Mpa get(int mpaID);

    Collection<Mpa> getAll();

    boolean contains(int mpaID);
}
