package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.LotConverter;
import com.sergey.zhuravlev.auctionserver.dto.RequestLotDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseLotDto;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.entity.Category;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.repository.CategoryRepository;
import com.sergey.zhuravlev.auctionserver.service.BidService;
import com.sergey.zhuravlev.auctionserver.service.LotService;
import com.sergey.zhuravlev.auctionserver.service.SecurityService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Log
@RestController
public class LotController {

    private final LotService lotService;
    private final CategoryRepository categoryRepository;
    private final BidService bidService;

    @Autowired
    public LotController(LotService lotService, BidService bidService,
                         CategoryRepository categoryRepository) {
        this.lotService = lotService;
        this.bidService = bidService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping(value = "/lots/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseLotDto getLot(@PathVariable("id") Long id) {
        Lot lot = lotService.get(id);
        Bid lastBid = bidService.getLastBid(id);
        ResponseLotDto lotDto = LotConverter.toResponse(lot);
        lotDto.setLastBid(lastBid);
        return lotDto;
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/lots")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseLotDto createLot(@Validated @RequestBody RequestLotDto lotDto) {
        User user = SecurityService.getAuthenticationUser();
        Lot lot = lotService.create(lotDto.getTitle(), lotDto.getDescription(), lotDto.getImage(), lotDto.getExpirationDate(),
                lotDto.getStartingPrice(), lotDto.getAuctionStep(), user, lotDto.getCategory());
        return LotConverter.toResponse(lot);
    }

    @Secured({"ROLE_USER"})
    @PutMapping(value = "/lots/{id}")
    public ResponseLotDto updateLot(@PathVariable("id") Long id, @Validated @RequestBody ResponseLotDto lotDto) {
        User user = SecurityService.getAuthenticationUser();
        Lot lot = lotService.update(id, lotDto.getTitle(), lotDto.getDescription(),
                lotDto.getImage(), lotDto.getExpirationDate(), lotDto.getStartingPrice(),
                lotDto.getAuctionStep(), user, lotDto.getCategory());
        return LotConverter.toResponse(lot);
    }

    @Secured({"ROLE_USER"})
    @DeleteMapping(value = "/lots/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLot(@PathVariable("id") Long id) {
        lotService.delete(id);
    }

    @GetMapping(value = "/lots/search")
    public List<ResponseLotDto> getLots(
            @RequestParam(value = "category", required = false) Long categoryID,
            @RequestParam(value = "owner", required = false) Long ownerID,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page-size", required = false) Integer pageSize) {
        return LotConverter.toResponseCollection(lotService.getLots(Status.SALE, title, ownerID, categoryID, page, pageSize));
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/profile/bids")
    public List<ResponseLotDto> getLotsWhichPlaceBid() {
        User user = SecurityService.getAuthenticationUser();
        return LotConverter.toResponseCollection(lotService.getLotsByBuyer(user));
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/profile/lots")
    public Iterable<Lot> getMyLots(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page-size", required = false) Integer pageSize) {
        User user = SecurityService.getAuthenticationUser();
        return lotService.getLots(Status.values()[status], null, user.getId(), null, page, pageSize);
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/profile/purchased")
    public List<ResponseLotDto> getPurchasedLots() {
        User user = SecurityService.getAuthenticationUser();
        return LotConverter.toResponseCollection(lotService.getUserPurchasedLots(user));
    }

    @GetMapping(value = "/profile/recommend")
    public List<Lot> getRecommendLots() {
        return lotService.getRecommendLots();
    }

    @GetMapping(value = "/categories")
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

}
