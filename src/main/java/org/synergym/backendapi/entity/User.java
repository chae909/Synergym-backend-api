package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="Users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name="email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    private String goal;
    private LocalDate birthday;
    private String gender;
    private Float weight;
    private Float height;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob // Large Object: BLOB, CLOB 타입 매핑
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "profile_image_file_name")
    private String profileImageFileName;

    @Column(name = "profile_image_content_type")
    private String profileImageContentType;

    @Column(name = "provider")
    private String provider;

    @Builder
    public User(int id, String email, String password, String name, String goal, LocalDate birthday, String gender, Float weight, Float height, Role role, String provider) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.goal = goal;
        this.birthday = birthday;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.role = role;
        this.provider = provider;
    }

    public void updateBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void updateGender(String gender) {
        this.gender = gender;
    }

    public void updateWeight(Float weight) {
        this.weight = weight;
    }

    public void updateHeight(Float height) {
        this.height = height;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateGoal(String newGoal) {
        this.goal = newGoal;
    }

    public void updateProfileImage(byte[] profileImage, String fileName, String contentType) {
        this.profileImage = profileImage;
        this.profileImageFileName = fileName;
        this.profileImageContentType = contentType;
    }

    public void removeProfileImage() {
        this.profileImage = null;
        this.profileImageFileName = null;
        this.profileImageContentType = null;
    }

    public void updateProvider(String newProvider) {
        this.provider = newProvider;
    }
}
