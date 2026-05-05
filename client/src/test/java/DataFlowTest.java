

import com.auction.client.controller.*;
import com.auction.client.network.SocketClient;
import com.auction.server.database.*;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.*;
import javafx.application.Platform;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataFlowTest {
    private static SocketClient con;
    private static LoginController controller1;
    private static AdminDashboardController controller2;
    private static MainPageController controller3;
    private static CreateAuctionPageController controller4;
    private static RegisterController controller5;

    @BeforeAll
    public static void setUp() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
        controller1 = new LoginController();
        controller2 = new AdminDashboardController();
        controller3 = new MainPageController();
        controller4 = new CreateAuctionPageController();
        controller5 = new RegisterController();
        UserDatabase.setPath("UserTest.ser");
        createAuctionDatabase.setPath("AuctionRequestTest.ser");
        AuctionListDatabase.setPath("AuctionListTest.ser");
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(4556)) {
                server.setSoTimeout(5000);
                System.out.println("Test Server started on port 4555...");
                while (true) {
                    try {
                        Socket connection = server.accept();
                        new Thread(new ClientHandler(connection)).start();
                    } catch (java.net.SocketTimeoutException e) {
                        System.out.println("No more test requests. Shutting down server.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Could not start test server: " + e.getMessage());
            }
        }).start();
        con = new SocketClient( 4556);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    @Test
    @Order(1)
    public void testRegister() {
        controller5.setSocketClient(con);
        RegisterRequest request = new RegisterRequest("MiroVeranda7882@gmail.com", "Hunghang7882@", "Miro");
        con.sendRequest(request);
        
        waitForResponse();
        assertTrue(RegisterController.getSwitchToMainSuccess());
        assertEquals(1, ClientHandler.getOnlineUser().size());
    }

    @Test
    @Order(2)
    public void testAdminLogin() {
        controller1.setSocketClient(con);
        AdminLoginRequest request = new AdminLoginRequest("9999999999");
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
        LoginRequest request = new LoginRequest("MiroVeranda7882@gmail.com", "Hunghang7882@");
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
        byte[] sampleImage = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
        createAuctionRequest request = new createAuctionRequest(sampleImage, "Electronic", "1000", "desc", LocalDate.now().plusDays(4));
        con.sendRequest(request);

        waitForResponse();
        assertTrue(AdminDashboardController.getUpdateSuccess());
    }

    @Test
    @Order(6)
    public void testUpdateMainPage() {
        controller3.setSocketClient(con);
        byte[] sampleImage = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47 };
        createAuctionRequest request1 = new createAuctionRequest(sampleImage, "Electronic", "1000", "desc", LocalDate.now().plusDays(4));
        UpdateMainPageRequest req = new UpdateMainPageRequest(request1);
        
        con.sendRequest(req);
        waitForResponse();
        assertTrue(MainPageController.getUpdateMainSuccess());
    }
    private void waitForResponse() {
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }
    @AfterAll
    public static void clearResource(){
        try{
            FileOutputStream out1 = new FileOutputStream("UserTest.ser");
            FileOutputStream out2 = new FileOutputStream("AuctionListTest.ser");
            ClientHandler.getOnlineUser().clear();
            

            FileOutputStream out3 = new FileOutputStream("AuctionRequestTest.ser");


            
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}