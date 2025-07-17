// org/synergym/backendapi/service/AchievementService.java
package org.synergym.backendapi.service;

public interface AchievementService {
    void awardBadgeToUser(int userId, String badgeName, String badgeDescription);
}