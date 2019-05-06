package com.sergey.zhuravlev.auctionserver.database.builder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.database.entity.Lot;
import com.sergey.zhuravlev.auctionserver.database.entity.QBid;

public class BidPredicateBuilder {

    private static final QBid bidQuery = QBid.bid;
    private final BooleanBuilder builder;

    public BidPredicateBuilder() {
        builder = new BooleanBuilder();
    }

    public BidPredicateBuilder withLot(Lot lot) {
        if (lot != null) {
            builder.and(bidQuery.lot.eq(lot));
        }
        return this;
    }

    public Predicate build(){
        return builder.getValue();
    }

}
