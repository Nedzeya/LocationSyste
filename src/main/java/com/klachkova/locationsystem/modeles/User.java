package com.klachkova.locationsystem.modeles;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "User")
public class User implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "name")
    @NotBlank(message = "Name should not be empty")
    @Size(min = 3, max = 30,
            message = "Name should be between 3 and 30 characters")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")

    private String email;

    @OneToMany
    private List<Location> locations;

    public User() {
    }

    public User( String name, String email) {
        this.name = name;
        this.email = email;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }


}
