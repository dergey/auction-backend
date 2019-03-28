package com.sergey.zhuravlev.auctionserver.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images", uniqueConstraints = {
        @UniqueConstraint(name = "uk_image_name", columnNames = "name")
})
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 16, nullable = false, unique = true)
    private String name;

    @Column(name = "filename", length = 50)
    private String filename;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_account_id")
    private Account owner;

    @Column(name = "context_type", length = 50, nullable = false)
    private String contentType;

    @Lob
    @Column(name = "context", nullable = false)
    private byte[] content;

}
