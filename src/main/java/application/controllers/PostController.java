package application.controllers;

import application.models.Post;
import application.services.ForumService;
import application.services.PostService;
import application.services.ThreadService;
import application.services.UserService;
import application.utils.requests.UpdatePostMessageRequest;
import application.utils.responses.BadResponse;
import application.utils.responses.SuccessPostDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/post")
public class PostController {

    private PostService postService;
    private UserService userService;
    private ForumService forumService;
    private ThreadService threadService;

    @Autowired
    public PostController(PostService postService, UserService userService,
                          ForumService forumService, ThreadService threadService) {
        this.postService = postService;
        this.userService = userService;
        this.forumService = forumService;
        this.threadService = threadService;
    }

    @GetMapping(path = "/{id}/details")
    public ResponseEntity getPostDetails(@PathVariable Long id,
                                         @RequestParam(value = "related", required = false) List<String> related) {
        try {
            final SuccessPostDetailsResponse response = new SuccessPostDetailsResponse();
            final Post post = postService.getPostById(id);
            response.setPost(post);
            if (related != null) {
                if (related.contains("user")) {
                    response.setAuthor(userService.getUserByNickname(post.getAuthor()));
                }
                if (related.contains("forum")) {
                    response.setForum(forumService.getForumDetails(post.getForum()));
                }
                if (related.contains("thread")) {
                    response.setThread(threadService.getThreadById(post.getThread()));
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find data"));
        }
    }

    @SuppressWarnings("unused")
    @PostMapping(path = "{id}/details")
    public ResponseEntity updatePost(@PathVariable Long id,
                                     @RequestBody UpdatePostMessageRequest request) {
        try {
            final Post post = postService.getPostById(id);
            final Post updatedPost = postService.updatePostMessage(id, request.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(updatedPost);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such post"));
        }
    }
}
