package com.klachkova.locationsystem.modeles;

import com.klachkova.locationsystem.util.annotations.ValidAccessLevel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "LocationAccess")
public class LocationAccess {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotBlank (message = "User should not be empty")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @NotBlank (message = "Location should not be empty")
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Access level cannot be empty")
    @ValidAccessLevel (message = "Invalid access level. It should be READ_ONLY or ADMIN")
    private AccessLevel accessLevel;

    public LocationAccess() {
    }

    public LocationAccess( User user, Location location, AccessLevel accessLevel) {
        this.user = user;
        this.location = location;
        this.accessLevel = accessLevel;
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
