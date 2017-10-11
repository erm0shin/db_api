package application.services;

import application.models.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Service
@Transactional
public class ForumService {
    private final JdbcTemplate template;

    @Autowired
    public ForumService(JdbcTemplate template) {
        this.template = template;
    }

    private static final RowMapper<Forum> FORUM_ROW_MAPPER = (res, num) -> new Forum(res.getLong("id"),
            res.getLong("posts"), res.getString("slug"), res.getInt("threads"),
            res.getString("title"), res.getString("\"user\""));

//    CREATE TABLE IF NOT EXISTS forums (
//            id        BIGSERIAL   PRIMARY KEY,
//            posts     BIGINT,
//            slug      TEXT        NOT NULL,
//            threads   INT,
//            title     TEXT        NOT NULL,
//            "user"    TEXT        NOT NULL
//    );

    public Forum createForum(String slug, String title, String user) {
        final String query = "INSERT INTO forums (posts, slug, threads, title, \"user\", ) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING *";
        return template.queryForObject(query, FORUM_ROW_MAPPER, 0, slug, 0, title, user);
    }

    public Forum getForumDetails(String slug) {
        final String query = "SELECT * FROM forums f WHERE LOWER(f.slug) = LOWER(?)";
        return template.queryForObject(query, FORUM_ROW_MAPPER, slug);
    }

}