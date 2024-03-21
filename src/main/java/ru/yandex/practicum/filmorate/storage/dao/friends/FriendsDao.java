package ru.yandex.practicum.filmorate.storage.dao.friends;

import java.util.Collection;

public interface FriendsDao {


    void add(long fromUserID, long toUserID, boolean isMutual);

    void delete(long fromUserID, long toUserID);

    Collection<Long> getUserIds(long friendId);

    boolean contains(long fromUserID, long toUserID);
}
