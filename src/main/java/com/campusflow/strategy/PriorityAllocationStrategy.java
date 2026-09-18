package com.campusflow.strategy;

import com.campusflow.model.Booking;
import com.campusflow.model.User;

/**
 * Strategy interface defining how booking priority scores and scheduling precedence are calculated.
 */
public interface PriorityAllocationStrategy {
    /**
     * Calculates an integer priority weight for a reservation request.
     * Higher numbers represent higher scheduling priority.
     *
     * @param requester the user requesting the facility
     * @param durationMinutes duration of the proposed timeslot in minutes
     * @param isAcademicPurpose whether the booking is for academic/research purposes
     * @return calculated priority score
     */
    int calculatePriorityScore(User requester, long durationMinutes, boolean isAcademicPurpose);
}
