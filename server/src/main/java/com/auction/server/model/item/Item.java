package com.auction.server.model.item;

import com.auction.shared.entity.Entity;
import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;

public abstract class Item extends Entity {
    protected String name;
    protected String description;
    protected ItemCondition condition;
    protected String imageUrl;
    protected User seller;

    public Item(String id, String name, String description, ItemCondition condition, String imageUrl, User seller) {
        super(id);
        this.name = name;
        this.description = description;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.seller = seller;
    }
    public abstract void printInfo();
    public boolean validate() {

        return name != null && !name.trim().isEmpty() && seller != null;
    }



    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ItemCondition getCondition() { return condition; }
    public void setCondition(ItemCondition condition) { this.condition = condition; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
}
