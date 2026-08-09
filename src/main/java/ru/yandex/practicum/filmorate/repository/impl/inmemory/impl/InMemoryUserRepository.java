package ru.yandex.practicum.filmorate.repository.impl.inmemory.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;
import ru.yandex.practicum.filmorate.repository.impl.inmemory.InMemoryAbstractRepository;

@Repository
@Profile("inmemory")
public class InMemoryUserRepository extends InMemoryAbstractRepository<Integer, User> implements UserRepository {

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId.getAndIncrement());
        }
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        repository.put(user.getId(), user);
        return user;
    }
}