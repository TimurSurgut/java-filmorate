package ru.yandex.practicum.filmorate.storage.userStorage;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;


@Component


public interface UserStorage {


    ResponseEntity<?> findAllUsers();


    ResponseEntity<?> postUser(User user);


    ResponseEntity<?> updateUser(User user);


    HashMap<Integer, User> findAll();


    ResponseEntity<?> getUserById(int id);

 }
