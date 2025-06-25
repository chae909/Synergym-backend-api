package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "goal", length = 255)
    private String goal;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Builder
    public User(int id, String email, String password, String name, String goal, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.goal = goal;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
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

    public void updateProfileImageUrl(String newProfileImageUrl) {
        this.profileImageUrl = newProfileImageUrl;
    }
}
