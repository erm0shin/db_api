package api.services;

import api.models.Post;
import api.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Service
@Transactional
public class PostService {

    private JdbcTemplate template;
    private UserService userService;

    @Autowired
    public PostService(JdbcTemplate template, UserService userService) {
        this.template = template;
        this.userService = userService;
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
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
        final Post oldPost = this.getPostById(id);
        if (message == null || oldPost.getMessage().equals(message)) {
            return oldPost;
        }
        final String query = "UPDATE posts SET isEdited = ?, message = COALESCE(?, message) WHERE id = ? RETURNING *";
        return template.queryForObject(query, POST_ROW_MAPPER, true, message, id);
    }


    @SuppressWarnings({"unused", "ThrowInsideCatchBlockWhichIgnoresCaughtException", "ConstantConditions", "JDBCResourceOpenedButNotSafelyClosed", "TooBroadScope"})
    public List<Post> createPosts(List<Post> request, Thread thread) throws SQLException {
        final String postQuery = "INSERT INTO posts (id, author, created, forum, isEdited, message, parent, thread_id, path) "
                + "VALUES (?, ?, ?::TIMESTAMPTZ, ?, ?, ?, ?, ?, (SELECT path FROM posts p WHERE p.id = ?) || ?::BIGINT) RETURNING *";
        final String forumMembersQuery = "INSERT INTO forum_members (user_id, forum_id) VALUES "
                + " ((SELECT id FROM users WHERE LOWER(nickname) = LOWER(?)), (SELECT id FROM forums WHERE LOWER(slug) = LOWER(?)))";
        final String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        final String forum = thread.getForum();
        final List<Post> response = new ArrayList<>();
        try (final Connection postConnection = template.getDataSource().getConnection();
            final PreparedStatement postPS = postConnection.prepareStatement(postQuery, Statement.NO_GENERATED_KEYS);
            final Connection forumMembersConnection = template.getDataSource().getConnection();
            final PreparedStatement forumMembersPS = forumMembersConnection.prepareStatement(forumMembersQuery, Statement.NO_GENERATED_KEYS)) {
            for (final Post post : request) {
                final Long newId = template.queryForObject("SELECT nextval('posts_id_seq')", Long.class);
                if (post.getParent() != null && post.getParent() != 0) {
                    try {
                        final Integer parentPostThread = template.queryForObject("SELECT p.thread_id FROM posts p WHERE p.id = ?",
                                Integer.class, post.getParent());
                        if (!Objects.equals(parentPostThread, thread.getId())) {
                            throw new NoSuchElementException("Parent post was created in another thread");
                        }
                    } catch (EmptyResultDataAccessException e) {
                        throw new NoSuchElementException("Any parentpost field missed");
                    }
                } else {
                    post.setParent(0L);
                }
                post.setId(newId);
                post.setAuthor(userService.getUserByNickname(post.getAuthor()).getNickname());
                post.setCreated(created);
                post.setForum(forum);
                post.setIsEdited(false);
                post.setThread(thread.getId());

                postPS.setLong(1, newId);
                postPS.setString(2, post.getAuthor());
                postPS.setString(3, created);
                postPS.setString(4, forum);
                postPS.setBoolean(5, false);
                postPS.setString(6, post.getMessage());
                postPS.setLong(7, post.getParent());
                postPS.setInt(8, thread.getId());
                postPS.setLong(9, post.getParent());
                postPS.setLong(10, newId);
                postPS.addBatch();
                response.add(post);

                forumMembersPS.setString(1, post.getAuthor());
                forumMembersPS.setString(2, forum);
                forumMembersPS.addBatch();
            }
            postPS.executeBatch();
            postConnection.close();
            forumMembersPS.executeBatch();
            forumMembersConnection.close();
        }
        template.update("UPDATE forums SET posts = posts + ? WHERE LOWER(slug) = LOWER(?)", request.size(), forum);
        return response;
    }

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
    }

    public List<Post> getPostsSortedParentTree(Integer threadId, Long limit, Long since, Boolean desc) {
        final String order = (desc ? " DESC " : " ASC ");
        final String sign = (desc ? " < " : " > ");

        final StringBuilder query = new StringBuilder();
        query.append("WITH sub_table AS (SELECT path FROM posts WHERE thread_id = ? AND parent = 0 ");
        if (since != null) {
            query.append("AND path").append(sign).append("(SELECT path FROM posts where id = ?) ");
        }
        query.append("ORDER BY id").append(order).append("LIMIT ?) ")
                .append("SELECT * FROM posts p JOIN sub_table s ON (s.path <@ p.path) ORDER BY p.path").append(order);
        if (since != null) {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, since, limit);
        } else {
            return template.query(query.toString(), POST_ROW_MAPPER, threadId, limit);
        }
    }
}
