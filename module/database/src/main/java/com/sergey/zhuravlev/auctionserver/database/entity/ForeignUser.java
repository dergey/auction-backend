package com.sergey.zhuravlev.auctionserver.database.entity;

import com.sergey.zhuravlev.auctionserver.database.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "foreign_users")
@DiscriminatorValue("FOREIGN")
public class ForeignUser extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

}
