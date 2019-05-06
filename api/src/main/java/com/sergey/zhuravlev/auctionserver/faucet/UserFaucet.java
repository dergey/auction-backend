package com.sergey.zhuravlev.auctionserver.faucet;

import com.sergey.zhuravlev.auctionserver.converter.AccountConverter;
import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.database.repository.AccountRepository;
import com.sergey.zhuravlev.auctionserver.database.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFaucet {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public UserDto getUserDto(User user) {
        user = userRepository
                .findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getPrincipal().getEmail());
        userDto.setPhone(user.getPrincipal().getPhone());
        Account account = accountRepository.findAccountByUser(user).orElse(null);
        userDto.setAccount(AccountConverter.getAccountResponseDto(account));
        return userDto;
    }

}
