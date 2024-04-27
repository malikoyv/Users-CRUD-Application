package com.example.UsersCRUD.model;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import java.time.LocalDate;

@Entity
@Table(name="Users")
@NoArgsConstructor
@Setter
@Getter
@ToString
public class User {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    private String address;
    private String phoneNumber;

    public User(long id, String email, String firstName, String lastName, LocalDate birthDate) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }
}
