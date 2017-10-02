package com.sergey.zhuravlev.auctionserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "lots")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "image")
    private String image;
    @Column(name = "expiration_date")
    private Timestamp expirationDate;
    @Column(name = "starting_price")
    private Double startingPrice;
    @Column(name = "auction_step")
    private Double auctionStep;
    @Column(name = "status")
    private Integer status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @JsonIgnore
    transient Bid lastBid;

    public Lot() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Double getAuctionStep() {
        return auctionStep;
    }

    public void setAuctionStep(Double auctionStep) {
        this.auctionStep = auctionStep;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @JsonProperty("lastBid")
    public Bid getLastBid() {
        return lastBid;
    }

    @JsonIgnore
    public void setLastBid(Bid lastBid) {
        this.lastBid = lastBid;
    }

    @Override
    public String toString() {
        return "Lot{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", expirationDate=" + expirationDate +
                ", startingPrice=" + startingPrice +
                ", auctionStep=" + auctionStep +
                ", status=" + status +
                ", owner=" + owner +
                ", category=" + category +
                '}';
    }
}

