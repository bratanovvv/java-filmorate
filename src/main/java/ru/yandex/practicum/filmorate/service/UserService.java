package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User getUser(int id) {
        return userRepository.getById(id)
                .orElseThrow(() ->
                        new ApiException(ErrorCode.USER_NOT_FOUND, id));
    }

    public List<User> getUsers() {
        return userRepository.getAll();
    }

    @Transactional
    public User saveUser(User user) {
        normalizeUser(user);

        User saved = userRepository.save(user);

        log.info("Создан пользователь: id={}, login={}", saved.getId(), saved.getLogin());

        return saved;
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = getUser(user.getId());

        normalizeUser(user);

        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());

        User updated = userRepository.update(existingUser);

        log.info("Обновлён пользователь: id={}, login={}", updated.getId(), updated.getLogin());

        return updated;
    }

@Transactional
    public void addFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().add(friend.getId());

        userRepository.update(user);

        log.info("Пользователь id={} добавил в друзья пользователя id={}", userId, friendId);
    }

    @Transactional
    public void removeFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().remove(friend.getId());

        userRepository.update(user);

        log.info("Пользователь id={} удалил из друзей пользователя id={}", userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        User user = getUser(userId);
        return userRepository.findAllByIds(user.getFriends());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUser(userId);
        User other = getUser(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.collectingAndThen(
                        Collectors.toSet(),
                        userRepository::findAllByIds
                ));
    }

    private void normalizeUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}