package com.klachkova.locationsystem.modeles;

import com.klachkova.locationsystem.util.annotations.USAddress;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "Location")
public class Location {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "name")
    @NotBlank(message = "Name should not be empty")
    @Size(min = 3, max = 30,
            message = "Name should be between 3 and 30 characters")
    private String name;

    @NotBlank(message = "Address should not be empty")
    @USAddress(message = "Address must be in US format (ex.: '123 Main St, Springfield, IL, 62704')")
    private String address;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

    @OneToMany
    private List<User> sharedUsers;

    public Location() {
    }

    public Location(Integer id, String name, String address, User owner) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.owner = owner;
    }

    public List<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(List<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
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
}
