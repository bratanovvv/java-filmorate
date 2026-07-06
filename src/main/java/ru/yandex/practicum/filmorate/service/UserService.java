package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;

import java.util.List;

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

    public User saveUser(User user) {
        normalizeUser(user);

        User saved = userRepository.save(user);

        log.info("Создан пользователь: id={}, login={}", saved.getId(), saved.getLogin());

        return saved;
    }

    public User updateUser(User user) {
        User existingUser = getUser(user.getId());

        normalizeUser(user);

        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());

        User updated = userRepository.save(existingUser);

        log.info("Обновлён пользователь: id={}, login={}", updated.getId(), updated.getLogin());

        return updated;
    }

    private void normalizeUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}