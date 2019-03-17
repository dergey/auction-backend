package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.LotConverter;
import com.sergey.zhuravlev.auctionserver.dto.RequestLotDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseLotDto;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.service.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/lots")
public class LotController {

    private final LotService lotService;

    //TODO added PageDto
    @GetMapping
    public Collection<ResponseLotDto> list(
            @RequestParam(value = "category", required = false) Long categoryID,
            @RequestParam(value = "owner", required = false) Long ownerID,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "page", required = false) int pageNumber,
            @RequestParam(value = "size", required = false) int pageSize) {
        return lotService.list(LotStatus.ACTIVE, title, ownerID, categoryID, pageNumber, pageSize).stream()
                .map(LotConverter::convert).collect(Collectors.toList());
    }

    @GetMapping(value = "{lot_id}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseLotDto getLot(@PathVariable("lot_id") Long lotId) {
        Lot lot = lotService.getLot(lotId);
        return LotConverter.convert(lot);
    }

    @Secured({"ROLE_USER"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseLotDto createLot(@Validated @RequestBody RequestLotDto lotDto,
                                    Principal principal) {
        User user = getAuthenticationUser(principal);
        Lot lot = lotService.createLot(lotDto, user);
        return LotConverter.convert(lot);
    }

    @Secured({"ROLE_USER"})
    @PostMapping("{lot_id}")
    public ResponseLotDto updateLot(@PathVariable("lot_id") Long lotId,
                                    @Validated @RequestBody RequestLotDto lotDto,
                                    Principal principal) {
        User user = getAuthenticationUser(principal);
        Lot lot = lotService.getLot(lotId);
        lot = lotService.updateLot(lot, lotDto, user);
        return LotConverter.convert(lot);
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "{lot_id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseLotDto cancelLot(@PathVariable("lot_id") Long lotId, Principal principal) {
        User user = getAuthenticationUser(principal);
        Lot lot = lotService.getLot(lotId);
        lot = lotService.cancelLot(lot, user);
        return LotConverter.convert(lot);
    }

    private User getAuthenticationUser(Principal rawPrincipal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) rawPrincipal;
        return (User) token.getPrincipal();
    }

}
