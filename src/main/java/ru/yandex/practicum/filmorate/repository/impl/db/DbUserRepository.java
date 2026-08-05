package ru.yandex.practicum.filmorate.repository.impl.db;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;
import ru.yandex.practicum.filmorate.repository.impl.db.query.UserQueries;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
public class DbUserRepository extends DbAbstractRepository<Integer, User> implements UserRepository {

    public DbUserRepository(JdbcTemplate jdbc, RowMapper<User> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<User> getById(Integer id) {
        return findOne(UserQueries.FIND_BY_ID, id);
    }

    @Override
    public List<User> getAll() {
        return findMany(UserQueries.FIND_ALL);
    }

    @Override
    public void clear() {
        jdbc.execute(UserQueries.CLEAR);
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
        return user;
    }

    @Override
    public List<User> findAllByIds(Collection<Integer> ids) {
        return findAllByIds(UserQueries.FIND_ALL_BY_IDS_PREFIX, UserQueries.FIND_ALL_BY_IDS_SUFFIX, ids);
    }
}
