package application.services;

import application.models.Thread;
import application.models.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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


//    CREATE TABLE votes (
//            user_id     BIGINT    NOT NULL,
//            thread_id   INT       NOT NULL,
//            voice       INT       NOT NULL
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
            res.getString("forum"), res.getString("message"), res.getString("slug"),
            res.getString("title"), res.getInt("votes"));

    private static final RowMapper<Vote> VOTE_ROW_MAPPER = (res, num) -> new Vote(res.getLong("user_id"),
            res.getInt("thread_id"), res.getInt("voice"));

    public Thread createThread(String author, String created, String message,
                               String title, String slug, String forum) {
        final String query = "INSERT INTO threads (author, created, forum, message, slug, title, votes) "
                + "VALUES (?, COALESCE (?::TIMESTAMPTZ, CURRENT_TIMESTAMP ), ?, ?, ?, ?, ?) RETURNING *";
        final Thread thread = template.queryForObject(query, THREAD_ROW_MAPPER, author, created, forum, message, slug, title, 0);
        template.update("UPDATE forums SET threads = threads + 1 WHERE LOWER(slug) = LOWER(?)", forum);
        return thread;
    }

    public List<Thread> getForumThreads(String slug, Integer limit, String since, Boolean desc) {
        if (desc == null) {
            desc = false;
        }
        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM threads t WHERE LOWER(t.forum) = LOWER(?) ");
        if (since != null) {
            if (desc) {
                query.append("AND t.created <= '").append(since).append("'::TIMESTAMPTZ ");
            } else {
                query.append("AND t.created >= '").append(since).append("'::TIMESTAMPTZ ");
            }
        }
        if (desc) {
            query.append("ORDER BY t.created DESC ");
        } else {
            query.append("ORDER BY t.created ASC ");
        }
        query.append("LIMIT ?");
        return template.query(query.toString(), THREAD_ROW_MAPPER, slug, limit);
    }

    public Thread getThreadById(Integer id) {
        final String query = "SELECT * FROM threads t WHERE t.id = ?";
        return template.queryForObject(query, THREAD_ROW_MAPPER, id);
    }

    public Thread getThreadBySlug(String slug) {
        final String query = "SELECT * FROM threads t WHERE LOWER(t.slug) = LOWER(?)";
        return template.queryForObject(query, THREAD_ROW_MAPPER, slug);
    }

    public Thread getThreadBySlugOrId(String slugOrId) {
        try {
            return this.getThreadBySlug(slugOrId);
        } catch (EmptyResultDataAccessException e) {
            return this.getThreadById(Integer.valueOf(slugOrId));
        }
    }

    public Thread updateThreadDetails(String slugOrId, String message, String title) {
        final Thread thread = this.getThreadBySlugOrId(slugOrId);
        final String query = "UPDATE threads SET message = ?, title = ? WHERE id = ? RETURNING *";
        return template.queryForObject(query, THREAD_ROW_MAPPER, message, title, thread.getId());
    }

    @SuppressWarnings("ConstantConditions")
    public Thread voteThread(String slugOrId, Long userId, Integer voice) {
        final Thread thread = this.getThreadBySlugOrId(slugOrId);
        final String threadQuery = "UPDATE threads SET votes = votes + ? WHERE id = ? RETURNING *";

        try {
            final String lastVoteQuery = "SELECT * FROM votes WHERE user_id = ? AND thread_id = ?";
            final Vote lastVote = template.queryForObject(lastVoteQuery, VOTE_ROW_MAPPER, userId, thread.getId());

            if (Objects.equals(lastVote.getVoice(), voice)) {
                return thread;
            }
            final String votesQuery = "UPDATE votes SET voice = ? WHERE user_id = ? AND thread_id = ?";
            template.update(votesQuery, voice, userId, thread.getId());
            thread.setVotes(template.queryForObject(threadQuery, THREAD_ROW_MAPPER, (voice == 1 ? 2 : -2), thread.getId()).getVotes());
        } catch (EmptyResultDataAccessException e) {
            final String votesQuery = "INSERT INTO votes (user_id, thread_id, voice) VALUES (?, ?, ?)";
            template.update(votesQuery, userId, thread.getId(), voice);
            thread.setVotes(template.queryForObject(threadQuery, THREAD_ROW_MAPPER, voice, thread.getId()).getVotes());
        }
        return thread;
    }
}
