package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.LotConverter;
import com.sergey.zhuravlev.auctionserver.core.service.CategoryService;
import com.sergey.zhuravlev.auctionserver.core.service.LotService;
import com.sergey.zhuravlev.auctionserver.database.builder.LotPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.Category;
import com.sergey.zhuravlev.auctionserver.database.entity.Lot;
import com.sergey.zhuravlev.auctionserver.database.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.dto.PageDto;
import com.sergey.zhuravlev.auctionserver.dto.RequestLotDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseLotDto;
import com.sergey.zhuravlev.auctionserver.faucet.SecurityFaucet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Currency;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/lots")
public class LotController {

    private final LotService lotService;
    private final SecurityFaucet securityFaucet;
    private final CategoryService categoryService;

    @GetMapping
    public PageDto<ResponseLotDto> list(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "owner", required = false) String owner,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        LotPredicateBuilder lotPredicateBuilder = new LotPredicateBuilder()
                .withStatus(LotStatus.ACTIVE)
                .withTitleLike(title)
                .withOwnerName(owner)
                .withCategoryName(category);
        return new PageDto<>(lotService.list(lotPredicateBuilder, PageRequest.of(
                page == null ? 0 : page,
                size == null ? PageDto.DEFAULT_PAGE_SIZE : size,
                Sort.Direction.ASC, "updateAt")
        ).map(LotConverter::convert));
    }

    @GetMapping(value = "{lot_id}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseLotDto getLot(@PathVariable("lot_id") Long lotId) {
        Lot lot = lotService.getLot(lotId);
        return LotConverter.convert(lot);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseLotDto createLot(@Valid @RequestBody RequestLotDto lotDto) {
        Account account = securityFaucet.getCurrentAccount();
        Category category = categoryService.getCategory(lotDto.getCategoryId());
        Lot lot = lotService.createLot(account,
                category,
                lotDto.getTitle(),
                lotDto.getDescription(),
                lotDto.getImages(),
                lotDto.getExpiresAt(),
                lotDto.getStartingAmount(),
                lotDto.getAuctionStep(),
                Currency.getInstance(lotDto.getCurrencyCode()));
        return LotConverter.convert(lot);
    }

    @PostMapping("{lot_id}")
    public ResponseLotDto updateLot(@PathVariable("lot_id") Long lotId,
                                    @Valid @RequestBody RequestLotDto lotDto) {
        Account account = securityFaucet.getCurrentAccount();
        Category category = categoryService.getCategory(lotDto.getCategoryId());
        Lot lot = lotService.getLot(lotId);
        lot = lotService.updateLot(lot,
                account,
                category,
                lotDto.getTitle(),
                lotDto.getDescription(),
                lotDto.getImages(),
                lotDto.getExpiresAt(),
                lotDto.getStartingAmount(),
                lotDto.getAuctionStep(),
                Currency.getInstance(lotDto.getCurrencyCode()));
        return LotConverter.convert(lot);
    }

    @PostMapping(value = "{lot_id}/cancel")
    public ResponseLotDto cancelLot(@PathVariable("lot_id") Long lotId) {
        Account account = securityFaucet.getCurrentAccount();
        Lot lot = lotService.getLot(lotId);
        lot = lotService.cancelLot(lot, account);
        return LotConverter.convert(lot);
    }

}
