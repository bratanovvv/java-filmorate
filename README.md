# java-filmorate

Бэкенд-сервис для оценки фильмов: пользователи добавляют фильмы, ставят лайки,
добавляют друг друга в друзья, пишут отзывы и получают рекомендации и ленту событий.

## Технологии

- Java 21, Spring Boot 3.5 (Web, Validation, JDBC)
- H2 Database (файловый режим по умолчанию, настраивается через `DB_URL`)
- Lombok, Checkstyle, Logbook (логирование HTTP)

## Запуск

```bash
mvn spring-boot:run
```

Схема БД (`schema.sql`) и справочные данные (`data.sql`) применяются автоматически при старте.

## Схема базы данных

```mermaid
erDiagram

    FILM {
        int id PK
        varchar name
        varchar description
        date release_date
        int duration
        int mpa_rating_id FK
    }

    USER {
        int id PK
        varchar email "UNIQUE"
        varchar login
        varchar name
        date birthday
    }

    MPA_RATING {
        int id PK
        varchar name
    }

    GENRE {
        int id PK
        varchar name
    }

    DIRECTOR {
        int id PK
        varchar name
    }

    FILM_GENRE {
        int film_id PK, FK
        int genre_id PK, FK
    }

    FILM_DIRECTOR {
        int film_id PK, FK
        int director_id PK, FK
    }

    LIKE {
        int film_id PK, FK
        int user_id PK, FK
    }

    FRIEND_STATUS {
        int id PK
        varchar name
    }

    FRIENDSHIP {
        int user_id PK, FK
        int friend_id PK, FK
        int status_id FK
    }

    EVENT {
        int id PK
        int user_id FK
        int entity_id
        varchar event_type
        varchar operation
        bigint created_at
    }

    REVIEW {
        int id PK
        varchar content
        boolean is_positive
        int user_id FK
        int film_id FK
    }

    REVIEW_LIKE {
        int review_id PK, FK
        int user_id PK, FK
        boolean is_like
    }

    MPA_RATING ||--o{ FILM : "имеет"
    FILM ||--o{ FILM_GENRE : "имеет"
    GENRE ||--o{ FILM_GENRE : "входит в"
    FILM ||--o{ FILM_DIRECTOR : "снимают"
    DIRECTOR ||--o{ FILM_DIRECTOR : "участвует в"
    FILM ||--o{ LIKE : "получает"
    USER ||--o{ LIKE : "ставит"
    USER ||--o{ FRIENDSHIP : "отправляет"
    USER ||--o{ FRIENDSHIP : "получает"
    FRIEND_STATUS ||--o{ FRIENDSHIP : "определяет"
    USER ||--o{ EVENT : "генерирует"
    FILM ||--o{ REVIEW : "получает"
    USER ||--o{ REVIEW : "пишет"
    REVIEW ||--o{ REVIEW_LIKE : "оценивается"
    USER ||--o{ REVIEW_LIKE : "оценивает"
```
