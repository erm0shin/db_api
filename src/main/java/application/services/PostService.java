package application.services;

import application.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Service
@Transactional
public class PostService {

    private JdbcTemplate template;

    @Autowired
    public PostService(JdbcTemplate template) {
        this.template = template;
    }

//    CREATE TABLE posts (
//            id          BIGSERIAL     PRIMARY KEY,
//            author      TEXT          NOT NULL,
//            created     TIMESTAMPTZ   NOT NULL,
//            forum       TEXT,
//            isEdited    BOOLEAN       DEFAULT FALSE,
//            message     TEXT          NOT NULL,
//            parent      BIGINT,
//            thread_id   INT           NOT NULL
//    );

    private static final RowMapper<Post> POST_ROW_MAPPER = (res, num) -> new Post(res.getLong("id"),
            res.getString("author"), LocalDateTime.ofInstant(res.getTimestamp("created").toInstant(),
            ZoneOffset.ofHours(0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
            res.getString("forum"), res.getBoolean("isEdited"), res.getString("message"),
            res.getLong("parent"), res.getInt("thread_id"));

    public Post getPostById(Long id) {
        final String query = "SELECT * FROM posts p WHERE p.id = ?";
        return template.queryForObject(query, POST_ROW_MAPPER, id);
    }

    public Post updatePostMessage(Long id, String message) {
        final String query = "UPDATE posts p SET p.isEdited = ?, p.message = ? WHERE p.id = ? RETURNING *";
        return template.queryForObject(query, POST_ROW_MAPPER, true, message, id);
    }
}
