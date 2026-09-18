package com.campusflow.strategy;

import com.campusflow.model.Role;
import com.campusflow.model.User;

/**
 * High-priority allocation strategy favoring official department conferences, guest lectures, and faculty-led research.
 */
public class FacultyPriorityAllocationStrategy implements PriorityAllocationStrategy {

    @Override
    public int calculatePriorityScore(User requester, long durationMinutes, boolean isAcademicPurpose) {
        int score = 50;
        if (requester.getRole() == Role.ADMIN) {
            score += 40;
        } else if (requester.getRole() == Role.FACULTY) {
            score += 25;
        }
        if (isAcademicPurpose) {
            score += 20;
        }
        return score;
    }
}
