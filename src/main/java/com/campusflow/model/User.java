package com.campusflow.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing an authenticated user in the campus system.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String fullName;
    private String email;
    private String passwordHash;
    private Role role;
    private String department;

    public User(String id, String fullName, String email, String passwordHash, Role role, String department) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.department = department != null ? department : "General";
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s [%s]", fullName, id, role.getDisplayName(), department);
    }
}
