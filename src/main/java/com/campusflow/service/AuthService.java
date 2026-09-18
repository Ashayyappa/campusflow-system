package com.campusflow.service;

import com.campusflow.exception.UnauthorizedException;
import com.campusflow.exception.ValidationException;
import com.campusflow.model.Role;
import com.campusflow.model.User;
import com.campusflow.repository.DataStore;

import java.util.Optional;

/**
 * Service managing user authentication, registration, and permission checks.
 */
public class AuthService {
    private final DataStore dataStore;
    private User currentUser;

    public AuthService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public User login(String email, String password) throws ValidationException, UnauthorizedException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", "Email address cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("password", "Password cannot be empty.");
        }

        Optional<User> userOpt = dataStore.findUserByEmail(email.trim());
        if (!userOpt.isPresent()) {
            throw new UnauthorizedException("Invalid email or user does not exist: " + email);
        }

        User user = userOpt.get();
        if (!user.getPasswordHash().equals(password)) {
            throw new UnauthorizedException("Invalid password credentials for: " + email);
        }

        this.currentUser = user;
        dataStore.addAuditLog(String.format("User %s (%s) logged in successfully.", user.getFullName(), user.getId()));
        return user;
    }

    public void logout() {
        if (currentUser != null) {
            dataStore.addAuditLog(String.format("User %s (%s) logged out.", currentUser.getFullName(), currentUser.getId()));
            this.currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void requireRole(Role minimumRole) throws UnauthorizedException {
        if (currentUser == null) {
            throw new UnauthorizedException("No active session. Please authenticate first.");
        }
        if (currentUser.getRole().getAccessLevel() < minimumRole.getAccessLevel()) {
            throw new UnauthorizedException(currentUser.getId(), minimumRole.getDisplayName(), "Protected Admin Operation");
        }
    }
}
