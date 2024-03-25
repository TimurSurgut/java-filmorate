package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.service.user.UserLogicException;
import ru.yandex.practicum.filmorate.exception.storage.dao.friendship.FriendshipAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.storage.dao.friendship.FriendshipNotFoundException;
import ru.yandex.practicum.filmorate.exception.storage.user.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.storage.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.storage.user.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendsDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.exception.service.user.UserLogicException.*;
import static ru.yandex.practicum.filmorate.exception.storage.dao.friendship.FriendshipAlreadyExistsException.FRIENDSHIP_ALREADY_EXIST;
import static ru.yandex.practicum.filmorate.exception.storage.dao.friendship.FriendshipNotFoundException.FRIENDSHIP_NOT_FOUND;
import static ru.yandex.practicum.filmorate.exception.storage.user.UserAlreadyExistsException.USER_ALREADY_EXISTS;
import static ru.yandex.practicum.filmorate.exception.storage.user.UserNotFoundException.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDbService implements UserService {
    private final UserStorage userStorage;
    private final FriendsDao friendsDao;

    @Override
    public User add(User user) {
        checkUserToAdd(user);
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        checkUserToUpdate(user);
        return userStorage.update(user);
    }

    @Override
    public User get(long userID) {
        if (!userStorage.contains(userID)) {
            log.warn("Не удалось вернуть пользователя: {}.", String.format(USER_NOT_FOUND, userID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userID));
        }
        return userStorage.get(userID);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void addFriend(long userID, long friendID) {
        checkFriendToAdd(userID, friendID);
        boolean isMutual = friendsDao.contains(friendID, userID);
        friendsDao.add(friendID, userID, isMutual);
    }

    @Override
    public void deleteFriend(long userID, long friendID) {
        checkFriendToDelete(userID, friendID);
        friendsDao.delete(friendID, userID);
    }

    @Override
    public Collection<User> getFriends(long userID) {
        if (!userStorage.contains(userID)) {
            log.warn("Не удалось вернуть друзей: {}.", String.format(USER_NOT_FOUND, userID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userID));
        }
        List<User> friends = friendsDao.getUserIds(userID).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(userStorage::get)
                .collect(Collectors.toList());
        log.trace("Возвращён список друзей: {}.", friends);
        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(long userID, long otherUserID) {
        checkCommonFriendToGet(userID, otherUserID);
        List<User> commonFriends = CollectionUtils.intersection(
                        friendsDao.getUserIds(userID),
                        friendsDao.getUserIds(otherUserID)).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(userStorage::get)
                .collect(Collectors.toList());
        log.trace("Возвращён список общих друзей: {}.", commonFriends);
        return commonFriends;
    }


    private void checkUserToAdd(User user) {
        String message;
        log.debug("checkUserToAdd({}).", user);
        String msg = "Не удалось добавить пользователя: {}.";
        if (user.getId() != null) {
            if (userStorage.contains(user.getId())) {
                log.warn(msg, String.format(USER_ALREADY_EXISTS, user.getId()));
                throw new UserAlreadyExistsException(String.format(USER_ALREADY_EXISTS, user.getId()));
            } else {
                log.warn(msg, "Запрещено устанавливать ID вручную");
                throw new IllegalArgumentException("Запрещено устанавливать ID вручную");
            }
        }
        if (user.getEmail() == null || user.getEmail().indexOf("@") == -1) {
            message = "электронная почта не может быть пустой и должна содержать символ @";
            throw new ValidationException(HttpStatus.BAD_REQUEST, message);
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            message = "логин не может быть пустым и содержать пробелы";
            throw new ValidationException(HttpStatus.BAD_REQUEST, message);
        }
        if (LocalDate.now().isBefore(user.getBirthday())) {
            message = "дата рождения не может быть в будущем";
            throw new ValidationException(HttpStatus.BAD_REQUEST, message);
        }
    }


    private void checkUserToUpdate(User user) {
        if (!userStorage.contains(user.getId())) {
            log.warn("Не удалось обновить пользователя: {}.", String.format(USER_NOT_FOUND, user.getId()));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, user.getId()));
        }
    }


    private void checkFriendToAdd(long userID, long friendID) {
        log.debug("checkFriendToAdd({}, {}).", userID, friendID);
        String msg = "Не удалось добавить друга: {}.";
        if (!userStorage.contains(userID)) {
            log.warn(msg, String.format(USER_NOT_FOUND, userID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userID));
        }
        if (!userStorage.contains(friendID)) {
            log.warn(msg, String.format(USER_NOT_FOUND, friendID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, friendID));
        }
        if (userID == friendID) {
            log.warn(msg, String.format(UNABLE_TO_ADD_YOURSELF, userID));
            throw new UserLogicException(String.format(UNABLE_TO_ADD_YOURSELF, userID));
        }
        if (friendsDao.contains(friendID, userID)) {
            log.warn(msg, String.format(FRIENDSHIP_ALREADY_EXIST, friendID, userID));
            throw new FriendshipAlreadyExistsException(String.format(FRIENDSHIP_ALREADY_EXIST, friendID, userID));
        }
    }


    private void checkFriendToDelete(long userID, long friendID) {
        log.debug("checkFriendToDelete({}, {}).", userID, friendID);
        String msg = "Не удалось удалить друга: {}.";
        if (!userStorage.contains(userID)) {
            log.warn(msg, String.format(USER_NOT_FOUND, userID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userID));
        }
        if (!userStorage.contains(friendID)) {
            log.warn(msg, String.format(USER_NOT_FOUND, friendID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, friendID));
        }
        if (userID == friendID) {
            log.warn(msg, String.format(UNABLE_TO_DELETE_YOURSELF, userID));
            throw new UserLogicException(String.format(UNABLE_TO_DELETE_YOURSELF, userID));
        }
        if (!friendsDao.contains(friendID, userID)) {
            log.warn(msg, String.format(FRIENDSHIP_NOT_FOUND, friendID, userID));
            throw new FriendshipNotFoundException(String.format(FRIENDSHIP_NOT_FOUND, friendID, userID));
        }
    }


    private void checkCommonFriendToGet(long userID, long otherUserID) {
        if (!userStorage.contains(userID)) {
            log.warn("Не удалось вернуть общих друзей: {}.", String.format(USER_NOT_FOUND, userID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userID));
        }
        if (!userStorage.contains(otherUserID)) {
            log.warn("Не удалось вернуть общих друзей: {}.", String.format(USER_NOT_FOUND, otherUserID));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, otherUserID));
        }
        if (userID == otherUserID) {
            log.warn("Не удалось вернуть общих друзей: {}.", String.format(UNABLE_FRIENDS_AMONG_THEMSELVES, userID));
            throw new UserLogicException(String.format(UNABLE_FRIENDS_AMONG_THEMSELVES, userID));
        }
    }
}