package ru.yandex.practicum.filmorate.storage.dao.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.storage.mapper.FriendsMapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDaoImpl implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long userId, long friendId, boolean statusRequest) {
        log.debug("add({}, {}, {}).", userId, friendId, statusRequest);
        jdbcTemplate.update(""
                + "INSERT INTO Friends (user_id, friend_id, status_request) "
                + "VALUES(?, ?, ?)", userId, friendId, statusRequest);
        Friends result = get(userId, friendId);
        log.trace("Добавлена связь: {}.", result);
    }

    @Override
    public void delete(long userId, long friendId) {
        log.debug("delete({}, {}).", userId, friendId);
        Friends result = Objects.requireNonNull(get(userId, friendId));
        jdbcTemplate.update(""
                + "DELETE FROM Friends "
                + "WHERE user_id=? "
                + "AND friend_id=?", userId, friendId);
        if (result.getStatusRequest()) {
            jdbcTemplate.update(""
                    + "UPDATE Friends "
                    + "SET status_request=false "
                    + "WHERE user_id=? "
                    + "AND friend_id=?", friendId, userId);
            log.debug("Дружба между {} и {} перестала быть взаимной.",
                    friendId, userId);
        }
        log.trace("Удалена связь: {}.", result);
    }

    @Override
    public Collection<Long> getUserIds(long friendId) {
        log.debug("getFriends({}).", friendId);
        List<Long> Friend = jdbcTemplate.query(format(""
                        + "SELECT user_id, friend_id, status_request "
                        + "FROM Friends "
                        + "WHERE friend_id=%d", friendId), new FriendsMapper())
                .stream()
                .map(Friends::getUserId)
                .collect(Collectors.toList());
        log.trace("Возвращены запросы на дружбу с пользователем ID_{}: {}.",
                friendId, Friend);
        return Friend;
    }

    @Override
    public boolean contains(long userId, long friendId) {
        log.debug("contains({}, {}).", userId, friendId);
        try {
            get(userId, friendId);
            log.trace("Найден запрос на дружбу от пользователя ID_{} к пользователю ID_{}.",
                    userId, friendId);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            log.trace("Запрос на дружбу от пользователя ID_{} к пользователю ID_{} не найден.",
                    userId, friendId);
            return false;
        }
    }


    private Friends get(long userId, long friendId) {
        return jdbcTemplate.queryForObject(format(""
                + "SELECT user_id, friend_id, status_request "
                + "FROM Friends "
                + "WHERE user_id=%d "
                + "AND friend_id=%d", userId, friendId), new FriendsMapper());
    }
}
