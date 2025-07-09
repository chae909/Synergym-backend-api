package org.synergym.backendapi.dto;

import java.util.List;

public class UserSignupStatsResponse {
    private List<MonthlySignupStats> monthly;

    public UserSignupStatsResponse(List<MonthlySignupStats> monthly) {
        this.monthly = monthly;
    }

    public List<MonthlySignupStats> getMonthly() {
        return monthly;
    }

    public void setMonthly(List<MonthlySignupStats> monthly) {
        this.monthly = monthly;
    }

    public static class MonthlySignupStats {
        private int month;
        private List<Integer> weeks;

        public MonthlySignupStats(int month, List<Integer> weeks) {
            this.month = month;
            this.weeks = weeks;
        }

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