package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private String title;
    private String subTitle;
    private String content;
}
