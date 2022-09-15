package com.egs.hibernate.assigment.core.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity(name = "users")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @BatchSize(size = 5)
    private Set<PhoneNumber> phoneNumbers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @BatchSize(size = 5)
    private Set<Address> addresses;
}