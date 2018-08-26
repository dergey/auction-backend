package com.sergey.zhuravlev.auctionserver.repository;

import com.sergey.zhuravlev.auctionserver.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}