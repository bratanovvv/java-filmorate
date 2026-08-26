package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.query.UserQueries;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepository extends AbstractRepository<Integer, User> {

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<User> getById(Integer id) {
        List<User> users = findAll(UserQueries.FIND_BY_ID, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        User user = users.getFirst();
        loadFriendsForUsers(List.of(user));
        return Optional.of(user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = findAll(UserQueries.FIND_ALL);
        loadFriendsForUsers(users);
        return users;
    }

    @Override
    public User save(User user) {
        int id = insert(UserQueries.INSERT,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null);
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        executeUpdate(UserQueries.UPDATE,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null,
                user.getId());
        jdbc.update(UserQueries.DELETE_FRIENDS, user.getId());
        saveFriends(user);
        return user;
    }

    @Override
    public List<User> findAllByIds(Collection<Integer> ids) {
        List<User> users = findByIds(UserQueries.FIND_ALL_BY_IDS, ids);
        loadFriendsForUsers(users);
        return users;
    }

    @Override
    public void delete(Integer id) {
        deleteById(UserQueries.DELETE_USER, id);
    }

    private void loadFriendsForUsers(List<User> users) {
        if (users.isEmpty()) {
            return;
        }

        List<Integer> userIds = users.stream().map(User::getId).toList();
        String placeholders = userIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = UserQueries.FIND_FRIENDS_BY_USER_IDS.formatted(placeholders);

        Map<Integer, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        jdbc.query(query, rs -> {
            int userId = rs.getInt("user_id");
            int friendId = rs.getInt("friend_id");
            User user = userMap.get(userId);
            if (user != null) {
                user.getFriends().add(friendId);
            }
        }, userIds.toArray());
    }

    private void saveFriends(User user) {
        saveRelation(user.getId(), user.getFriends(), UserQueries.MERGE_FRIEND);
    }
}
