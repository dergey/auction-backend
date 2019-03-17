package com.sergey.zhuravlev.auctionserver.repository;

import com.sergey.zhuravlev.auctionserver.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ImageRepository  extends JpaRepository<Image, String> {

    List<Image> findAllByNameIn(Collection<String> names);

    Image findByName(String name);

}
