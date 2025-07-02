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
    User findUserEntityById(int id);


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
