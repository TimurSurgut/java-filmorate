package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.NonNull;


@Data
public class User {

    private int id;
    @NonNull
    private String email;
    @NonNull
    private String login;

    private String name;
    @NonNull
    private String birthday;

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
