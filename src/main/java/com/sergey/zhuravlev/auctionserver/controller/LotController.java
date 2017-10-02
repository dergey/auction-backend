package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.dao.BidDao;
import com.sergey.zhuravlev.auctionserver.dao.CategoryDao;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.model.Category;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.model.User;
import com.sergey.zhuravlev.auctionserver.notification.NotificationService;
import com.sergey.zhuravlev.auctionserver.service.LotService;
import com.sergey.zhuravlev.auctionserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
public class LotController {

    private static final Logger logger = LoggerFactory.getLogger(LotController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LotService lotService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private UserService userService;

    @Autowired
    private BidDao bidDao;

    //Получаем по ID
    @RequestMapping(value = "/lot", method = RequestMethod.GET)
    @ResponseBody
    public Lot getLot(@RequestParam("id") Long id) {
        return lotService.getByID(id);
    }

    @RequestMapping(value = "/lots", method = RequestMethod.GET)
    @ResponseBody
    public List<Lot> findLots(@RequestParam(value = "category", required = false) Long categoryID,
                              @RequestParam(value = "owner", required = false) Long ownerID,
                              @RequestParam(value = "search", required = false) String query,
                              @RequestParam(value = "offset", required = false) Integer offset){
        return lotService.getLots(categoryID, ownerID, query, offset);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/lots", method = RequestMethod.POST)
    @ResponseBody
    public Lot updateOrAddLot(@RequestBody Lot lot) {
        lot.setExpirationDate(new Timestamp((System.currentTimeMillis() + lot.getExpirationDate().getTime())));
        if (lot.getCategory() == null) lot.setCategory(categoryDao.findOne(1L));
        lot.setOwner(userService.findByUsername(getAuthenticationUser().getUsername()));
        if (lot.getStatus() == null) lot.setStatus(Status.Sale);
        logger.debug("Добавляется " + lot.toString());
        lotService.save(lot);
        logger.debug("Lot " + lot);
        return lot;
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/lots/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity deleteLot(@RequestParam("id") Long id) {
        Lot lot = lotService.getByID(id);
        if (lot == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (getAuthenticationUser() == null) return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        User user = userService.findByUsername(getAuthenticationUser().getUsername());
        if (user.getId().longValue() == lot.getOwner().getId().longValue()) {
            lotService.remove(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/profile/bids", method = RequestMethod.GET)
    @ResponseBody
    public List<Lot> findLotsFromBidsBuyer() {
        UserDetails userDetails = getAuthenticationUser();
        return lotService.getLotsByBuyerId(userService.findByUsername(userDetails.getUsername()).getId());
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/profile/sold", method = RequestMethod.GET)
    @ResponseBody
    public List<Lot> findSoldLots() {
        UserDetails userDetails = getAuthenticationUser();
        return lotService.getByOwnerIDAndStatus(userService.findByUsername(userDetails.getUsername()).getId(), Status.Sold);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/profile/purchased", method = RequestMethod.GET)
    @ResponseBody
    public List<Lot> findPurchasedLots() {
        UserDetails userDetails = getAuthenticationUser();
        return lotService.getPurchasedLots(userService.findByUsername(userDetails.getUsername()).getId());
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @ResponseBody
    public List<Category> getAllCategories(){
        return categoryDao.findAll();
    }


    @RequestMapping(value = "/profile/recommend", method = RequestMethod.GET)
    @ResponseBody
    public List<Lot> findRecommendLots() {
        return lotService.getRandom();
    }

    @RequestMapping(value = "/profile/lots", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Lot>> getMyLots(){
        UserDetails userDetails = getAuthenticationUser() ;
        if (userDetails != null)
            return new ResponseEntity<>(lotService.getByOwnerID(userService.findByUsername(userDetails.getUsername()).getId()), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test(){
        return "available";
    }

    private UserDetails getAuthenticationUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return (UserDetails) auth.getPrincipal();
        } else
            return null;
    }



}
