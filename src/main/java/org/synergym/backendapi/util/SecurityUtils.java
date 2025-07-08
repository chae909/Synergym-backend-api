package org.synergym.backendapi.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.synergym.backendapi.dto.UserDTO;

@Component
public class SecurityUtils {
    
    /**
     * 현재 인증된 사용자의 이메일을 반환
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                return (String) principal; // JWT 필터에서 설정한 이메일
            } else if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
        }
        return null;
    }
    
    /**
     * 현재 인증된 사용자가 ADMIN 권한을 가지고 있는지 확인
     */
    public static boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
    
    /**
     * 현재 사용자가 특정 사용자 ID에 접근할 권한이 있는지 확인
     * (본인이거나 관리자인 경우)
     */
    public static boolean canAccessUser(String userEmail) {
        String currentUserEmail = getCurrentUserEmail();
        return currentUserEmail != null && 
               (currentUserEmail.equals(userEmail) || isCurrentUserAdmin());
    }
    
    /**
     * 현재 사용자가 특정 사용자 ID에 접근할 권한이 있는지 확인
     * (본인이거나 관리자인 경우)
     */
    public static boolean canAccessUserId(int userId, org.synergym.backendapi.service.UserService userService) {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            return false;
        }
        
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // 현재 사용자의 ID와 요청된 ID가 같은지 확인
        try {
            UserDTO currentUser = userService.getUserByEmail(currentUserEmail);
            return currentUser != null && currentUser.getId() == userId;
        } catch (Exception e) {
            return false;
        }
    }
}
