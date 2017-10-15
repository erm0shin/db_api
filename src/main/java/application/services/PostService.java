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
import java.util.Arrays;
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
//            created     TIMESTAMPTZ,
//            forum       TEXT,
//            isEdited    BOOLEAN       NOT NULL DEFAULT FALSE,
//            message     TEXT          NOT NULL,
//            parent      BIGINT,
//            thread_id   INT           NOT NULL,
//            path        BIGINT []
//    );

    @SuppressWarnings("unchecked")
    private static final RowMapper<Post> POST_ROW_MAPPER = (res, num) -> new Post(res.getLong("id"),
            res.getString("author"), LocalDateTime.ofInstant(res.getTimestamp("created").toInstant(),
            ZoneOffset.ofHours(0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
            res.getString("forum"), res.getBoolean("isEdited"), res.getString("message"),
            res.getLong("parent"), res.getInt("thread_id"), Arrays.asList(res.getArray("path")));

    public Post getPostById(Long id) {
        final String query = "SELECT * FROM posts p WHERE p.id = ?";
        return template.queryForObject(query, POST_ROW_MAPPER, id);
    }

    public Post updatePostMessage(Long id, String message) {
        final String query = "UPDATE posts p SET p.isEdited = ?, p.message = ? WHERE p.id = ? RETURNING *";
        return template.queryForObject(query, POST_ROW_MAPPER, true, message, id);
    }

//    public List<Post> createBatch(List<Post> postsInfo, ThreadModel thread) throws SQLException {
//        final String query = "INSERT INTO posts " +
//                "(id, author, created, forum, is_edited, message, parent, path, thread_id)" +
//                " VALUES (?, (SELECT u.nickname FROM users u WHERE lower(u.nickname) = lower(?)), ?::TIMESTAMPTZ, " +
//                "(SELECT f.slug FROM forums f WHERE lower(f.slug) = lower(?)), ?, ?, ?," +
//                " (SELECT path FROM posts WHERE id = ?) || ?::BIGINT, ?) " ;
//
//        final String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
//        try(Connection conn = template.getDataSource().getConnection();
//            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.NO_GENERATED_KEYS)) {
//
//            for (final Post post : postsInfo) {
//                post.setId(template.queryForObject("SELECT nextval('posts_id_seq')", Long.class));
//                post.setForum(thread.getForum());
//                post.setThread(thread.getId());
//                post.setCreated(created);
//                try {
//                    if (post.getParent() != null && !Objects.equals(this.getThreadIdById(post.getParent()), post.getThread())) {
//                        throw new DataIntegrityViolationException("thread exception");
//                    }
//                } catch (EmptyResultDataAccessException e) {
//                    throw new DataIntegrityViolationException(e.getMessage());
//                }
//                preparedStatement.setLong(1, post.getId());
//                preparedStatement.setString(2, post.getAuthor());
//                preparedStatement.setString(3, post.getCreated());
//                preparedStatement.setString(4, post.getForum());
//                preparedStatement.setBoolean(5, post.getIsEdited());
//                preparedStatement.setString(6, post.getMessage());
//                preparedStatement.setObject(7, post.getParent());
//                preparedStatement.setObject(8, post.getParent());
//                preparedStatement.setLong(9, post.getId());
//                preparedStatement.setLong(10, post.getThread());
//                preparedStatement.addBatch();
//            }
//
//            preparedStatement.executeBatch();
//            template.update("UPDATE forums SET posts = posts + ? " +
//                    "WHERE lower(slug) = lower(?)", postsInfo.size(), thread.getForum());
//        }
//        return postsInfo;
//    }

    @SuppressWarnings({"unused", "ThrowInsideCatchBlockWhichIgnoresCaughtException"})
    public List<Post> createPosts(List<CreatePostRequest> request, Thread thread) {
        final String query = "INSERT INTO posts (id, author, created, forum, isEdited, message, parent, thread_id, path) "
                + "VALUES (?, ?, ?::TIMESTAMPTZ, ?, ?, ?, ?, ?, (SELECT path FROM posts p WHERE p.id = ?) || ?::BIGINT) RETURNING *";
        final String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        final String forum = thread.getForum();
        final List<Post> response = new ArrayList<>();
        for (final CreatePostRequest post : request) {
            final Long newId = template.queryForObject("SELECT nextval('posts_id_seq')", Long.class);
//            post.setId(template.queryForObject("SELECT nextval('posts_id_seq')", Long.class));
            if (post.getParent() != null && post.getParent() != 0) {
                try {
                    final Post parentPost = this.getPostById(post.getParent());
                } catch (EmptyResultDataAccessException e) {
                    throw new NoSuchElementException("Any parentpost field missed");
                }
            } else {
                post.setParent(0L);
            }
            response.add(template.queryForObject(query, POST_ROW_MAPPER, newId, post.getAuthor(), created, forum,
                    false, post.getMessage(), post.getParent(), thread.getId(), post.getParent(), newId));
            final String forumMembersQuery = "INSERT INTO forum_members (user_id, forum_id) VALUES "
                    + " ((SELECT id FROM users WHERE LOWER(nickname) = LOWER(?)), (SELECT id FROM forums WHERE LOWER(slug) = LOWER(?)))";
            template.update(forumMembersQuery, post.getAuthor(), forum);
        }
        template.update("UPDATE forums SET posts = posts + ? WHERE LOWER(slug) = LOWER(?)", request.size(), forum);
        return response;
//        final String query = "INSERT INTO posts (author, created, forum, isEdited, message, parent, thread_id) "
//                + "VALUES (?, ?::TIMESTAMPTZ, ?, ?, ?, ?, ?) RETURNING *";
//        final String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
//        final String forum = thread.getForum();
//        final List<Post> response = new ArrayList<>();
//        for (final CreatePostRequest post : request) {
//            if (post.getParent() != null && post.getParent() != 0) {
//                try {
//                    final Post parentPost = this.getPostById(post.getParent());
//                } catch (EmptyResultDataAccessException e) {
//                    throw new NoSuchElementException("Any parentpost field missed");
//                }
//            } else {
//                post.setParent(0L);
//            }
//            response.add(template.queryForObject(query, POST_ROW_MAPPER, post.getAuthor(), created, forum,
//                    false, post.getMessage(), post.getParent(), thread.getId()));
//            final String forumMembersQuery = "INSERT INTO forum_members (user_id, forum_id) VALUES "
//                    + " ((SELECT id FROM users WHERE LOWER(nickname) = LOWER(?)), (SELECT id FROM forums WHERE LOWER(slug) = LOWER(?)))";
//            template.update(forumMembersQuery, post.getAuthor(), forum);
//        }
//        template.update("UPDATE forums SET posts = posts + ? WHERE LOWER(slug) = LOWER(?)", request.size(), forum);
//        return response;
    }

//    private String getSortQuery(String order, Boolean desc) {
//        final StringBuilder query = new StringBuilder();
//        query.append("SELECT * FROM posts p WHERE p.thread_id = ? ORDER BY ").append(order);
//        if (desc) {
//            query.append(" DESC ");
//        } else {
//            query.append(" ASC ");
//        }
//        query.append("LIMIT ? OFFSET ?");
//        return query.toString();
//    }

    public List<Post> getPostsSortedFlat(Integer threadId, Long limit, Long since, Boolean desc) {
        final String order = (desc ? " DESC " : " ASC ");
        final String sign = (desc ? " < " : " > ");

        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM posts p WHERE p.thread_id = ? ");
        if (since != null) {
            query.append("AND p.id").append(sign).append("? ");
        }
        query.append("ORDER BY p.id").append(order).append("LIMIT ?");
        if (since != null) {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, since, limit);
        } else {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, limit);
        }

//        final String query = this.getSortQuery("p.id", desc);
//        return template.query(query, POST_ROW_MAPPER, threadId, limit, since);
    }

    public List<Post> getPostsSortedTree(Integer threadId, Long limit, Long since, Boolean desc) {
        final String order = (desc ? " DESC " : " ASC ");
        final String sign = (desc ? " < " : " > ");

        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM posts p WHERE p.thread_id = ? ");
        if (since != null) {
            query.append("AND p.path").append(sign).append("(SELECT p.path FROM posts p where p.id = ?) ");
        }
        query.append("ORDER BY p.path").append(order).append("LIMIT ?");
        if (since != null) {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, since, limit);
        } else {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, limit);
        }


//        final String query = this.getSortQuery("p.path", desc);
//        return template.query(query, POST_ROW_MAPPER, threadId, limit, since);
    }

//    public List<PostData> parentTree(int threadId, int offset, int limit, boolean desc) {
//        String order = (desc ? "DESC" : "ASC");
//
//        return this.jdbc.query(
//                "WITH Roots AS (" +
//                        "SELECT path FROM Posts WHERE thread = ? AND parent = 0 " +
//                        "ORDER BY" +
//                        " id " + order +
//                        " LIMIT ? OFFSET ?" +
//                        ") SELECT " +
//                        " Posts.id, Posts.author, Posts.forum, Posts.created, Posts.message, Posts.thread, " +
//                        "   Posts.parent, Posts.isEdited " +
//                        "FROM Posts JOIN Roots ON Roots.path <@ Posts.path WHERE thread = ?" +
//                        " ORDER BY " +
//                        "   Posts.path " + order,
//                new PostData(),
//                threadId, limit, offset, threadId
//        );
//    }

    public List<Post> getPostsSortedParentTree(Integer threadId, Long limit, Long since, Boolean desc) {
        final String order = (desc ? " DESC " : " ASC ");
        final String sign = (desc ? " < " : " > ");

//        final StringBuilder query = new StringBuilder();
//        query.append("SELECT * FROM posts p WHERE p.thread_id = ? ");
//        if (since != null) {
//            query.append("AND p.path[1] IN (SELECT p.id FROM posts p WHERE p.)")
//        }
//
//
        final StringBuilder query = new StringBuilder();
        query.append("WITH sub_table AS (SELECT path FROM posts WHERE thread_id = ? AND parent = 0 ");
        if (since != null) {
            query.append("AND path").append(sign).append("(SELECT path FROM posts where id = ?) ");
        }
        query.append("ORDER BY id").append(order).append("LIMIT ?) ");

        //наверное можно убрать вторую проверку на ветку!!!

        query.append("SELECT * FROM posts p JOIN sub_table s ON (s.path <@ p.path) WHERE p.thread_id = ? ORDER BY p.path").append(order);
        if (since != null) {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, since, limit, threadId);
        } else {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, limit, threadId);
        }



//        final StringBuilder query = new StringBuilder();
//        query.append("WITH sub_table AS (SELECT p.path FROM posts p WHERE p.thread_id = ? AND p.parent = 0 ORDER BY p.id ");
//        if (desc) {
//            query.append(" DESC ");
//        } else {
//            query.append(" ASC ");
//        }
//
//        //наверное можно убрать вторую проверку на ветку!!!
//
//        query.append("LIMIT ? OFFSET ?) ")
//                .append("SELECT * FROM posts p JOIN sub_table s ON (s.path <@ p.path) WHERE p.thread_id = ? ORDER BY p.path ");
//        if (desc) {
//            query.append(" DESC ");
//        } else {
//            query.append(" ASC ");
//        }
//        return template.query(query.toString(), POST_ROW_MAPPER, threadId, limit, since, threadId);
    }
}
