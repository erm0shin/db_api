package application.services;

import application.models.Post;
import application.models.Thread;
import application.utils.requests.CreatePostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

    @SuppressWarnings({"unused", "ThrowInsideCatchBlockWhichIgnoresCaughtException"})
    public List<Post> createPosts(List<CreatePostRequest> request, Thread thread) {
//        if (request.isEmpty()) {
//            return request;
//        }
        final String query = "INSERT INTO posts (author, created, forum, isEdited, message, parent, thread_id) "
                + "VALUES (?, ?::TIMESTAMPTZ, ?, ?, ?, ?, ?) RETURNING *";
        final String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        final String forum = thread.getForum();
        final List<Post> response = new ArrayList<>();
        for (final CreatePostRequest post : request) {
            if (post.getParent() != null && post.getParent() != 0) {
                try {
                    final Post parentPost = this.getPostById(post.getParent());
//                if (post.getParent() != null && !Objects.equals(this.getThreadIdById(post.getParent()), post.getThread()))
                } catch (EmptyResultDataAccessException e) {
                    throw new NoSuchElementException("Any parentpost field missed");
                }
            } else {
                post.setParent(0L);
            }
//            try {
//                if (post.getParent() != null && !Objects.equals(this.getThreadIdById(post.getParent()), post.getThread())) {
//                    throw new DataIntegrityViolationException("thread exception");
//                }
//            } catch (EmptyResultDataAccessException e) {
//                throw new DataIntegrityViolationException(e.getMessage());
//            }
            response.add(template.queryForObject(query, POST_ROW_MAPPER, post.getAuthor(), created, forum,
                    false, post.getMessage(), post.getParent(), thread.getId()));
//            INSERT INTO forum_members (user_id, forum_id) VALUES ((SELECT id FROM users WHERE lower(NEW.author) = lower(nickname)),
//                    (SELECT id FROM forums WHERE lower(NEW.forum) = lower(slug)));
            final String forumMembersQuery = "INSERT INTO forum_members (user_id, forum_id) VALUES "
                    + " ((SELECT id FROM users WHERE LOWER(nickname) = LOWER(?)), (SELECT id FROM forums WHERE LOWER(slug) = LOWER(?)))";
            template.update(forumMembersQuery, post.getAuthor(), forum);
        }
        template.update("UPDATE forums SET posts = posts + ? WHERE LOWER(slug) = LOWER(?)", request.size(), forum);
        return response;
    }
}
