package com.auction.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.database.AuctionBidderDetailDatabase;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.database.UserDatabase;
import com.auction.server.handler.auction.CreateAuctionRequestHandler;
import com.auction.server.handler.auction.ProcessAuctionReviewHandler;
import com.auction.server.model.auction.AuctionBidderDetail;
import com.auction.server.model.auction.BidProcessResult;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.BidService;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.enums.BidderStatus;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.auction.BidRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.ProcessAuctionReviewRequest;
import com.auction.shared.response.auction.CreateAuctionResponse;
import com.auction.shared.response.auction.ProcessAuctionReviewResponse;
import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerLogicTest {
  private static final int AUCTION_ID = 1001;
  private static final String TEST_DATA_PATH = "../data-test/server-logic";

  @BeforeAll
  static void setUpDataPath() {
    System.setProperty("dataPath", TEST_DATA_PATH);
  }

  @BeforeEach
  void resetDatabase() {
    AuctionListDatabase.getInstance().saveData(new ConcurrentHashMap<Integer, Auction>());
    AuctionBidderDetailDatabase.getInstance()
        .saveData(new ConcurrentHashMap<Integer, AuctionBidderDetail>());
    PendingAuctionDatabase.getInstance()
        .saveData(new ConcurrentHashMap<Integer, PendingAuctionReviewRequest>());
    AdminResponseDatabase.getInstance()
        .saveData(new LinkedHashMap<Integer, PendingAuctionReviewRequest>());
    UserDatabase.getInstance().saveData(new ConcurrentHashMap<String, User>());
  }

  @Test
  void databaseShouldSaveToDataTestFolder() {
    ConcurrentHashMap<String, User> userList = UserDatabase.getInstance().getData();
    User user = new User("test@example.com", "123456", "tester");
    userList.put(user.getEmail(), user);
    UserDatabase.getInstance().saveData(userList);

    File userDataFile = new File(TEST_DATA_PATH + "/User.ser");
    assertTrue(userDataFile.exists());
    assertEquals("tester", UserDatabase.getInstance().getData().get(user.getEmail()).getUsername());
  }

  @Test
  void validBidShouldUpdateAuctionAndBidderStatus() {
    User seller = new User("seller@example.com", "123456", "seller");
    User bidder = new User("bidder@example.com", "123456", "bidder");
    Auction auction = createOpenAuction(seller);

    ConcurrentHashMap<Integer, Auction> auctionList = AuctionListDatabase.getInstance().getData();
    auctionList.put(AUCTION_ID, auction);
    AuctionListDatabase.getInstance().saveData(auctionList);

    BidRequest request = new BidRequest(AUCTION_ID, 150, bidder);
    BidProcessResult result = new BidService(request).executeLogic(AUCTION_ID);

    Auction updatedAuction = AuctionListDatabase.getInstance().getData().get(AUCTION_ID);
    AuctionBidderDetail bidderDetail =
        AuctionBidderDetailDatabase.getInstance().getData().get(AUCTION_ID);

    assertEquals(BidResponseStatus.ACCEPTED, result.getBidResponseStatus());
    assertEquals(150, updatedAuction.getCurrentPrice());
    assertEquals(bidder.getEmail(), updatedAuction.getWinner().getEmail());
    assertEquals(1, updatedAuction.getBidHistory().size());
    assertNotNull(bidderDetail);
    assertEquals(bidder.getEmail(), bidderDetail.getCurrentWinnerEmail());
    assertEquals(
        BidderStatus.CURRENT_WINNER,
        bidderDetail.getBidderStatusHashMap().get(bidder.getEmail()));
  }

  @Test
  void cancelledAuctionShouldRejectBid() {
    User seller = new User("seller@example.com", "123456", "seller");
    User bidder = new User("bidder@example.com", "123456", "bidder");
    Auction auction = createOpenAuction(seller);
    auction.setStatus(AuctionStatus.CANCELLED);

    ConcurrentHashMap<Integer, Auction> auctionList = AuctionListDatabase.getInstance().getData();
    auctionList.put(AUCTION_ID, auction);
    AuctionListDatabase.getInstance().saveData(auctionList);

    BidRequest request = new BidRequest(AUCTION_ID, 150, bidder);
    BidProcessResult result = new BidService(request).executeLogic(AUCTION_ID);

    Auction updatedAuction = AuctionListDatabase.getInstance().getData().get(AUCTION_ID);
    assertEquals(BidResponseStatus.DECLINED, result.getBidResponseStatus());
    assertEquals(100, updatedAuction.getCurrentPrice());
    assertEquals(0, updatedAuction.getBidHistory().size());
    assertEquals(AuctionStatus.CANCELLED, updatedAuction.getStatus());
  }

  @Test
  void concurrentBidShouldKeepHighestBid() throws InterruptedException {
    User seller = new User("seller@example.com", "123456", "seller");
    User lowBidder = new User("low@example.com", "123456", "low");
    User highBidder = new User("high@example.com", "123456", "high");
    Auction auction = createOpenAuction(seller);

    ConcurrentHashMap<Integer, Auction> auctionList = AuctionListDatabase.getInstance().getData();
    auctionList.put(AUCTION_ID, auction);
    AuctionListDatabase.getInstance().saveData(auctionList);

    Thread lowBidThread = new Thread() {
      public void run() {
        BidRequest request = new BidRequest(AUCTION_ID, 150, lowBidder);
        new BidService(request).executeLogic(AUCTION_ID);
      }
    };
    Thread highBidThread = new Thread() {
      public void run() {
        BidRequest request = new BidRequest(AUCTION_ID, 200, highBidder);
        new BidService(request).executeLogic(AUCTION_ID);
      }
    };

    lowBidThread.start();
    highBidThread.start();
    lowBidThread.join();
    highBidThread.join();

    Auction updatedAuction = AuctionListDatabase.getInstance().getData().get(AUCTION_ID);
    assertEquals(200, updatedAuction.getCurrentPrice());
    assertEquals(highBidder.getEmail(), updatedAuction.getWinner().getEmail());
  }

  @Test
  void createAuction() {
    User seller = new User("seller@example.com", "123456", "seller");
    ClientHandler clientHandler = new ClientHandler(null);
    clientHandler.setUser(seller);
    CreateAuctionRequest request = createAuctionRequest();

    CreateAuctionResponse response =
        (CreateAuctionResponse) new CreateAuctionRequestHandler().handle(request, clientHandler);

    ConcurrentHashMap<Integer, PendingAuctionReviewRequest> pendingList =
        PendingAuctionDatabase.getInstance().getData();
    PendingAuctionReviewRequest pendingRequest = pendingList.get(AUCTION_ID);

    assertTrue(response.getResponse());
    assertNotNull(pendingRequest);
    assertEquals(seller.getEmail(), pendingRequest.getUser().getEmail());
    assertEquals(CreateAuctionStatus.PENDING, pendingRequest.getStatus());
  }

  @Test
  void acceptAuction() {
    User seller = new User("seller@example.com", "123456", "seller");
    PendingAuctionReviewRequest pendingRequest =
        new PendingAuctionReviewRequest(createAuctionRequest(), seller);
    PendingAuctionDatabase.getInstance().getData().put(AUCTION_ID, pendingRequest);
    PendingAuctionDatabase.getInstance().saveData(PendingAuctionDatabase.getInstance().getData());

    ProcessAuctionReviewRequest request =
        new ProcessAuctionReviewRequest(pendingRequest, CreateAuctionStatus.SUCCESS);
    ProcessAuctionReviewResponse response =
        (ProcessAuctionReviewResponse) new ProcessAuctionReviewHandler().handle(request, null);

    assertTrue(response.getResponse());
    assertTrue(AdminResponseDatabase.getInstance().getData().containsKey(AUCTION_ID));
    assertTrue(AuctionListDatabase.getInstance().getData().containsKey(AUCTION_ID));
    assertFalse(PendingAuctionDatabase.getInstance().getData().containsKey(AUCTION_ID));
    assertEquals(
        CreateAuctionStatus.SUCCESS,
        AdminResponseDatabase.getInstance().getData().get(AUCTION_ID).getStatus());
  }

  @Test
  void declineAuction() {
    User seller = new User("seller@example.com", "123456", "seller");
    PendingAuctionReviewRequest pendingRequest =
        new PendingAuctionReviewRequest(createAuctionRequest(), seller);
    PendingAuctionDatabase.getInstance().getData().put(AUCTION_ID, pendingRequest);
    PendingAuctionDatabase.getInstance().saveData(PendingAuctionDatabase.getInstance().getData());

    ProcessAuctionReviewRequest request =
        new ProcessAuctionReviewRequest(pendingRequest, CreateAuctionStatus.DECLINED);
    ProcessAuctionReviewResponse response =
        (ProcessAuctionReviewResponse) new ProcessAuctionReviewHandler().handle(request, null);

    assertTrue(response.getResponse());
    assertTrue(AdminResponseDatabase.getInstance().getData().containsKey(AUCTION_ID));
    assertFalse(AuctionListDatabase.getInstance().getData().containsKey(AUCTION_ID));
    assertFalse(PendingAuctionDatabase.getInstance().getData().containsKey(AUCTION_ID));
    assertEquals(
        CreateAuctionStatus.DECLINED,
        AdminResponseDatabase.getInstance().getData().get(AUCTION_ID).getStatus());
  }

  @Test
  void antiSnipping() {
    User seller = new User("seller@example.com", "123456", "seller");
    User bidder = new User("bidder@example.com", "123456", "bidder");
    Auction auction = createOpenAuction(seller);
    LocalDateTime oldEndTime = LocalDateTime.now().plusSeconds(5);
    auction.setEndTime(oldEndTime);
    auction.setAntiSnippingEnabled(true);

    ConcurrentHashMap<Integer, Auction> auctionList = AuctionListDatabase.getInstance().getData();
    auctionList.put(AUCTION_ID, auction);
    AuctionListDatabase.getInstance().saveData(auctionList);

    BidRequest request = new BidRequest(AUCTION_ID, 150, bidder);
    BidProcessResult result = new BidService(request).executeLogic(AUCTION_ID);

    Auction updatedAuction = AuctionListDatabase.getInstance().getData().get(AUCTION_ID);
    assertEquals(BidResponseStatus.ACCEPTED, result.getBidResponseStatus());
    assertTrue(updatedAuction.getEndTime().isAfter(oldEndTime));
  }

  private Auction createOpenAuction(User seller) {
    return new Auction(
        AUCTION_ID,
        "Laptop",
        "Test auction",
        seller,
        100,
        10,
        LocalDateTime.now().plusDays(1),
        null,
        "Electronic");
  }

  private CreateAuctionRequest createAuctionRequest() {
    return new CreateAuctionRequest(
        null,
        "Electronic",
        100,
        "Test auction",
        LocalDateTime.now().plusDays(1),
        AUCTION_ID,
        "Laptop",
        10,
        true);
  }
}
