package com.example.demo.features.models;

import com.example.demo.auth.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imageUrl;
    private String fileName;
    private String itemName;
    private Date expiryDate;
    private String remarks;
    private String googleCalEventId;

    @JsonIgnore
    @ManyToOne
    private User user;
}
