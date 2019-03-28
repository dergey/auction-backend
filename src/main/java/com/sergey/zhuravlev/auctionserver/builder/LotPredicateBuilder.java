package com.sergey.zhuravlev.auctionserver.builder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.entity.Account;
import com.sergey.zhuravlev.auctionserver.entity.QBid;
import com.sergey.zhuravlev.auctionserver.entity.QLot;
import com.sergey.zhuravlev.auctionserver.enums.LotStatus;

import java.sql.Date;


public class LotPredicateBuilder {

    private static final QLot lotQuery = QLot.lot;
    private static final QBid bidQuery = QBid.bid;
    private final BooleanBuilder builder;

    public LotPredicateBuilder() {
        builder = new BooleanBuilder();
    }

    public LotPredicateBuilder withStatus(LotStatus lotStatus) {
        if (lotStatus != null) {
            builder.and(lotQuery.status.eq(lotStatus));
        }
        return this;
    }

    public LotPredicateBuilder withOwner(Account owner) {
        if (owner != null) {
            builder.and(lotQuery.owner.eq(owner));
        }
        return this;
    }

    public LotPredicateBuilder withBuyer(Account buyer) {
        if (buyer != null) {
            builder.and(bidQuery.owner.eq(buyer));
        }
        return this;
    }

    public LotPredicateBuilder withTitleLike(String titleLike) {
        if (titleLike != null) {
            //TODO may be not work
            builder.and(lotQuery.title.like(titleLike));
        }
        return this;
    }

    public LotPredicateBuilder withBuyerName(String buyerName) {
        if (buyerName != null) {
            builder.and(bidQuery.owner.username.eq(buyerName));
        }
        return this;
    }

    public LotPredicateBuilder withOwnerName(String ownerName) {
        if (ownerName != null) {
            builder.and(lotQuery.owner.username.eq(ownerName));
        }
        return this;
    }

    public LotPredicateBuilder withCategoryName(String categoryName) {
        if (categoryName != null) {
            builder.and(lotQuery.category.name.eq(categoryName));
        }
        return this;
    }

    public LotPredicateBuilder withNearestExpirationDate() {
        builder.and(lotQuery.expiresAt.min().before(new Date(System.currentTimeMillis())));
        return this;
    }

    public Predicate build(){
        return builder.getValue();
    }

}
