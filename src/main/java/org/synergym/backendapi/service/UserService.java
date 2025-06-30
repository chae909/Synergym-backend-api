package org.synergym.backendapi.service;


import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.ChangePasswordRequest;
import org.synergym.backendapi.dto.LoginRequest;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDTO getUserById(int id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(int id, UserDTO userDTO, MultipartFile profileImage, boolean removeImage) throws IOException;
    void deleteUserById(int id);
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
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .weight(user.getWeight())
                .height(user.getHeight())
                .role(user.getRole())
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
                .birthday(dto.getBirthday())
                .gender(dto.getGender())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .role(dto.getRole())
                .password(dto.getPassword())
                .build();
    }
}
