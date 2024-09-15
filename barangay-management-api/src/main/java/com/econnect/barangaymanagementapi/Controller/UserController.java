package com.econnect.barangaymanagementapi.Controller;

import com.econnect.barangaymanagementapi.Domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@AllArgsConstructor
@Slf4j
public class UserController {

    private final HttpServletRequest httpServletRequest;

    private ArrayList<User> listUsers = new ArrayList<>();

    @GetMapping("/user")
    public ArrayList<User> getUser() {
        log.info("Getting user: {}",httpServletRequest.getRequestURL().toString());
        return listUsers;
    }

    @PostMapping("/user")
    public User postUser(@RequestBody User user) {
        log.info("Posting User: {}", httpServletRequest.getRequestURI());
        listUsers.add(user);
        return user;
    }
}
