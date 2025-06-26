package org.synergym.backendapi.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO, MultipartFile profileImage) throws IOException {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        User newUser = DTOtoEntity(userDTO);

        if (profileImage != null && !profileImage.isEmpty()) {
            newUser.updateProfileImage(
                    profileImage.getBytes(),
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType()
            );
        }

        User savedUser = userRepository.save(newUser);
        return entityToDTO(savedUser);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::entityToDTO)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. 이메일: " + email));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(String email, UserDTO userDTO, MultipartFile profileImage, boolean removeImage) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. 이메일: " + email));

        user.updateName(userDTO.getName());
        user.updateGoal(userDTO.getGoal());

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
    public void deleteUserByEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new EntityNotFoundException("삭제할 사용자가 없습니다. 이메일: "+ email);
        }
        userRepository.delete(userRepository.findByEmail(email).get());
    }

    @Override
    public List<UserDTO> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name).stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
