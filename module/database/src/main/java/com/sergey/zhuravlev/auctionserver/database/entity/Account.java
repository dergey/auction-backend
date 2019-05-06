package com.sergey.zhuravlev.auctionserver.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "username", length = 40, nullable = false, unique = true)
    private String username;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Image photo;

    @Column(name = "firstname", length = 40, nullable = false)
    private String firstname;

    @Column(name = "lastname", length = 40, nullable = false)
    private String lastname;

    @Column(name = "average_stars", scale = 2, precision = 2)
    private BigDecimal averageStars;

    @Column(name = "bio", length = 200)
    private String bio;

}
