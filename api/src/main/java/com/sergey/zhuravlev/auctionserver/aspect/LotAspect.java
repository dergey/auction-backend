package com.sergey.zhuravlev.auctionserver.aspect;

import com.sergey.zhuravlev.auctionserver.database.entity.Lot;
import com.sergey.zhuravlev.auctionserver.service.WatcherExpirationLotsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class LotAspect {

    private final WatcherExpirationLotsService watcherExpirationLotsService;

    @Pointcut("execution(* com.sergey.zhuravlev.auctionserver.core.service.LotService.createLot(..))")
    private void createLotPointcut(){}

    @Pointcut("execution(* com.sergey.zhuravlev.auctionserver.core.service.LotService.updateLot(..))")
    private void updateLotPointcut(){}

    @Pointcut("execution(* com.sergey.zhuravlev.auctionserver.core.service.LotService.cancelLot(..))")
    private void cancelLotPointcut(){}

    @AfterReturning(pointcut = "createLotPointcut() || updateLotPointcut()", returning = "result")
    private void afterLotsChanged(Object result) {
        Lot lot = (Lot) result;
        log.info("Trying to start following the lot {}.!", lot);
        watcherExpirationLotsService.follow(lot);
    }

    @AfterReturning(pointcut = "cancelLotPointcut()", returning = "result")
    void afterLotDelete (Object result) {
        Long id = (Long) result;
        log.info("Unfollow the lot with {} id!", id);
        watcherExpirationLotsService.unfollowById(id);
    }

}
