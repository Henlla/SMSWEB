package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Devices {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "device_token", nullable = true, length = 200)
    private String deviceToken;
    @Basic
    @Column(name = "account_id", nullable = true)
    private Integer accountId;
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Account accountDevice;

}
