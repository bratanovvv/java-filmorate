package ru.yandex.practicum.filmorate.repository.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

@Component
public class InMemoryUserRepository extends InMemoryAbstractRepository<Integer, User> implements UserRepository {

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId.getAndIncrement());
        }
        repository.put(user.getId(), user);
        return user;
    }
}