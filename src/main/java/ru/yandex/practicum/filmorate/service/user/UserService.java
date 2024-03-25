package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {


    User add(User user);

    User update(User user);

    User get(long userID);

    Collection<User> getAll();

    void addFriend(long userID, long friendID);


    void deleteFriend(long userID, long friendID);


    Collection<User> getFriends(long userID);


    Collection<User> getCommonFriends(long userID, long otherUserID);
}