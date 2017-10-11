package application.services;

import application.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Service
@Transactional
public class ThreadService {
//    CREATE TABLE IF NOT EXISTS threads (
//            id        SERIAL     PRIMARY KEY,
//            author    TEXT          NOT NULL,
//            created   TIMESTAMPTZ,
//            forum     TEXT,
//            message   TEXT          NOT NULL,
//            slug      TEXT          UNIQUE,
//            title     TEXT          NOT NULL,
//            votes     INT
//    );

    private JdbcTemplate template;

    @Autowired
    public ThreadService(JdbcTemplate template) {
        this.template = template;
    }

    //Упростить преобразование времени!!!!!!!!

    private static final RowMapper<Thread> THREAD_ROW_MAPPER = (res, num) -> new Thread(res.getInt("id"),
            res.getString("author"), LocalDateTime.ofInstant(res.getTimestamp("created").toInstant(),
            ZoneOffset.ofHours(0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
            res.getString("forum"), res.getString("mesage"), res.getString("slug"),
            res.getString("title"), res.getInt("votes"));

    public Thread createThread(String author, String created, String message,
                               String title, String slug, String forum) {
        template.update("UPDATE forums SET threads = threads + 1 WHERE LOWER(SLUG) = LOWER(?)", forum);
        final String query = "INSERT INTO threads (author, created, forum, message, slug, title, votes) "
                + "VALUES (?, ?::TIMESTAMPTZ, ?, ?, ?, ?, ?) RETURNING *";
        return template.queryForObject(query, THREAD_ROW_MAPPER, author, created, forum, message, slug, title, 0);
    }

    //since, наверное, может быть null!!!!!!
    public List<Thread> getForumThreads(String slug, Integer limit, String since, Boolean desc) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM threads WHERE LOWER(t.forum) = LOWER(?) AND t.created >= '")
                .append(since).append("'::TIMESTAMPTZ ORDER BY t.created ");
        if (desc) {
            query.append("DESC ");
        } else {
            query.append("ASC ");
        }
        query.append("LIMIT ?");
        return template.query(query.toString(), THREAD_ROW_MAPPER, slug, limit);
    }

    public Thread getThreadById(Integer id) {
        final String query = "SELECT * FROM threads t WHERE t.id = ?";
        return template.queryForObject(query, THREAD_ROW_MAPPER, id);
    }
}
