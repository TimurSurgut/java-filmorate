package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.userStorage.InMemoryUserStorage;

@RestController
public class UserController {
    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    private final String pathIdFriendsId = "/users/{id}/friends/{friendId}";

    @ResponseBody
    @GetMapping("/users")
    public ResponseEntity<?> findAllUsers() {
        return inMemoryUserStorage.findAllUsers();
    }

    @ResponseBody
    @PostMapping(value = "/users")
    public ResponseEntity<?> postUser(@RequestBody User user) {
        return inMemoryUserStorage.postUser(user);
    }

    @ResponseBody
    @PutMapping(value = "/users")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @ResponseBody
    @PutMapping(value = pathIdFriendsId)
    public ResponseEntity<?> addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @ResponseBody
    @DeleteMapping(value = pathIdFriendsId)
    public ResponseEntity<?> deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @ResponseBody
    @GetMapping(value = "/users/{id}/friends")
    public ResponseEntity<?> getAllFriends(@PathVariable int id) {
        return userService.getAllFriends(id);
    }

    @ResponseBody
    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public ResponseEntity<?> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @ResponseBody
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable int id) {
        return inMemoryUserStorage.getUserById(id);
    }
}



