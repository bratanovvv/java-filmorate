package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserRepository extends AbstractRepository<Integer, User> {
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public User save(User user) {
        if(user.getId() == null) {
            user.setId(nextId.getAndIncrement());
        }
        repository.put(user.getId(), user);
        return user;
    }
}
