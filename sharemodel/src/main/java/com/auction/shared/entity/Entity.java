package com.auction.shared.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Entity implements Serializable {
    protected String id;
    protected LocalDateTime createdTime;

    public Entity(String id) {
        this.id = id;
        this.createdTime = LocalDateTime.now();
    }
}