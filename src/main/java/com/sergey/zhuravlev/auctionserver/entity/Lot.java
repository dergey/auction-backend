package com.sergey.zhuravlev.auctionserver.entity;

import com.sergey.zhuravlev.auctionserver.enums.Status;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "lots")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "image")
    private String image;
    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;
    @Column(name = "starting_price", nullable = false)
    private Double startingPrice;
    @Column(name = "auction_step", nullable = false)
    private Double auctionStep;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}

