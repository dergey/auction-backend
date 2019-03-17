package com.sergey.zhuravlev.auctionserver.builder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.entity.QBid;
import com.sergey.zhuravlev.auctionserver.entity.QLot;
import com.sergey.zhuravlev.auctionserver.entity.User;
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

    public LotPredicateBuilder withOwner(User owner) {
        if (owner != null) {
            builder.and(lotQuery.owner.eq(owner));
        }
        return this;
    }

    public LotPredicateBuilder withBuyer(User buyer) {
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

    public LotPredicateBuilder withBuyerId(Long buyerId) {
        if (buyerId != null) {
            builder.and(bidQuery.owner.id.eq(buyerId));
        }
        return this;
    }

    public LotPredicateBuilder withOwnerId(Long ownerId) {
        if (ownerId != null) {
            builder.and(lotQuery.owner.id.eq(ownerId));
        }
        return this;
    }

    public LotPredicateBuilder withCategoryId(Long categoryId) {
        if (categoryId != null) {
            builder.and(lotQuery.category.id.eq(categoryId));
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
