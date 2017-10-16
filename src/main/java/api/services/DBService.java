package api.services;

import api.models.DBInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Service
@Transactional
public class DBService {

    private JdbcTemplate template;

    @Autowired
    public DBService(JdbcTemplate template) {
        this.template = template;
    }

    private static final RowMapper<DBInfo> DB_INFO_ROW_MAPPER = (res, num) -> new DBInfo(res.getInt("forum"),
            res.getInt("post"), res.getInt("thread"), res.getInt("user"));

    public void clearDB() {
        final String query = "TRUNCATE TABLE users CASCADE;"
                + "TRUNCATE TABLE users CASCADE;"
                + "TRUNCATE TABLE forums CASCADE;"
                + "TRUNCATE TABLE threads CASCADE;"
                + "TRUNCATE TABLE posts CASCADE;"
                + "TRUNCATE TABLE votes CASCADE;"
                + "TRUNCATE TABLE forum_members CASCADE;";
        template.update(query);
    }

    public DBInfo getDBInfo() {
        final String query = "SELECT (SELECT COUNT(*) FROM forums) AS forum, "
                + "(SELECT COUNT(*) FROM posts) AS post, "
                + "(SELECT COUNT(*) FROM threads) AS thread, "
                + "(SELECT COUNT(*) FROM users) AS \"user\"";
        return template.queryForObject(query, DB_INFO_ROW_MAPPER);
    }
}
