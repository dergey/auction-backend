package com.sergey.zhuravlev.auctionserver.aspect;

import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.service.WatcherExpirationLotsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Log
@Aspect
@Component
@RequiredArgsConstructor
public class LotAspect {

    private final WatcherExpirationLotsService watcherExpirationLotsService;

    @Pointcut("execution(* com.sergey.zhuravlev.auctionserver.service.LotService.createLot(..))")
    public void createLot(){}

    @Pointcut("execution(* com.sergey.zhuravlev.auctionserver.service.LotService.updateLot(..))")
    public void updateLot(){}

    @Pointcut("execution(* com.sergey.zhuravlev.auctionserver.service.LotService.cancelLot(..))")
    public void cancelLot(){}

    @AfterReturning(pointcut = "createLot() || updateLot()", returning = "result")
    private void afterLotsChanged(Object result) {
        Lot lot = (Lot) result;
        log.info(String.format("Trying to start following the lot %s.!", lot));
        watcherExpirationLotsService.follow(lot);
    }

    @AfterReturning(pointcut = "cancelLot()", returning = "result")
    void afterLotDelete (Object result) {
        Long id = (Long) result;
        log.info(String.format("Unfollow the lot with %s id!", id));
        watcherExpirationLotsService.unfollowById(id);
    }

}
