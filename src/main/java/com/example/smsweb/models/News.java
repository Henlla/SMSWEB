package com.example.smsweb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class News {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "title")
    private String title;
    @Basic
    @Column(name = "sub_title")
    private String sub_title;
    @Basic
    @Column(name = "content")
    private String content;
    @Basic
    @Column(name = "post_date")
    private String post_date;
    @Basic
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    @Basic
    @Column(name = "thumbnail_path")
    private String thumbnailPath;
    @Basic
    @Column(name = "isActive")
    private Boolean isActive;
}
