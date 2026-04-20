# 🔨 Online Auction System (Hệ thống đấu giá trực tuyến)

## 📖 Giới thiệu dự án
Đây là dự án thuộc môn học **Lập trình nâng cao**, nhằm mục đích xây dựng một nền tảng phần mềm theo mô hình Client-Server cho phép nhiều người dùng tham gia cạnh tranh giá để mua sản phẩm trong một khoảng thời gian xác định.

Hệ thống được thiết kế chặt chẽ theo các nguyên lý Lập trình hướng đối tượng (OOP) như Encapsulation, Inheritance, Polymorphism và Abstraction. Mã nguồn có cấu trúc rõ ràng, dễ bảo trì, mở rộng và đảm bảo an toàn luồng (thread-safe) khi có nhiều người dùng tương tác đồng thời.

## 🚀 Công nghệ & Kiến trúc
* **Ngôn ngữ:** Java
* **Giao diện người dùng (GUI):** JavaFX kết hợp FXML (Áp dụng mô hình MVC phía Client).
* **Kiến trúc hệ thống:** Client - Server.
* **Giao tiếp mạng:** Socket.
* **Quản lý dự án & Build tool:** Maven (`pom.xml`).
* **Sơ đồ thiết kế:** File `Online-Auction-System-UML.drawio`.
* **Kiểm thử (Testing):** JUnit cho các logic nghiệp vụ quan trọng.

## 🧩 Design Patterns Áp dụng
* **Singleton:** Quản lý kết nối mạng và khởi tạo duy nhất cho `AuctionManager`.
* **Observer:** Cập nhật giá thầu (bid) theo thời gian thực đến toàn bộ Client đang theo dõi phiên đấu giá.
* **Factory Method:** Khởi tạo linh hoạt các loại sản phẩm đấu giá (Electronics, Art, Vehicle,...).
* **MVC (Model-View-Controller):** Phân tách rõ ràng giữa giao diện, logic điều khiển và truy xuất dữ liệu.

## ✨ Chức năng chính

### 1. Chức năng cốt lõi (Bắt buộc)
* **Quản lý người dùng:** Đăng ký/Đăng nhập.
* **Quản lý sản phẩm:** Seller có thể Thêm/Sửa/Xóa sản phẩm (Tên, mô tả, giá khởi điểm, thời gian đấu giá).
* **Cơ chế đấu giá:** Bidder đặt giá cao hơn giá hiện tại. Hệ thống kiểm tra tính hợp lệ và cập nhật người dẫn đầu.
* **Vòng đời phiên đấu giá:** Chuyển đổi trạng thái tự động (`OPEN` → `RUNNING` → `FINISHED` → `PAID`/`CANCELED`) và xác định người thắng cuộc khi hết giờ.
* **Xử lý ngoại lệ:** Ngăn chặn đặt giá thấp, đấu giá khi phiên đã đóng hoặc lỗi rớt mạng.

### 2. Chức năng nâng cao
* **Auto-Bidding:** Cho phép Bidder thiết lập giá tối đa (`maxBid`) và bước giá (`increment`). Hệ thống sẽ tự động cạnh tranh và trả giá thay người dùng.
* **Concurrent Bidding:** Xử lý an toàn dữ liệu khi có nhiều người đặt giá cùng một mili-giây, ngăn chặn tuyệt đối tình trạng lost update hay race condition.
* **Anti-sniping Algorithm:** Tự động gia hạn thêm thời gian nếu phát hiện có người đặt giá ở những giây cuối cùng của phiên.
* **Realtime Update:** Dữ liệu giá được đồng bộ lập tức qua Socket/Observer Pattern mà không cần polling (tải lại trang).
* **Bid History Visualization:** Hiển thị biểu đồ đường (line chart) trực quan hóa biến động giá thầu theo thời gian thực.

## 📂 Cấu trúc thư mục (Nổi bật)
Dự án được tổ chức theo các module để tối ưu hóa việc tái sử dụng mã nguồn:
* `com.auction.shared`: Chứa các thành phần dùng chung giữa Client và Server (Entities, Enums, Models, Request/Response payload).
* `server`: Chứa logic xử lý trung tâm, `ClientHandler`, `AuctionManager`, và kết nối Database.
* `client`: Chứa các Controller của JavaFX và giao diện FXML.
* `observer` & `singleton`: Chứa các package implement các Design Pattern cốt lõi.

## ⚙️ Hướng dẫn cài đặt và chạy thử
1. **Yêu cầu môi trường:** Cài đặt Java, Maven, và IDE (khuyến khích sử dụng IntelliJ/Eclipse).
2. Tải toàn bộ mã nguồn về máy: `git clone <repository_url>`
3. Mở mã nguồn bằng IDE.
3. Mở terminal tại thư mục gốc của dự án (`Online-Auction-System`) và chạy lệnh sau để Maven dọn dẹp, tải các thư viện cần thiết và build module `sharemodel`:
`mvn clean install`
4. Chạy file `AuctionServer`
5. Mở một terminal mới, chạy lệnh:
```` bash
cd client
mvn javafx:run