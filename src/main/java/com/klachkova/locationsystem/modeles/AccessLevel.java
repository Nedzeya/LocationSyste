package com.klachkova.locationsystem.modeles;
/**
 * Represents the access levels that can be assigned to users for a location.
 * <p>
 * This enumeration defines the various levels of access a user can have to a location.
 * </p>
 */
public enum AccessLevel {
    /**
     * Read-only access level.
     * <p>
     * Users with this access level can only view the location.
     * </p>
     */
    READ_ONLY,
    /**
     * Admin access level.
     * <p>
     * Admin access allows friend user to add other friends to the owner's location.
     * </p>
     */
    ADMIN
}
