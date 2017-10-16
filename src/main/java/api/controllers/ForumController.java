package api.controllers;

import api.models.Forum;
import api.models.Thread;
import api.models.User;
import api.services.ForumService;
import api.services.ThreadService;
import api.services.UserService;
import api.utils.requests.CreateForumRequest;
import api.utils.requests.CreateThreadRequest;
import api.utils.responses.BadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping(path = "/api/forum")
public class ForumController {

    private ForumService forumService;
    private ThreadService threadService;
    private UserService userService;

    @Autowired
    public ForumController(ForumService forumService, ThreadService threadService, UserService userService) {
        this.forumService = forumService;
        this.threadService = threadService;
        this.userService = userService;
    }

    @PostMapping(path = "/create")
    public ResponseEntity createForum(@RequestBody CreateForumRequest request) {
        try {
            final String nickname = userService.getUserByNickname(request.getUser()).getNickname();
            final Forum forum = forumService.createForum(request.getSlug(), request.getTitle(), /*request.getUser()*/nickname);
            return ResponseEntity.status(HttpStatus.CREATED).body(forum);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such user"));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumService.getForumDetails(request.getSlug()));
        }
    }

    @PostMapping(path = "/{slug}/create")
    public ResponseEntity createThread(@PathVariable String slug,
                                       @RequestBody CreateThreadRequest request) {
        try {
            final User user = userService.getUserByNickname(request.getAuthor());
            final Forum forum = forumService.getForumDetails(slug);
            final Thread thread = threadService.createThread(request.getAuthor(), request.getCreated(),
                    request.getMessage(), request.getTitle(), request.getSlug(), forum.getSlug());
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such user or forum"));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadService.getThreadBySlug(request.getSlug()));
        }
    }

    @GetMapping(path = "{slug}/details")
    public ResponseEntity getForumDetails(@PathVariable String slug) {
        try {
            final Forum forum = forumService.getForumDetails(slug);
            return ResponseEntity.status(HttpStatus.OK).body(forum);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such forum"));
        }
    }

    @GetMapping(path = "{slug}/threads")
    public ResponseEntity getForumThreads(@PathVariable String slug,
                                          @RequestParam(value = "limit", required = false) Integer limit,
                                          @RequestParam(value = "since", required = false) String since,
                                          @RequestParam(value = "desc", required = false) Boolean desc) {
        try {
            final Forum forum = forumService.getForumDetails(slug);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such forum"));
        }
        try {
            final List<Thread> threads = threadService.getForumThreads(slug, limit, since, desc);
            return ResponseEntity.status(HttpStatus.OK).body(threads);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

    @GetMapping(path = "{slug}/users")
    public ResponseEntity getForumUsers(@PathVariable String slug,
                                        @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
                                        @RequestParam(value = "since", required = false) String since,
                                        @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc) {
        final Long forumId;
        try {
            forumId = forumService.getForumDetails(slug).getId();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such forum"));
        }
        try {
            final List<User> users = userService.getForumMembers(forumId, limit, since, desc);
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }
}
