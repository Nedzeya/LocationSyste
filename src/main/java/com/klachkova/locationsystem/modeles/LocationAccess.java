package com.klachkova.locationsystem.modeles;

import com.klachkova.locationsystem.util.annotations.ValidAccessLevel;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
/**
 * Represents the access permissions of a user to a specific location in the system.
 *
 * <p>This class maps to the "LocationAccess" table in the database and includes information about
 * which user has access to which location and at what access level.</p>
 */
@Entity
@Table(name = "LocationAccess")
public class LocationAccess {
    /**
     * Unique identifier for the location access entry.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The user who has access to the location.
     * <p>Must not be null.</p>
     */
    @ManyToOne
    @NotNull(message = "User should not be empty")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * The location to which the user has access.
     * <p>Must not be null.</p>
     */
    @ManyToOne
    @NotNull (message = "Location should not be empty")
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
    /**
     * The level of access the user has to the location.
     * <p>Must not be null and must be a valid access level as defined by {@link ValidAccessLevel}.</p>
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Access level must not be null")
    @ValidAccessLevel
    private AccessLevel accessLevel;

    /**
     * Default constructor.
     */
    public LocationAccess() {
    }
    /**
     * Constructs a new LocationAccess with the specified user, location, and access level.
     *
     * @param user        the user with access to the location
     * @param location    the location to which access is granted
     * @param accessLevel the level of access granted
     */
    public LocationAccess( User user, Location location, AccessLevel accessLevel) {
        this.user = user;
        this.location = location;
        this.accessLevel = accessLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
