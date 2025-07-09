package org.synergym.backendapi.dto;

import java.util.List;

// 회원가입 통계 응답 DTO : 관리자 대시보드에서 월간/주간 가입자 추이를 표시하기 위한 데이터 구조
public class UserSignupStatsResponse {
    private List<MonthlySignupStats> monthly; // 월별 회원가입 통계

    // 생성자
    public UserSignupStatsResponse(List<MonthlySignupStats> monthly) {
        this.monthly = monthly;
    }

    // Getter/Setter
    public List<MonthlySignupStats> getMonthly() {
        return monthly;
    }

    
    public void setMonthly(List<MonthlySignupStats> monthly) {
        this.monthly = monthly;
    }

    // 월별 회원가입 통계 내부 클래스 : 각 월의 주간별 가입자 수를 관리
    public static class MonthlySignupStats {
        private int month; // 월
        private List<Integer> weeks; // 주

        // 생성자
        public MonthlySignupStats(int month, List<Integer> weeks) {
            this.month = month;
            this.weeks = weeks;
        }

        // Getter/Setter
        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public List<Integer> getWeeks() {
            return weeks;
        }

        public void setWeeks(List<Integer> weeks) {
            this.weeks = weeks;
        }
    }
} 