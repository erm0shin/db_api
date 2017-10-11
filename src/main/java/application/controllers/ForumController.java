package application.controllers;

import application.models.Forum;
import application.models.Thread;
import application.models.User;
import application.services.ForumService;
import application.services.ThreadService;
import application.services.UserService;
import application.utils.requests.CreateForumRequest;
import application.utils.requests.CreateThreadRequest;
import application.utils.responses.SuccessForumResponse;
import application.utils.responses.SuccessThreadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        //Сделать проверку на существование пользователя
        //Сделать проверку на уже присутствие такого форума
        final Forum forum = forumService.createForum(request.getSlug(), request.getTitle(), request.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessForumResponse(forum));
    }

    @PostMapping(path = "/{slug}/create")
    public ResponseEntity createThread(@PathVariable String slug,
                                       @RequestBody CreateThreadRequest request) {
        //Сделать проверку на существование автора и форума
        //Сделать проверку на уже присутствие такой ветки
        final application.models.Thread thread = threadService.createThread(request.getAuthor(), request.getCreated(),
                request.getMessage(), request.getTitle(), request.getSlug(), slug);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessThreadResponse(thread));
    }

    @GetMapping(path = "{slug}/details")
    public ResponseEntity getForumDetails(@PathVariable String slug) {
        //Сделать проверку на присутствие форума в системе
        final Forum forum = forumService.getForumDetails(slug);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessForumResponse(forum));
    }

    @GetMapping(path = "{slug}/threads")
    public ResponseEntity getForumThreads(@PathVariable String slug,
                                          @RequestParam(value = "limit") Integer limit,
                                          @RequestParam(value = "since") String since,
                                          @RequestParam(value = "desc") Boolean desc) {
        //Сделать проверку на существование форума в системе
        final List<Thread> threads = threadService.getForumThreads(slug, limit, since, desc);
        return ResponseEntity.status(HttpStatus.OK).body(threads);
    }

    @GetMapping(path = "{slug}/users")
    public ResponseEntity getForumUsers(@PathVariable String slug,
                                        @RequestParam(value = "limit") Integer limit,
                                        @RequestParam(value = "since") String since,
                                        @RequestParam(value = "desc") Boolean desc) {
        //Сделать проверку на существование форума в системе
        final List<User> users = userService.getForumMembers(slug, limit, since, desc);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

}
