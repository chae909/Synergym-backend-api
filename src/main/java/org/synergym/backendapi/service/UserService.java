package org.synergym.backendapi.service;


import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDTO createUser(UserDTO userDTO, MultipartFile profileImage) throws IOException;
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(String email, UserDTO userDTO, MultipartFile profileImage, boolean removeImage) throws IOException;
    void deleteUserByEmail(String email);
    List<UserDTO> searchUsersByName(String name);
    Optional<User> findUserEntityByEmail(String email);

    default UserDTO entityToDTO(User user){
        String profileImageUrl = (user.getProfileImage() != null)
                ? "api/users" + user.getId() + "/profile-image"
                : null;

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .goal(user.getGoal())
                .profileImageUrl(profileImageUrl)
                .password(null)
                .build();
    }

    default User DTOtoEntity(UserDTO dto){
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .goal(dto.getGoal())
                .password(dto.getPassword())
                .build();
    }
}
