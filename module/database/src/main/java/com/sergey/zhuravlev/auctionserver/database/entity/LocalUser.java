package com.sergey.zhuravlev.auctionserver.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_local")
@DiscriminatorValue("LOCAL")
public class LocalUser extends User {

    @Column(name = "password", length = 100, nullable = false)
    private String password;

}
