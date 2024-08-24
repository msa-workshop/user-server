package com.example.userserver.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private RestClient restClient = RestClient.create();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserInfo signUpUser(@RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable("id") int id) {
        UserInfo user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<UserInfo> getUserInfoByName(@PathVariable("name") String name) {
        UserInfo user = userService.getUserByName(name);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/signIn")
    public UserInfo signIn(@RequestBody UserRequest signInRequest) {
        return userService.signIn(signInRequest);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable("id") int id) {

        return userService.deleteUser(id);
    }
}
