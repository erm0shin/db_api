package api.controllers;

import api.models.Post;
import api.models.Thread;
import api.services.PostService;
import api.services.ThreadService;
import api.services.UserService;
import api.utils.requests.UpdateThreadRequest;
import api.utils.requests.VoteRequest;
import api.utils.responses.BadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@SuppressWarnings("MethodParameterNamingConvention")
@RestController
@RequestMapping(path = "/api/thread")
public class ThreadController {

    private PostService postService;
    private ThreadService threadService;
    private UserService userService;

    @Autowired
    public ThreadController(PostService postService, ThreadService threadService, UserService userService) {
        this.postService = postService;
        this.threadService = threadService;
        this.userService = userService;
    }

    @PostMapping(path = "/{slug_or_id}/create")
    public ResponseEntity createPosts(@PathVariable String slug_or_id,
                                      @RequestBody List<Post> request) {
        try {
            final Thread thread = threadService.getThreadBySlugOrId(slug_or_id);
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPosts(request, thread));
        } catch (EmptyResultDataAccessException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new BadResponse(e.getMessage()));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Error"));
        }
    }

    @GetMapping(path = "/{slug_or_id}/details")
    public ResponseEntity getPostDetails(@PathVariable String slug_or_id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(threadService.getThreadBySlugOrId(slug_or_id));
        } catch (EmptyResultDataAccessException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        }
    }

    @PostMapping(path = "/{slug_or_id}/details")
    public ResponseEntity updatePostDetails(@PathVariable String slug_or_id,
                                            @RequestBody UpdateThreadRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(threadService.updateThreadDetails(slug_or_id, request.getMessage(), request.getTitle()));
        } catch (EmptyResultDataAccessException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        }
    }

    @PostMapping(path = "/{slug_or_id}/vote")
    public ResponseEntity voteThread(@PathVariable String slug_or_id,
                                     @RequestBody VoteRequest request) {
        try {
            final Long userId = userService.getUserByNickname(request.getNickname()).getId();
            return ResponseEntity.status(HttpStatus.OK).body(threadService.voteThread(slug_or_id, userId, request.getVoice()));
        } catch (EmptyResultDataAccessException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        }
    }

    @GetMapping(path = "/{slug_or_id}/posts")
    public ResponseEntity getPosts(@PathVariable String slug_or_id,
                                   @RequestParam(value = "limit") Long limit,
                                   @RequestParam(value = "since", required = false) Long since,
                                   @RequestParam(value = "sort", required = false, defaultValue = "flat") String sort,
                                   @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc) {
        final Integer threadId;
        try {
            threadId = threadService.getThreadBySlugOrId(slug_or_id).getId();
        } catch (EmptyResultDataAccessException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        }
        List<Post> posts = null;
        if (Objects.equals(sort, "flat")) {
            posts = postService.getPostsSortedFlat(threadId, limit, since, desc);
        }
        if (Objects.equals(sort, "tree")) {
            posts = postService.getPostsSortedTree(threadId, limit, since, desc);
        }
        if (Objects.equals(sort, "parent_tree")) {
            posts = postService.getPostsSortedParentTree(threadId, limit, since, desc);
        }
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
}
