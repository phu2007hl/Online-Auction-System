package com.auction.shared.enums;
/**
 * Đây là các trạng thái của người tham gia
 */
public enum BidderStatus {
    //Bao gồm người bị outbid và người đang thắng, seller, viewer chưa bid lần nào
    OUTBID, CURRENT_WINNER, VIEWER, VIEWER_ONLY
}
