package com.campusflow.service;

import com.campusflow.exception.CampusFlowException;
import com.campusflow.exception.ValidationException;
import com.campusflow.model.Facility;
import com.campusflow.model.ResourceType;
import com.campusflow.repository.DataStore;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing campus facility inventory, capacity audits, and maintenance states.
 */
public class FacilityService {
    private final DataStore dataStore;

    public FacilityService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void registerFacility(String id, String name, ResourceType type, int capacity, String block, double cost)
            throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "Facility ID cannot be empty.");
        }
        if (dataStore.findFacilityById(id).isPresent()) {
            throw new ValidationException("id", "A facility with ID '" + id + "' already exists.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("name", "Facility name cannot be empty.");
        }
        if (capacity <= 0) {
            throw new ValidationException("capacity", "Capacity must be positive integer.");
        }

        Facility facility = new Facility(id.trim(), name.trim(), type, capacity, block, cost);
        dataStore.saveFacility(facility);
        dataStore.addAuditLog("Facility registered: " + facility.getName() + " [" + facility.getId() + "]");
    }

    public Optional<Facility> getFacility(String id) {
        return dataStore.findFacilityById(id);
    }

    public Collection<Facility> getAllFacilities() {
        return dataStore.getAllFacilities();
    }

    public List<Facility> findAvailableByTypeAndMinCapacity(ResourceType type, int minCapacity) {
        return dataStore.getAllFacilities().stream()
                .filter(f -> !f.isUnderMaintenance())
                .filter(f -> type == null || f.getType() == type)
                .filter(f -> f.getCapacity() >= minCapacity)
                .collect(Collectors.toList());
    }

    public void setMaintenanceMode(String facilityId, boolean inMaintenance, String reason) throws CampusFlowException {
        Facility f = dataStore.findFacilityById(facilityId)
                .orElseThrow(() -> new ValidationException("facilityId", "Facility not found: " + facilityId));

        f.setUnderMaintenance(inMaintenance);
        dataStore.addAuditLog(String.format("Facility %s maintenance set to %b. Reason: %s", facilityId, inMaintenance, reason));
    }
}
