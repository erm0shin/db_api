package application.services;

import application.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Service
@Transactional
public class UserService {

    private JdbcTemplate template;

    @Autowired
    public UserService(JdbcTemplate template) {
        this.template = template;
    }

//    CREATE TABLE IF NOT EXISTS users (
//            id        BIGSERIAL  PRIMARY KEY,
//            email     TEXT       NOT NULL,
//            fullname  TEXT       NOT NULL,
//            nickname  TEXT,
//            about     TEXT
//    );

//    CREATE TABLE forum_members (
//            user_id     BIGINT,    --REFERENCES users (id),
//            forum_id    BIGINT    --REFERENCES forums (id)
//);

    private static final RowMapper<User> USER_ROW_MAPPER = (res, num) -> new User(res.getLong("id"),
            res.getString("email"), res.getString("fullname"),
            res.getString("nickname"), res.getString("about"));


    public List<User> getForumMembers(String slug, Integer limit, String since, Boolean desc) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM users u JOIN forum_members fm ON(u.id = fm.user_id) ")
                .append("WHERE fm.forum_id = ? ")
                .append("AND LOWER(u.nickname) > LOWER('").append(since).append("') ")
                .append("ORDER BY LOWER(u.nickname) ");
        if (desc) {
            query.append("DESC ");
        } else {
            query.append("ASC ");
        }
        query.append("LIMIT ?");
        return template.query(query.toString(), USER_ROW_MAPPER, slug, limit);
    }

    public User getUserByNickname(String nickname) {
        final String query = "SELECT * FROM users u WHERE LOWER(u.nickname) = LOWER(?)";
        return template.queryForObject(query, USER_ROW_MAPPER, nickname);
    }

    public User createUser(String email, String fullname, String nickname, String about) {
        final String query = "INSERT INTO users (email, fullname, nickname, about) "
                + "VALUES (?, ?, ?, ?) RETURNING *";
        return template.queryForObject(query, USER_ROW_MAPPER, email, fullname, nickname, about);
    }

    public List<User> getUsersByNicknameOrEmail(String nickname, String email) {
        final String query = "SELECT * FROM users u WHERE LOWER(u.nickname) = LOWER(?) OR LOWER(u.email) = LOWER(?)";
        return template.query(query, USER_ROW_MAPPER, nickname, email);
    }

    public User updateProfile(String email, String fullname, String nickname, String about) {
        final String query = "UPDATE users u SET u.email = ?, u.fullname = ?, u.about = ? "
                + "WHERE LOWER(u.nickname) = LOWER(?) RETURNING *";
        return template.queryForObject(query, USER_ROW_MAPPER, email, fullname, about, nickname);
    }
}
