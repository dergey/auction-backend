package com.sergey.zhuravlev.auctionserver.dao;

import com.sergey.zhuravlev.auctionserver.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<Role, Long> {
}
