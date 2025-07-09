package org.synergym.backendapi.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.dto.WeeklyMonthlyStats;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.service.ExerciseLogService;
import org.synergym.backendapi.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ExerciseLogService exerciseLogService;

    // 모든 유저 조회
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 유저 고유 아이디로 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        UserDTO userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    // 소셜로그인유저 - 이메일로 조회
    @GetMapping("/social/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    // 유저 정보 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable int id,
            @RequestPart("userDTO") UserDTO userDTO, // Postman KEY: userDTO
            @RequestPart(name = "profileImage", required = false) MultipartFile profileImage, // Postman KEY: profileImage
            @RequestParam(name = "removeImage", defaultValue = "false") boolean removeImage) throws IOException {
        UserDTO updatedUser = userService.updateUser(id, userDTO, profileImage, removeImage);
        return ResponseEntity.ok(updatedUser);
    }

    // 유저 프로필 이미지 조회
    @GetMapping("/{id}/profile-image")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getProfileImage(@PathVariable int id) {
        log.info(">>>>> getProfileImage 호출됨, 사용자 ID: {}", id);

        try {
            // 'User' 엔티티를 직접 조회
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

    // 유저의 운동 통계 조회
    @GetMapping("{userId}/exercise-stats")
    public ResponseEntity<WeeklyMonthlyStats> getExerciseStats(@PathVariable Integer userId) {
        // 현재 날짜 기준으로 이번주/이번달 통계 계산
        LocalDate now = LocalDate.now();

        // 이번주 시작일 (월요일)
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // 이번달 시작일/끝일
        LocalDate monthStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = now.with(TemporalAdjusters.lastDayOfMonth());

        // DB에서 통계 조회 (Integer를 Long으로 변환)
        WeeklyMonthlyStats stats = exerciseLogService.getStats(userId, weekStart, weekEnd, monthStart, monthEnd);

        return ResponseEntity.ok(stats);
    }

    // 유저 삭제 (soft-delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
