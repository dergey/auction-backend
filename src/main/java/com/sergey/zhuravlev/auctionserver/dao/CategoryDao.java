package com.sergey.zhuravlev.auctionserver.dao;

import com.sergey.zhuravlev.auctionserver.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<Category, Long> {
}