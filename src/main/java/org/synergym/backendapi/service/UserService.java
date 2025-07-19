package org.synergym.backendapi.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.BadgeDTO;
import org.synergym.backendapi.dto.FinalGoalsDTO;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;

import java.io.IOException;
import java.util.List;

public interface UserService {

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * @param id 사용자 ID
     * @return 사용자 정보 DTO
     */
    UserDTO getUserById(int id);

    /**
     * 이메일로 사용자 정보를 조회합니다.
     * @param email 사용자 이메일
     * @return 사용자 정보 DTO
     */
    UserDTO getUserByEmail(String email);

    /**
     * 전체 사용자 목록을 조회합니다.
     * @return 모든 사용자 정보 리스트
     */
    List<UserDTO> getAllUsers();

    /**
     * 사용자 정보를 수정합니다.
     * @param id 사용자 ID
     * @param userDTO 수정할 사용자 정보 DTO
     * @param profileImage 새 프로필 이미지 (nullable)
     * @param removeImage 기존 이미지 제거 여부
     * @return 수정된 사용자 정보 DTO
     * @throws IOException 이미지 처리 중 예외 발생 가능
     */
    UserDTO updateUser(int id, UserDTO userDTO, MultipartFile profileImage, boolean removeImage) throws IOException;

    /**
     * 사용자 계정을 소프트 삭제합니다.
     * (이메일 알림도 함께 발송)
     * @param id 사용자 ID
     */
    void deleteUserById(int id);

    /**
     * 이름 키워드로 사용자 검색
     * @param name 이름 키워드
     * @return 검색된 사용자 리스트
     */
    List<UserDTO> searchUsersByName(String name);

    /**
     * ID로 사용자 엔티티를 직접 조회 (DTO 아님)
     * @param id 사용자 ID
     * @return User 엔티티
     */
    User findUserEntityById(int id);


    void saveUserGoals(Integer userId, FinalGoalsDTO goalsDTO) throws JsonProcessingException;


    /**
     * 특정 사용자가 획득한 모든 뱃지 목록 조회
     * @param userId 사용자 ID
     * @return 뱃지 DTO 리스트
     */
    List<BadgeDTO> getUserBadges(int userId);


    default UserDTO entityToDTO(User user){
        String profileImageUrl = null;
        if (user.getProfileImage() != null) {
            String baseUrl = "http://localhost:8081"; // 실제 배포 시에는 설정 파일에서 관리
            profileImageUrl = baseUrl + "/api/users/" + user.getId() + "/profile-image";
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .goal(user.getGoal())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .weight(user.getWeight())
                .height(user.getHeight())
                .role(user.getRole())
                .profileImageUrl(profileImageUrl)
                .password(null)
                .weeklyGoal(user.getWeeklyGoal())
                .monthlyGoal(user.getMonthlyGoal())
                .build();
    }

    default User DTOtoEntity(UserDTO dto){
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .goal(dto.getGoal())
                .birthday(dto.getBirthday())
                .gender(dto.getGender())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .role(dto.getRole())
                .password(dto.getPassword())
                .build();
    }
}
