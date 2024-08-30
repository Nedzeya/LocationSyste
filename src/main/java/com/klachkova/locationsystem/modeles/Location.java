package com.klachkova.locationsystem.modeles;

import com.klachkova.locationsystem.util.annotations.USAddress;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
/**
 * Represents a location in the system.
 *
 * <p>This class maps to the "Location" table in the database. It contains details about a location,
 * including its name, address, owner, and users with whom the location is shared.</p>
 */
@Entity
@Table(name = "Location")
public class Location {
    /**
     * Unique identifier for the location.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * Name of the location.
     * <p>Must not be blank and must be between 2 and 30 characters in length.</p>
     */
    @Column(name = "name")
    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name;
    /**
     * Address of the location.
     * <p>Must not be blank and must follow the US address format (e.g., '123 Main St, Springfield, IL, 62704').</p>
     */
    @NotBlank(message = "Address should not be empty")
    @USAddress(message = "Address must be in US format (ex.: '123 Main St, Springfield, IL, 62704')")
    private String address;
    /**
     * The user who owns the location.
     * <p>Must not be null.</p>
     */
    @ManyToOne
    @NotNull(message = "Owner should not be empty")
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User owner;
    /**
     * List of users with whom the location is shared.
     */
    @OneToMany
    private List<User> sharedUsers;
    /**
     * Default constructor.
     */
    public Location() {
    }
    /**
     * Constructs a new Location with the specified id, name, address, and owner.
     *
     * @param id       the unique identifier of the location
     * @param name     the name of the location
     * @param address  the address of the location
     * @param owner    the user who owns the location
     */
    public Location(Integer id, String name, String address, User owner) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(List<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", owner=" + owner +
                ", sharedUsers=" + sharedUsers +
                '}';
    }
}
