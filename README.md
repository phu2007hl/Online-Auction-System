# Online Auction System

## Mô tả ngắn gọn bài toán và phạm vi hệ thống

Đây là hệ thống đấu giá trực tuyến được xây dựng theo mô hình **Client-Server**, cho phép người dùng đăng ký tài khoản, đăng nhập, tạo phiên đấu giá, tham gia đặt giá và theo dõi kết quả đấu giá theo thời gian thực. Hệ thống hỗ trợ hai vai trò chính:

- **User (Người dùng)**: Đăng ký/đăng nhập, tạo phiên đấu giá, tham gia đặt giá, xem chi tiết auction, nhắn tin trong phòng đấu giá.
- **Admin (Quản trị viên)**: Duyệt phiên đấu giá chờ xử lý, chỉnh sửa thông tin auction, xem lịch sử auction đã duyệt, phát hành phiên đấu giá đã được duyệt.

---

## Công nghệ sử dụng, môi trường chạy và yêu cầu cài đặt

| Thành phần | Công nghệ / Phiên bản |
|---|---|
| Ngôn ngữ lập trình | Java 25 |
| Giao diện người dùng | JavaFX 21.0.2 |
| Build tool | Apache Maven |
| Giao tiếp mạng | Java Socket (TCP, cổng 4100) |
| Serialization | Java Object Serialization |
| Logging | SLF4J 2.0.16 + Logback 1.5.16 |
| IDE khuyên dùng | IntelliJ IDEA |

### Yêu cầu cài đặt

- **JDK 25** trở lên (đã cài đặt và cấu hình `JAVA_HOME`)
- **Apache Maven 3.8+**
- **JavaFX SDK 21** (nếu build thủ công, không dùng Maven plugin)

---

## Cấu trúc thư mục

```
Online-Auction-System/
├── pom.xml                    # Parent POM (multi-module Maven)
├── server/                    # Module Server
│   ├── pom.xml
│   └── src/main/java/com/auction/server/
│       ├── network/           # AuctionServer.java, ClientHandler.java, AdminHandler.java
│       ├── handler/           # RequestDispatcher + các RequestHandler
│       │   ├── auth/          # Login, Register, AdminLogin, LogOut
│       │   ├── auction/       # Bid, CreateAuction, JoinRoom, LeaveRoom, ...
│       │   └── admin/         # EditAuction, GetPendingList, GetCheckedList
│       ├── service/           # Business logic
│       │   ├── auth/
│       │   ├── auction/       # BidService, AuctionRoomManager, AuctionClosingScheduler, ...
│       │   └── admin/
│       └── database/          # Lưu trữ dữ liệu (file .ser)
│           ├── UserDatabase.java
│           ├── AuctionListDatabase.java
│           ├── PendingAuctionDatabase.java
│           └── ...
├── client/                    # Module Client (JavaFX)
│   ├── pom.xml
│   └── src/main/java/com/auction/client/
│       ├── MainApp.java       # Điểm khởi động client
│       ├── network/           # SocketClient.java
│       ├── controller/        # JavaFX Controllers
│       │   ├── auth/          # LoginController, RegisterController
│       │   ├── auction/       # MainPageController, AuctionDetailController, CreateAuctionPageController
│       │   └── admin/         # AdminDashboardController, EditAuctionController, ...
│       └── service/           # LoginAuthenticationService, RegisterAuthenticationService
│   └── src/main/resources/
│       └── fxml/              # Các file giao diện FXML
└── sharemodel/                # Module dùng chung giữa client và server
    └── src/main/java/com/auction/shared/
        ├── model/             # User.java
        ├── auction/           # Auction.java, BidTransaction.java
        ├── entity/            # Entity.java
        ├── enums/             # AuctionStatus, ItemCondition, LoginResponseStatus, ...
        ├── request/           # Tất cả các Request class
        └── response/          # Tất cả các Response class
```

---

## Vị trí các file `.jar`

> **Lưu ý:** Các file JAR cần được build trước khi chạy (xem hướng dẫn bên dưới). Sau khi build thành công, các file sẽ nằm tại:

| Module | Đường dẫn JAR |
|---|---|
| Server (fat JAR) | `server/target/server.jar` |
| Client (fat JAR) | `client/target/client.jar` |

---

## Hướng dẫn build
Build toàn bộ project từ thư mục gốc:
```bash
# Tại thư mục gốc của project
mvn clean package -DskipTests
```

---

## Hướng dẫn chạy Server và Client theo thứ tự cụ thể

### Bước 1: Chạy Server

```bash
java -jar server/target/server.jar
```

Server sẽ khởi động và lắng nghe kết nối tại **cổng 4100**. Khi thấy log `"Server đã khởi động ở cổng 4100"` thì server đã sẵn sàng.

### Bước 2: Chạy Client

```bash
java -jar client/target/client.jar
```

> **Lưu ý:** Server phải được khởi động **trước** khi mở Client. Client kết nối tới `localhost:4100`.

### Chạy thêm client (nhiều người dùng)

Mở thêm terminal và chạy lại lệnh client ở bước 2. Mỗi lần chạy là một phiên client độc lập.


## Danh sách chức năng đã hoàn thành

### Xác thực người dùng (Authentication)
- [x] Đăng ký tài khoản mới
- [x] Đăng nhập tài khoản người dùng
- [x] Đăng nhập với quyền Admin
- [x] Đăng xuất

### Chức năng người dùng (User)
- [x] Xem danh sách phiên đấu giá đang mở (`GetApprovedAuctionList`)
- [x] Xem chi tiết phiên đấu giá (`GetAuctionDetail`)
- [x] Tạo phiên đấu giá mới (`CreateAuction`) — chờ Admin duyệt
- [x] Tham gia phòng đấu giá (`JoinRoom`)
- [x] Rời phòng đấu giá (`LeaveRoom`)
- [x] Đặt giá trong phiên đấu giá (`PlaceBid`) — xử lý đồng thời với `LockManager`
- [x] Nhắn tin trong phòng đấu giá (`SendMessage`)
- [x] Nhận kết quả đấu giá tự động khi hết giờ (`AuctionAutoClose`)
- [x] Cập nhật thông tin người dùng và danh sách auction theo thời gian thực

### Chức năng Admin
- [x] Xem danh sách phiên đấu giá chờ duyệt (`GetPendingAuctionList`)
- [x] Xem lịch sử phiên đấu giá đã duyệt (`GetCheckedAuctionList`)
- [x] Duyệt / từ chối phiên đấu giá (`SaveAuctionReviewResult`)
- [x] Chỉnh sửa thông tin phiên đấu giá đã duyệt (`EditAuction`)
- [x] Phát hành phiên đấu giá đã duyệt (`PublishApprovedAuction`)

### Kiến trúc & Kỹ thuật
- [x] Kiến trúc Client-Server với Java Socket (TCP)
- [x] Module `sharemodel` dùng chung giữa client và server
- [x] `RequestDispatcher` điều phối request đến đúng handler
- [x] Xử lý đa luồng (mỗi client một Thread)
- [x] Quản lý phòng đấu giá (`AuctionRoomManager`)
- [x] Tự động đóng phiên đấu giá theo thời gian (`AuctionClosingScheduler`)
- [x] Xử lý đồng thời bid với `LockManager`
- [x] Lưu trữ dữ liệu bằng file `.ser` (Java Serialization)
- [x] Logging với SLF4J + Logback

---

### Bước 3: Sử dụng hệ thống

**Người dùng thông thường:**
1. Đăng ký tài khoản hoặc đăng nhập.
2. Xem danh sách phiên đấu giá đang mở.
3. Tạo phiên đấu giá mới (chờ Admin duyệt).
4. Vào phòng đấu giá, đặt giá thầu và nhận thông báo realtime.

**Admin:**
1. Đăng nhập bằng tài khoản Admin.
2. Vào Admin Dashboard để duyệt/chỉnh sửa phiên đấu giá.
3. Publish phiên đấu giá đã duyệt lên hệ thống.

---

## Phân chia công việc giữa các thành viên trong nhóm

| Thành viên | Phần việc chính | Mô tả chi tiết |
|---|---|---|
| Phú | JavaFX – Giao diện UI | Thiết kế và phát triển toàn bộ giao diện người dùng bằng JavaFX: màn hình Login, Register, MainPage, CreateAuction, AuctionDetail, Admin Dashboard và các view liên quan. Xử lý điều hướng, thông báo lỗi và trải nghiệm hiển thị phía client. |
| Sơn | Server & Giao tiếp Client-Server | Thiết lập server, quản lý Socket connection, tổ chức luồng gửi/nhận dữ liệu. Xây dựng `AuctionServer`, `ClientHandler`, `RequestDispatcher`, đảm bảo request được điều phối đúng handler và response trả về client ổn định. |
| Quân | Model & Service | Xây dựng các lớp model và service nghiệp vụ chính: `Auction`, `BidTransaction`, `User`, các request/response, xác thực đăng nhập/đăng ký, xử lý dữ liệu auction và bid transaction. |
| Phong | Model & Service | Phối hợp cùng Quân: chuẩn hóa các lớp dữ liệu, bổ sung enum (`AuctionStatus`, `ItemCondition`, `BidResponseStatus`, ...), entity, service xử lý nghiệp vụ và hoàn thiện logic backend cho các tính năng đấu giá. |

---

