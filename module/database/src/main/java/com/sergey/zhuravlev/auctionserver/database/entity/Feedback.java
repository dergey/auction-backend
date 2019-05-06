package com.sergey.zhuravlev.auctionserver.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_user_id", nullable = false)
    private LocalUser review;

    @Column(name = "short_text", length = 80, nullable = false)
    private String shortText;

    @Column(name = "text", length = 500)
    private String text;

    @Column(name = "stars", length = 2, nullable = false)
    private Integer stars;

    //private Collection<Image> image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private LocalUser author;

    @Column(name = "create_at", nullable = false)
    private Date createAt;

    @Column(name = "update_at", nullable = false)
    private Date updateAt;

}
