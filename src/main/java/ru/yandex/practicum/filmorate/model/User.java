package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class
User {

    int id;
    String email;
    String login;
    String name;
    String birthday;
    HashSet<Integer> friends = new HashSet<>();

    public User(int id, String email, String login, String name, String birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User() {
    }
}
