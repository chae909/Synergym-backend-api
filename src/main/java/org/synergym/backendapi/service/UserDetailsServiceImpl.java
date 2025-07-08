package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.synergym.backendapi.repository.UserRepository;

/**
 * Spring Security가 사용자 인증 시 DB에서 사용자 정보를 조회하기 위해 사용하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 이메일(username)을 기반으로 사용자 정보를 로드합니다.
     * AuthenticationManager가 인증을 수행할 때 이 메서드를 호출합니다.
     * @param email 사용자가 로그인 시 입력한 이메일
     * @return UserDetails 객체 (사용자 정보, 비밀번호, 권한 포함)
     * @throws UsernameNotFoundException 해당 이메일의 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // DB에서 이메일로 사용자 정보 조회
        return userRepository.findByEmail(email)
                .map(this::createUserDetails) // 사용자가 있으면 UserDetails 객체 생성
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다. - " + email));
    }

    // User 엔티티를 Spring Security가 사용하는 UserDetails 객체로 변환합니다.
    private UserDetails createUserDetails(org.synergym.backendapi.entity.User user) {
        return User.builder()
                .username(user.getEmail()) // Spring Security에서 username은 고유 식별자를 의미합니다.
                .password(user.getPassword()) // DB에 저장된 암호화된 비밀번호
                .roles(user.getRole().name()) // "ROLE_" 접두사는 Spring Security가 자동으로 추가해 줍니다.
                .build();
    }
}
