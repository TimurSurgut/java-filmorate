package ru.yandex.practicum.filmorate.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;


@Service
public class UserService {
    UserStorage userStorage;
    Gson gson = new Gson();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ResponseEntity<?> addFriend(int id, int idFriend) {
        if (!userStorage.findAll().containsKey(id)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + id + " не зарегистрирован"), HttpStatus.NOT_FOUND);
        } else if (userStorage.findAll().containsKey(idFriend)) {
            if (userStorage.findAll().get(id).getFriends().contains(idFriend)) {
                return new ResponseEntity<>
                        (gson.toJson("Пользователь с id " + idFriend + " уже добавлен в друзья"), HttpStatus.OK);
            } else {
                userStorage.findAll().get(id).getFriends().add(idFriend);
                userStorage.findAll().get(idFriend).getFriends().add(id);
            }
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + idFriend + " успешно добавлен в друзья"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>
                    (gson.toJson("Невозможно добавить в друзья не зарегистрированного пользователя"),
                            HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteFriend(int id, int idFriend) {
        if (!userStorage.findAll().containsKey(id)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + id + " не зарегистрирован"), HttpStatus.NOT_FOUND);
        } else if (!userStorage.findAll().containsKey(idFriend)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + idFriend + " не зарегистрирован"),
                            HttpStatus.NOT_FOUND);
        } else if (!userStorage.findAll().get(id).getFriends().contains(id)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + idFriend + " отсутствует в списке друзей"),
                            HttpStatus.OK);
        } else {
            userStorage.findAll().get(id).getFriends().remove(idFriend);
            userStorage.findAll().get(idFriend).getFriends().remove(id);
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + idFriend + " успешно удалён из друзей"), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getAllFriends(int id) {
        if (!userStorage.findAll().containsKey(id)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + id + " не зарегистрирован"), HttpStatus.NOT_FOUND);
        } else if (userStorage.findAll().get(id).getFriends() == null) {
            return new ResponseEntity<>
                    (gson.toJson("У пользователя с id " + id + " список друзей пуст"),
                            HttpStatus.NOT_FOUND);
        } else {
            ArrayList<User> usersFriend = new ArrayList<>();
            HashSet<Integer> friends = userStorage.findAll().get(id).getFriends();
            System.out.println(friends);
            for (int i : friends) {
                usersFriend.add(userStorage.findAll().get(i));
                System.out.println(i);
            }
            return new ResponseEntity<>(gson.toJson(usersFriend), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getCommonFriends(int id, int otherId) {
        ArrayList<User> commonFriends = new ArrayList<>();
        if (!userStorage.findAll().containsKey(id)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + id + " не зарегистрирован"), HttpStatus.NOT_FOUND);
        } else if (!userStorage.findAll().containsKey(otherId)) {
            return new ResponseEntity<>
                    (gson.toJson("Пользователь с id " + otherId + " не зарегистрирован"), HttpStatus.NOT_FOUND);
        } else if (userStorage.findAll().get(id).getFriends() == null || userStorage.findAll().get(id).getFriends().isEmpty()) {
            return new ResponseEntity<>
                    (gson.toJson(commonFriends), HttpStatus.OK);
        } else if (userStorage.findAll().get(id).getFriends() == null || userStorage.findAll().get(id).getFriends().isEmpty()) {
            return new ResponseEntity<>
                    (gson.toJson(commonFriends), HttpStatus.OK);
        } else {
            HashSet<Integer> friends1 = userStorage.findAll().get(id).getFriends();
            HashSet<Integer> friends2 = userStorage.findAll().get(otherId).getFriends();
            for (int i : friends1) {
                if (friends2.contains(i)) {
                    commonFriends.add(userStorage.findAll().get(i));
                }
            }
            return new ResponseEntity<>(gson.toJson(commonFriends), HttpStatus.OK);

        }
    }

}

