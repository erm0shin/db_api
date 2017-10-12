package application.controllers;

import application.models.User;
import application.services.UserService;
import application.utils.requests.UserRequest;
import application.utils.responses.BadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/{nickname}/create")
    public ResponseEntity createUser(@PathVariable String nickname,
                                     @RequestBody UserRequest request) {
        try {
            final User user = userService.createUser(request.getEmail(), request.getFullname(), nickname, request.getAbout());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userService.getUsersByNicknameOrEmail(nickname, request.getEmail()));
        }
    }

    @GetMapping(path = "/{nickname}/profile")
    public ResponseEntity getProfile(@PathVariable String nickname) {
        try {
            final User user = userService.getUserByNickname(nickname);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such user"));
        }
    }

    @PostMapping(path = "/{nickname}/profile")
    public ResponseEntity updateProfile(@PathVariable String nickname,
                                        @RequestBody UserRequest request) {
        try {
            final User user = userService.updateProfile(request.getEmail(), request.getFullname(), nickname, request.getAbout());
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BadResponse("Can't find such user"));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new BadResponse("Conflict with other user's data"));
        }
    }
}
