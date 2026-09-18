package com.campusflow.service;

import com.campusflow.model.Booking;
import com.campusflow.model.BookingStatus;
import com.campusflow.model.Facility;
import com.campusflow.repository.DataStore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics engine computing resource utilization, booking demand, and operational metrics.
 */
public class AnalyticsService {
    private final DataStore dataStore;

    public AnalyticsService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Map<String, Object> generateExecutiveSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        Collection<Facility> facilities = dataStore.getAllFacilities();
        Collection<Booking> bookings = dataStore.getAllBookings();

        long totalFacilities = facilities.size();
        long activeMaintenance = facilities.stream().filter(Facility::isUnderMaintenance).count();
        long totalBookings = bookings.size();

        long confirmedCount = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long pendingCount = bookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count();
        long rejectedCount = bookings.stream().filter(b -> b.getStatus() == BookingStatus.REJECTED).count();
        long cancelledCount = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();

        // Facility booking frequency
        Map<String, Long> bookingCountsPerFacility = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getFacilityId, Collectors.counting()));

        String mostRequestedFacilityId = bookingCountsPerFacility.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        String mostRequestedFacilityName = dataStore.findFacilityById(mostRequestedFacilityId)
                .map(Facility::getName)
                .orElse("None");

        summary.put("totalFacilities", totalFacilities);
        summary.put("activeMaintenance", activeMaintenance);
        summary.put("operationalFacilities", totalFacilities - activeMaintenance);
        summary.put("totalBookings", totalBookings);
        summary.put("confirmedBookings", confirmedCount);
        summary.put("pendingBookings", pendingCount);
        summary.put("rejectedBookings", rejectedCount);
        summary.put("cancelledBookings", cancelledCount);
        summary.put("mostRequestedFacility", mostRequestedFacilityName + " (" + mostRequestedFacilityId + ")");

        double approvalRate = totalBookings > 0 ? ((double) confirmedCount / totalBookings) * 100.0 : 0.0;
        summary.put("approvalRatePercentage", String.format("%.1f%%", approvalRate));

        return summary;
    }

    public String formatSummaryReport() {
        Map<String, Object> s = generateExecutiveSummary();
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("         CAMPUSFLOW SYSTEM ANALYTICS & INSIGHTS          \n");
        sb.append("=========================================================\n");
        sb.append(String.format("  Total Facilities Managed       : %s\n", s.get("totalFacilities")));
        sb.append(String.format("  Operational Facilities         : %s\n", s.get("operationalFacilities")));
        sb.append(String.format("  Facilities Under Maintenance   : %s\n", s.get("activeMaintenance")));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format("  Total Reservations Logged      : %s\n", s.get("totalBookings")));
        sb.append(String.format("  Confirmed Active Bookings      : %s\n", s.get("confirmedBookings")));
        sb.append(String.format("  Pending Review Bookings        : %s\n", s.get("pendingBookings")));
        sb.append(String.format("  Rejected / Cancelled Bookings  : %s / %s\n", s.get("rejectedBookings"), s.get("cancelledBookings")));
        sb.append(String.format("  System Booking Approval Rate   : %s\n", s.get("approvalRatePercentage")));
        sb.append(String.format("  High Demand Facility Hotspot   : %s\n", s.get("mostRequestedFacility")));
        sb.append("=========================================================\n");
        return sb.toString();
    }
}
