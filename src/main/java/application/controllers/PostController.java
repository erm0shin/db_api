package application.controllers;

import application.models.Post;
import application.services.ForumService;
import application.services.PostService;
import application.services.ThreadService;
import application.services.UserService;
import application.utils.requests.UpdatePostMessageRequest;
import application.utils.responses.SuccessPostDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/post")
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
                                         @RequestParam(value = "related") List<String> related) {
        //Проверить существование поста
        //Проверить присутствие каких-либо данных в базе
        final SuccessPostDetailsResponse response = new SuccessPostDetailsResponse();
        final Post post = postService.getPostById(id);
        response.setPost(post);
        if (related != null) {
            if (related.contains("user")) {
                response.setAuthor(userService.getUserByNickname(post.getAuthor()));
            }
            if (related.contains("post")) {
                response.setForum(forumService.getForumDetails(post.getForum()));
            }
            if (related.contains("thread")) {
                response.setThread(threadService.getThreadById(post.getThread()));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "{id}/details")
    public ResponseEntity updatePost(@PathVariable Long id,
                                     @RequestBody UpdatePostMessageRequest request) {
        //if (request.getMessage() != null)
        //Сделать проверку на существование поста
        final Post post = postService.updatePostMessage(id, request.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }
}
