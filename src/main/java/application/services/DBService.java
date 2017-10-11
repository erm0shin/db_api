package application.services;

import application.models.DBInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("StringBufferReplaceableByString")
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
        final StringBuilder query = new StringBuilder();
        query.append("TRUNCATE TABLE users CASCADE;")
                .append("TRUNCATE TABLE users CASCADE;")
                .append("TRUNCATE TABLE forums CASCADE;")
                .append("TRUNCATE TABLE threads CASCADE;")
                .append("TRUNCATE TABLE posts CASCADE;")
                .append("TRUNCATE TABLE votes CASCADE;")
                .append("TRUNCATE TABLE forum_members CASCADE;");
        template.update(query.toString());
    }

    public DBInfo getDBInfo() {
        final StringBuilder query = new StringBuilder();
        query.append("(SELECT COUNT(*) FROM forums) AS forum, ")
                .append("(SELECT COUNT(*) FROM posts) AS post, ")
                .append("(SELECT COUNT(*) FROM threads) AS thread, ")
                .append("(SELECT COUNT(*) FROM users) AS \"user\"");
        return template.queryForObject(query.toString(), DB_INFO_ROW_MAPPER);
    }
}
