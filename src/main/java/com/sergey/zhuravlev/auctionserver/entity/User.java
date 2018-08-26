package com.sergey.zhuravlev.auctionserver.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "username", length = 40, nullable = false, unique = true)
    private String username;
    @Column(name = "password", length = 100, nullable = false)
    private String password;
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Column(name = "firstname", length = 40, nullable = false)
    private String firstname;
    @Column(name = "lastname", length = 40, nullable = false)
    private String lastname;
    @Column(name = "email", length = 40, nullable = false, unique = true)
    private String email;
    @Column(name = "rating", nullable = false, length = 5)
    private Byte rating;
    @Column(name = "history")
    private String history;
    @Column(name = "notification_token")
    private String notificationToken;

}
