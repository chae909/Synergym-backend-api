package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.ChangePasswordRequest;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.service.UserService;

import java.io.IOException;

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

    @GetMapping("/social/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable int id,
            @RequestPart("userDTO") UserDTO userDTO, // Postman KEY: userDTO
            @RequestPart(name = "profileImage", required = false) MultipartFile profileImage, // Postman KEY: profileImage
            @RequestParam(name = "removeImage", defaultValue = "false") boolean removeImage) throws IOException {
        UserDTO updatedUser = userService.updateUser(id, userDTO, profileImage, removeImage);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable int id) {
        log.info(">>>>> getProfileImage 호출됨, 사용자 ID: {}", id);

        try {
            // ✨ 해결책: DTO가 아닌 'User' 엔티티를 직접 조회해야 합니다.
            // 이전에 만든 findUserEntityById 메서드를 사용합니다.
            User user = userService.findUserEntityById(id);

            byte[] imageBytes = user.getProfileImage();

            if (imageBytes == null) {
                log.warn(">>>>> ID {} 사용자의 프로필 이미지가 DB에 없어 404를 반환합니다.", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(user.getProfileImageContentType()));

            log.info(">>>>> ID {} 사용자의 이미지 반환 성공.", id);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error(">>>>> getProfileImage 처리 중 에러 발생: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
