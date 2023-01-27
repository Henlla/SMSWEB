package com.example.smsweb.mail;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
    private String toMail;
    private String subject;
    private Map<String,Object> props;
}
