package com.sergey.zhuravlev.auctionserver.database.entity;

import com.sergey.zhuravlev.auctionserver.database.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "principal_id", nullable = false)
    private Principal principal;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, insertable = false, updatable = false)
    private UserType userType;

}
