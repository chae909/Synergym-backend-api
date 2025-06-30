package org.synergym.backendapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.ChangePasswordRequest;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
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

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDTO getUserById(int id) {
        return entityToDTO(findUserById(id));
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
        User user = findUserById(id);
        userRepository.delete(user);
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
