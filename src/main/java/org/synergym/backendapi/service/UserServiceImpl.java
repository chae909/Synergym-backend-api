package org.synergym.backendapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.BadgeDTO;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.entity.UserBadge;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.UserBadgeRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserBadgeRepository userBadgeRepository;

    // 생성자를 통한 의존성 주입
    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailService emailService, UserBadgeRepository userBadgeRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userBadgeRepository = userBadgeRepository;
    }

    // ID로 User 조회, 없으면 예외 발생
    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 사용자 ID로 단일 사용자 조회
     */
    @Override
    public UserDTO getUserById(int id) {
        User user = findUserById(id);
        return entityToDTO(user);
    }

    /**
     * 이메일로 사용자 조회
     */
    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        return entityToDTO(user);
    }

    /**
     * 모든 사용자 리스트 반환
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 정보 업데이트 (프로필 이미지 포함)
     *
     * @param id 사용자 ID
     * @param userDTO 변경할 사용자 정보
     * @param profileImage 새 프로필 이미지 파일 (nullable)
     * @param removeImage 기존 이미지 제거 여부
     */
    @Override
    @Transactional
    public UserDTO updateUser(int id, UserDTO userDTO, MultipartFile profileImage, boolean removeImage) throws IOException {
        User user = findUserById(id);

        // 기본 정보 업데이트
        user.updateName(userDTO.getName());
        user.updateGoal(userDTO.getGoal());
        user.updateBirthday(userDTO.getBirthday());
        user.updateGender(userDTO.getGender());
        user.updateHeight(userDTO.getHeight());
        user.updateWeight(userDTO.getWeight());

        // 프로필 이미지 처리
        if (removeImage) {
            user.removeProfileImage(); // 이미지 제거
        } else if (profileImage != null && !profileImage.isEmpty()) {
            user.updateProfileImage(
                    profileImage.getBytes(),
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType()
            );
        }

        return entityToDTO(user);
    }

    /**
     * 사용자 삭제 (소프트 삭제 및 이메일 알림 포함)
     */
    @Override
    @Transactional
    public void deleteUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 탈퇴 사유 예시
        String reason = "서비스 운영 정책 위반";

        // 강제 탈퇴 이메일 발송
        emailService.sendForcedWithdrawalEmail(user.getEmail(), user.getName(), reason);

        // 소프트 삭제 (isDeleted 필드 등을 활용한 논리적 삭제)
        user.softDelete();
    }

    /**
     * 이름으로 사용자 검색 (포함 검색)
     */
    @Override
    public List<UserDTO> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ID로 User 엔티티를 직접 반환 (DTO 아님)
     */
    @Override
    public User findUserEntityById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void saveUserGoals(Integer userId, String weeklyGoal, String monthlyGoal) {
        User user = findUserEntityById(userId);

        user.updateWeeklyGoal(weeklyGoal);
        user.updateMonthlyGoal(monthlyGoal);
        
        userRepository.save(user);
    }

    /**
     * 특정 사용자가 획득한 모든 뱃지 목록 조회 로직 구현
     */
    @Override
    @Transactional(readOnly = true)
    public List<BadgeDTO> getUserBadges(int userId) {
        // 1. 사용자 존재 여부 확인 (선택적이지만 좋은 습관)
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. UserBadge 리포지토리를 사용해 사용자가 획득한 뱃지 연결 정보 조회
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);

        // 3. 연결 정보(UserBadge) 리스트에서 실제 뱃지(Badge) 정보만 추출하여 DTO로 변환
        return userBadges.stream()
                .map(userBadge -> BadgeDTO.fromEntity(userBadge.getBadge()))
                .collect(Collectors.toList());
    }
}
