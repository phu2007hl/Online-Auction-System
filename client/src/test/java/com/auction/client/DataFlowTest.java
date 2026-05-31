
package com.auction.client;

import com.auction.client.controller.admin.AdminDashboardController;
import com.auction.client.controller.admin.EditApprovedAuctionController;
import com.auction.client.controller.auction.AuctionDetailController;
import com.auction.client.controller.auction.CreateAuctionPageController;
import com.auction.client.controller.auction.MainPageController;
import com.auction.client.controller.auth.LoginController;
import com.auction.client.controller.auth.RegisterController;
import com.auction.client.network.SocketClient;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.database.UserDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.request.auction.BidRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.ProcessAuctionReviewRequest;
import com.auction.shared.request.auth.AdminLoginRequest;
import com.auction.shared.request.auth.LoginRequest;
import com.auction.shared.request.auth.LogOutRequest;
import com.auction.shared.request.auth.RegisterRequest;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Test này cần JavaFX display, hiện chỉ dùng để chạy thủ công khi cần test full client-server")
public class DataFlowTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFlowTest.class);
    private static SocketClient con;
    private static LoginController controller1;
    private static AdminDashboardController controller2;
    private static MainPageController controller3;
    private static CreateAuctionPageController controller4;
    private static RegisterController controller5;
    private static AuctionDetailController controller6;
    private static EditApprovedAuctionController controller7;

    @BeforeAll
    public static void setUp() {
        System.setProperty("dataPath", "../data-test/client-flow");
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already started
        }
        controller1 = new LoginController();
        controller2 = new AdminDashboardController();
        controller3 = new MainPageController();
        controller4 = new CreateAuctionPageController();
        controller5 = new RegisterController();
        controller6 = new AuctionDetailController();
        controller7 = new EditApprovedAuctionController();
      ConcurrentHashMap<Integer,Auction> auctionListTest = new ConcurrentHashMap<>();
      auctionListTest.put(
          12345678,
          new Auction(
              12345678,
              "Yuri",
              "None",
              new User("seller@example.com", "Seller", "Seller"),
              0,
              0,
              null,
              null,
              null));
      AuctionListDatabase.getInstance().saveData(auctionListTest);
      PendingAuctionDatabase.getInstance().saveData(new ConcurrentHashMap<>());
      UserDatabase.getInstance().saveData(new ConcurrentHashMap<>());

        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(4556)) {
                server.setSoTimeout(5000);
                LOGGER.info("Test Server started on port 4555...");
                while (true) {
                    try {
                        Socket connection = server.accept();
                        new Thread(new ClientHandler(connection)).start();
                    } catch (java.net.SocketTimeoutException e) {
                        LOGGER.info("Không còn request kiểm thử, đang dừng server.");
                        break;
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Không thể khởi động server kiểm thử", e);
            }
        }).start();
        con = new SocketClient(4556);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
            // Interruption expected in test
        }
    }

    @Test
    @Order(1)
    public void testRegister() {
        controller5.setSocketClient(con);
        RegisterRequest request =
            new RegisterRequest(
                "MiroVeranda7882@gmail.com",
                "Hunghang7882@",
                "Miro");
        con.sendRequest(request);

        waitForResponse();
        assertTrue(RegisterController.getSwitchToMainSuccess());
        assertEquals(1, ClientHandler.getOnlineUser().size());
    }

    @Test
    @Order(2)
    public void testAdminLogin() {
        controller1.setSocketClient(con);
        AdminLoginRequest request = new AdminLoginRequest("admin");
        con.sendRequest(request);

        waitForResponse();
        assertTrue(LoginController.getSwitchToAdminSuccess());
    }

    @Test
    @Order(3)
    public void testLogOut() {
        controller3.setSocketClient(con);
        con.setController(controller3);
        LogOutRequest request = new LogOutRequest();
        con.sendRequest(request);

        waitForResponse();
        assertTrue(MainPageController.getLogOutSuccess());
        assertEquals(0, ClientHandler.getOnlineUser().size());
    }

    @Test
    @Order(4)
    public void testLogin() {
        controller1.setSocketClient(con);
        con.setController(controller1);
        LoginRequest request =
            new LoginRequest(
                "MiroVeranda7882@gmail.com",
                "Hunghang7882@");
        con.sendRequest(request);

        waitForResponse();
        assertTrue(LoginController.getSwitchToMainSuccess());
        assertEquals(1, ClientHandler.getOnlineUser().size());
    }

    @Test
    @Order(5)
    public void testCreateAuction() {
        controller2.setSocketClient(con);
        con.setController(controller2);
        byte[] sampleImage =
            new byte[] {
                (byte) 0x89,
                0x50,
                0x4E,
                0x47,
                0x0D,
                0x0A,
                0x1A,
                0x0A
            };
        CreateAuctionRequest request =
            new CreateAuctionRequest(
                sampleImage,
                "Electronic",
                1000,
                "desc",
                LocalDateTime.now().plusDays(4),
                888888888,
                "Laptop",
                100);
        con.sendRequest(request);

        waitForResponse();
        assertTrue(AdminDashboardController.getUpdateSuccess());
    }

    @Test
    @Order(6)
    public void testUpdateMainPage() {
        controller3.setSocketClient(con);
        byte[] sampleImage =
            new byte[] {
                (byte) 0x89,
                0x50,
                0x4E,
                0x47
            };
        CreateAuctionRequest request =
            new CreateAuctionRequest(
                sampleImage,
                "Electronic",
                1000,
                "desc",
                LocalDateTime.now().plusDays(4),
                888888888,
                "Laptop",
                100);
        PendingAuctionReviewRequest pendingRequest =
            new PendingAuctionReviewRequest(
                request,
                new User("MaroMoro@gmail.com", "l888888888", "Miro"));
        ProcessAuctionReviewRequest req =
            new ProcessAuctionReviewRequest(pendingRequest, CreateAuctionStatus.SUCCESS);

        con.sendRequest(req);
        waitForResponse();
        assertTrue(MainPageController.getUpdateMainSuccess());
    }

    private void waitForResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            // Interruption expected in test
        }
    }
    @Test
    @Order(7)
    public void conccurencyTest(){
        controller6.setSocketClient(con);
        controller7.setSocketClient(con);
        final CyclicBarrier gate = new CyclicBarrier(3);
        Thread t1 = new Thread(){
            public void run(){
                try{
                gate.await();
                con.sendRequest(new BidRequest(12345678,1500 ,new User("Miro@gmail.com", "aosdjiou", "Miro")));                    
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Thread t2 = new Thread(){
            public void run(){
                try{
                    gate.await();
                    con.sendRequest(new EditAuctionRequest(null, 12345678, getName(), getName(), getName(), CreateAuctionStatus.SUCCESS, AuctionStatus.CANCELLED));

                }
                catch (Exception e){
                    e.printStackTrace();
                }            }
        };
        t1.start();
        t2.start();
        try{
            gate.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }

    @AfterAll
    public static void clearResource() {
        ClientHandler.getOnlineUser().clear();
    }
}
