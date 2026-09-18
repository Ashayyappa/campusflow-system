package com.campusflow.strategy;

import com.campusflow.model.User;

/**
 * Standard allocation strategy used for student club activities and routine reservations.
 */
public class StandardAllocationStrategy implements PriorityAllocationStrategy {

    @Override
    public int calculatePriorityScore(User requester, long durationMinutes, boolean isAcademicPurpose) {
        int base = 10;
        // Academic purpose adds weight
        if (isAcademicPurpose) {
            base += 15;
        }
        // Shorter slots get slight scheduling agility bonus
        if (durationMinutes <= 120) {
            base += 5;
        }
        return base;
    }
}
