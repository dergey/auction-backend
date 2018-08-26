package com.sergey.zhuravlev.auctionserver.repository;

import com.sergey.zhuravlev.auctionserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role getByName(String userRole);

}
