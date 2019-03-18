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
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 16, nullable = false, unique = true)
    private String name;

    @Column(name = "filename", length = 50)
    private String filename;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "context_type", length = 50, nullable = false)
    private String contentType;

    @Lob
    @Column(name = "context", columnDefinition="BLOB", nullable = false)
    private byte[] content;

}
