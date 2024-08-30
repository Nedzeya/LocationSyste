package com.klachkova.locationsystem.modeles;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
/**
 * Represents a user in the system.
 *
 * <p>This class maps to the "User" table in the database. It contains user details and a list of locations associated
 * with the user.</p>
 */

@Entity
@Table(name = "User")
public class User {
    /**
     * Unique identifier for the user.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * Name of the user.
     * <p>Must not be blank and must be between 2 and 30 characters in length.</p>
     */
    @Column(name = "name")
    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name;
    /**
     * Email of the user.
     * <p>Must not be blank and must be a valid email address.</p>
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")
    private String email;
    /**
     * List of locations associated with the user.
     */
    @OneToMany
    private List<Location> locations;
    /**
     * Default constructor.
     */
    public User() {
    }
    /**
     * Constructs a new User with the specified name and email.
     *
     * @param name  the name of the user
     * @param email the email of the user
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", locations=" + locations +
                '}';
    }
}
