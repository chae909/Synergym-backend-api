package org.synergym.backendapi.service.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final String email;
    private final Role role;
    private final boolean isNewUser; // 신규 사용자인지 여부를 판단하는 플래그
    private final Map<String, Object> attributes;

    // 기존 사용자를 위한 생성자
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.email = user.getEmail();
        this.role = user.getRole();
        this.attributes = attributes;
        this.isNewUser = false;
    }

    // 추가 정보 입력이 필요한 신규 사용자를 위한 생성자
    public CustomOAuth2User(String email, Map<String, Object> attributes) {
        this.email = email;
        this.role = Role.MEMBER; // 임시 역할 부여
        this.attributes = attributes;
        this.isNewUser = true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 우리 시스템의 Role을 Spring Security의 권한 형식으로 변환
        return Collections.singleton((GrantedAuthority) () -> role.name());
    }

    @Override
    public String getName() {
        // OAuth2 표준에서 name은 사용자를 식별하는 고유 ID를 의미합니다.
        // 여기서는 이메일을 사용하거나, 필요에 따라 다른 값을 사용할 수 있습니다.
        return this.email;
    }
}
