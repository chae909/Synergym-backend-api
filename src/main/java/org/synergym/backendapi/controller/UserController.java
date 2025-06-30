package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.ChangePasswordRequest;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        UserDTO userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable int userId,
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("{}번 사용자의 비밀번호 변경 요청", userId);
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}
