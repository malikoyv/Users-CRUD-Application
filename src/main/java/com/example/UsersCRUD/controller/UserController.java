package com.example.UsersCRUD.controller;

import com.example.UsersCRUD.service.UserService;
import com.example.UsersCRUD.model.User;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Setter
@RestController
public class UserController {
    @Value("${minAge}")
    private String age;

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST user method
    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User user){
        String ageValue = age != null ? age : "18";
        return userService.addUser(user, ageValue);
    }

    // UPDATE user's field
    @PutMapping("/updateUsersFieldById/{id}/{field}/{value}")
    public ResponseEntity<User> updateUsersFieldById(@PathVariable long id, @PathVariable String field, @PathVariable String value){
        return userService.updateUsersFieldById(id, field, value);
    }

    // UPDATE user's full info
    @PutMapping("/updateUserFullById/{id}")
    public ResponseEntity<User> updateUserFullById(@PathVariable long id, @RequestBody User user) {
        return userService.updateUserFullById(id, user);
    }

    // DELETE user
    @DeleteMapping("/deleteUserById/{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable long id){
        return userService.deleteUserById(id);
    }

    // GET users by birthdate range
    @GetMapping("/getUsersByRange/{start}/{end}")
    public ResponseEntity<List<User>> getUsersByRange(@PathVariable String start, @PathVariable String end) {
        return userService.getUsersByRange(start, end);
    }
}