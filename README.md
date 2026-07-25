# java-filmorate

Database schema

```mermaid
erDiagram

    FILM {
        int id PK
        varchar name
        varchar description
        date release_date
        int duration
        int rating_id FK
    }

    USER {
        int id PK
        varchar email
        varchar login
        varchar name
        date birthday
    }

    RATING {
        int id PK
        varchar name
    }

    FILM_GENRE {
        int film_id PK, FK
        int genre_id PK, FK
    }



    FILM_LIKE {
        int film_id PK, FK
        int user_id PK, FK
    }

    FRIENDSHIP_STATUS {
        int id PK
        varchar name
    }

    FRIENDSHIP {
        int user_id PK, FK
        int friend_id PK, FK
        int status_id FK
    }

    GENRE {
        int id PK
        varchar name
    }


    FILM ||--o{ FILM_GENRE : "имеет"
    GENRE ||--o{ FILM_GENRE : "входит в"

    FILM ||--o{ FILM_LIKE : "получает"
    USER ||--o{ FILM_LIKE : "ставит"

    RATING ||--o{ FILM : "имеет"

    USER ||--o{ FRIENDSHIP : "отправляет"
    USER ||--o{ FRIENDSHIP : "получает"

    FRIENDSHIP_STATUS ||--o{ FRIENDSHIP : "определяет" 

  ```
