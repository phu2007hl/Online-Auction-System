package com.auction.shared.request.admin;

import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.Request;

public class EditAuctionRequest extends Request {
    private final byte[] imageContent;
    private final int id;
    private final String category;
    private final String description;
    private String itemName;
    private CreateAuctionStatus createAuctionStatus;
    private AuctionStatus auctionStatus;
    public EditAuctionRequest(byte[] imageContent, int id, String category, String description, String itemName,
                              CreateAuctionStatus createAuctionStatus,AuctionStatus auctionStatus) {
        this.imageContent = imageContent;
        this.id = id;
        this.category = category;
        this.description = description;
        this.itemName = itemName;
        this.createAuctionStatus = createAuctionStatus;
        this.auctionStatus = auctionStatus;
                              }
    public EditAuctionRequest(byte[] imageContent, int id, String category, String description, String itemName,
                              CreateAuctionStatus createAuctionStatus) {
        this.imageContent = imageContent;
        this.id = id;
        this.category = category;
        this.description = description;
        this.itemName = itemName;
        this.createAuctionStatus = createAuctionStatus;
                              }
    public byte[] getImageContent() {
        return imageContent;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getItemName() {
        return itemName;
    }

    public CreateAuctionStatus getCreateAuctionStatus() {
        return createAuctionStatus;
    }
    public AuctionStatus getAuctionStatus(){
        return auctionStatus;
    }

    public int getId() {
        return id;
    }
}
