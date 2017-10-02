package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.model.Bid;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.notification.NotificationBetBroken;
import com.sergey.zhuravlev.auctionserver.notification.NotificationService;
import com.sergey.zhuravlev.auctionserver.service.BetService;
import com.sergey.zhuravlev.auctionserver.service.LotService;
import com.sergey.zhuravlev.auctionserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
public class BidController {

    private static final Logger logger = LoggerFactory.getLogger(BidController.class);

    @Autowired
    private LotService lotService;

    @Autowired
    private BetService betService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/lots/bid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity make(@RequestBody Bid bid) {
        if (isAuthorizate()) {
            Lot lot = lotService.getByID(bid.getLot().getId());
            if (lot.getStatus() == 0) {
                Bid currentBid = betService.getBetByLotIdAndMaxSize(lot.getId());
                if (currentBid == null || bid.getSize() > currentBid.getSize()) {
                    bid.setBuyer(userService.findByUsername(getAuthenticationUser().getUsername()));
                    bid.setTime(new Timestamp(System.currentTimeMillis()));
                    // Уведомление предыдущему владельцу лоту
                    //logger.debug("Ставка  : " + currentBid);
                    if (currentBid!=null && !currentBid.getBuyer().getNotificationToken().isEmpty()){
                       // logger.debug("SEND MESSAGE : buyer - " + currentBid.getBuyer().getUsername() + " new bid size - " + bid.getSize() + " lot " + lot);
                        notificationService.send(new NotificationBetBroken(currentBid.getBuyer(), bid, lot));
                    }
                    betService.make(bid);
                } else return new ResponseEntity(HttpStatus.CONFLICT);
                return new ResponseEntity(HttpStatus.OK);
            } else return new ResponseEntity(HttpStatus.CONFLICT);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/lots/bid/remove", method = RequestMethod.POST)
    @ResponseBody
    public void delete(@RequestParam("id") Long id) {
        betService.remove(id);
    }

    @RequestMapping(value = "/lot/{id}/bids/", method = RequestMethod.GET)
    @ResponseBody
    public List<Bid> getAllBet(@PathVariable("id") Long lotId) {
        return betService.getAllBetByLotId(lotId);
    }

    @RequestMapping(value = "/lot/{id}/bids/last/", method = RequestMethod.GET)
    @ResponseBody
    public Bid getLastBet(@PathVariable("id") Long lotId) {
        return betService.getBetByLotIdAndMaxSize(lotId);
    }


    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAuthorizate(){
        return  (!(getAuthentication() instanceof AnonymousAuthenticationToken));
    }

    private UserDetails getAuthenticationUser(){
        Authentication auth = getAuthentication();
        if (isAuthorizate()) {
            return (UserDetails) auth.getPrincipal();
        } else
            return null;
    }
}
