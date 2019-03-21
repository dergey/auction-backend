package com.sergey.zhuravlev.auctionserver.entity;

import lombok.*;
import javax.persistence.*;

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
