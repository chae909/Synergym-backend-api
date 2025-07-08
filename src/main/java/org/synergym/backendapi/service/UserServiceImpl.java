package org.synergym.backendapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDTO getUserById(int id) {
        User user = findUserById(id);
        return entityToDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        return entityToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(int id, UserDTO userDTO, MultipartFile profileImage, boolean removeImage) throws IOException {
        User user = findUserById(id);

        user.updateName(userDTO.getName());
        user.updateGoal(userDTO.getGoal());
        user.updateBirthday(userDTO.getBirthday());
        user.updateGender(userDTO.getGender());
        user.updateHeight(userDTO.getHeight());
        user.updateWeight(userDTO.getWeight());

        if (removeImage) {
            user.removeProfileImage();
        } else if (profileImage != null && !profileImage.isEmpty()) {
            user.updateProfileImage(
                    profileImage.getBytes(),
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType()
            );
        }

        return entityToDTO(user);
    }

    @Override
    @Transactional
    public void deleteUserById(int id) {
        // 1. DB에서 사용자 정보를 조회합니다.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 임시 강제탈퇴 사유
        String reason = "서비스 운영 정책 위반";

        // 닉네임과 사유를 인자로 추가하여 이메일 발송 메소드 호출
        emailService.sendForcedWithdrawalEmail(user.getEmail(), user.getName(), reason);

        // 3. 사용자를 소프트 삭제 처리합니다.
        user.softDelete();
    }


    @Override
    public List<UserDTO> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name).stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public User findUserEntityById(int id) {
        // UserRepository를 사용해 ID로 User 엔티티를 찾습니다.
        // 만약 사용자가 없다면, 예외를 발생시킵니다.
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
