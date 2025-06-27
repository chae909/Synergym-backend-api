package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        UserDTO userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }
}
