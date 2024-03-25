package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.exception.storage.dao.mpa.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;


public interface MpaService {

    Mpa get(long mpaID) throws MpaNotFoundException;

    Collection<Mpa> getAll();
}
