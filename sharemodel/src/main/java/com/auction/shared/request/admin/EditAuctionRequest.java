package com.auction.shared.request.admin;

import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.Request;
import java.time.LocalDateTime;

public class EditAuctionRequest extends Request {
    private final byte[] imageContent;
    private final int id;
    private final String category;
    private final String description;
    private String itemName;
    private LocalDateTime endTime;
    private boolean antiSnippingEnabled;
    private CreateAuctionStatus createAuctionStatus;
    private AuctionStatus auctionStatus;
    public EditAuctionRequest(byte[] imageContent, int id, String category, String description, String itemName,
                              CreateAuctionStatus createAuctionStatus,AuctionStatus auctionStatus) {
        this(imageContent, id, category, description, itemName, null, false, createAuctionStatus, auctionStatus);
                              }
    public EditAuctionRequest(byte[] imageContent, int id, String category, String description, String itemName,
                              CreateAuctionStatus createAuctionStatus) {
        this(imageContent, id, category, description, itemName, null, false, createAuctionStatus, null);
                              }
    public EditAuctionRequest(byte[] imageContent, int id, String category, String description, String itemName,
                              LocalDateTime endTime, boolean antiSnippingEnabled,
                              CreateAuctionStatus createAuctionStatus) {
        this(imageContent, id, category, description, itemName, endTime, antiSnippingEnabled,
                createAuctionStatus, null);
                              }
    public EditAuctionRequest(byte[] imageContent, int id, String category, String description, String itemName,
                              LocalDateTime endTime, boolean antiSnippingEnabled,
                              CreateAuctionStatus createAuctionStatus,AuctionStatus auctionStatus) {
        this.imageContent = imageContent;
        this.id = id;
        this.category = category;
        this.description = description;
        this.itemName = itemName;
        this.endTime = endTime;
        this.antiSnippingEnabled = antiSnippingEnabled;
        this.createAuctionStatus = createAuctionStatus;
        this.auctionStatus = auctionStatus;
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public boolean isAntiSnippingEnabled() {
        return antiSnippingEnabled;
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
