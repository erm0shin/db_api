package application.controllers;

import application.models.Post;
import application.models.Thread;
import application.models.User;
import application.services.PostService;
import application.services.ThreadService;
import application.services.UserService;
import application.utils.requests.UpdateThreadRequest;
import application.utils.requests.VoteRequest;
import application.utils.responses.BadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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
            return ResponseEntity.status(HttpStatus.OK).body(postService.createPosts(request, thread));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new BadResponse(e.getMessage()));
        }
    }

    @GetMapping(path = "/{slug_or_id}/details")
    public ResponseEntity getPostDetails(@PathVariable String slug_or_id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(threadService.getThreadBySlugOrId(slug_or_id));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        }
    }

    @PostMapping(path = "/{slug_or_id}/details")
    public ResponseEntity updatePostDetails(@PathVariable String slug_or_id,
                                            @RequestBody UpdateThreadRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(threadService.updateThreadDetails(slug_or_id, request.getMessage(), request.getTitle()));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        }
    }

    @PostMapping(path = "/{slug_or_id}/vote")
    public ResponseEntity voteThread(@PathVariable String slug_or_id,
                                     @RequestBody VoteRequest request) {
        try {
            final Long userId = userService.getUserByNickname(request.getNickname()).getId();
            return ResponseEntity.status(HttpStatus.OK).body(threadService.voteThread(slug_or_id, userId, request.getVoice()));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such thread"));
        }
    }
}
