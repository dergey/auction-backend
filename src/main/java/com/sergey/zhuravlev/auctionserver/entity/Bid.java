package com.sergey.zhuravlev.auctionserver.entity;

import com.sergey.zhuravlev.auctionserver.enums.BidStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_at", nullable = false)
    private Date createAt;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "status", length = 12, nullable = false)
    @Enumerated(EnumType.STRING)
    private BidStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

}
