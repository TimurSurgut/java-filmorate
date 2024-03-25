package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friends {
    @NonNull
    private Long userId;

    @NonNull
    private Long friendId;

    @NonNull
    private Boolean statusRequest;
}