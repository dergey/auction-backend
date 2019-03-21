package com.sergey.zhuravlev.auctionserver.entity;

import com.sergey.zhuravlev.auctionserver.enums.LotStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lots")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 250)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<Image> images;

    @Column(name = "create_at", nullable = false)
    private Date createAt;

    @Column(name = "update_at", nullable = false)
    private Date updateAt;

    @Column(name = "complete_at")
    private Date completeAt;

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    @Column(name = "starting_amount", nullable = false)
    private BigDecimal startingAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "auction_step", nullable = false)
    private Long auctionStep;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_bid_id")
    private Bid currentBid;

    @Column(name = "status", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private LotStatus status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "category_id")
    private Category category;

}