package org.synergym.backendapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.service.UserService;

import java.nio.charset.StandardCharsets;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

//    @Test
//    void createUser() throws Exception {
//        UserDTO newUserDTO = UserDTO.builder()
//                .email("test@test.com")
//                .name("공명")
//                .password("pw123")
//                .goal("자세교정")
//                .build();
//
//        ClassPathResource resource = new ClassPathResource("test-image.jpg");
//        byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
//
//        MockMultipartFile imageFile = new MockMultipartFile(
//                "profileImage",
//                "test-image.jpg",
//                "image/jpg",
//                "test-image".getBytes(StandardCharsets.UTF_8));
//
//        UserDTO createdUser = userService.createUser(newUserDTO, imageFile);
//
//        System.out.println(createdUser);
//    }


    @Test
    void deleteUserByEmail() throws Exception {
        userService.deleteUserById(4);
    }


    @Test
    void updateUserImage() throws Exception {
        UserDTO updateInfo = UserDTO.builder()
                .name("공명태")
                .goal("거북목교정")
                .build();
        MockMultipartFile newFile = new MockMultipartFile(
                "profileImage2",
                "new-test-image.jpg",
                "image/jpg",
                "new-test-image".getBytes(StandardCharsets.UTF_8));

        UserDTO updatedUser = userService.updateUser(5, updateInfo, newFile, false);
    }
}
