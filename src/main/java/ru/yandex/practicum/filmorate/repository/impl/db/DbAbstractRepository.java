package ru.yandex.practicum.filmorate.repository.impl.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.repository.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class DbAbstractRepository<K, V> implements Repository<K, V> {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<V> rowMapper;

    protected DbAbstractRepository(JdbcTemplate jdbc, RowMapper<V> rowMapper) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
    }

    protected Optional<V> findOne(String query, Object... params) {
        List<V> result = jdbc.query(query, rowMapper, params);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    protected List<V> findMany(String query, Object... params) {
        return jdbc.query(query, rowMapper, params);
    }

    protected int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Не удалось сохранить данные");
        }
    }

    protected void executeUpdate(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Не удалось обновить данные");
        }
    }

    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    protected List<V> findAllByIds(String prefix, String suffix, Collection<K> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = prefix + placeholders + suffix;
        return findMany(query, ids.toArray());
    }
}
