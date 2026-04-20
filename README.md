# Online-Auction-System

## Giới thiệu
Đây là dự án bài tập lớn môn Lập trình nâng cao của nhóm sinh viên năm nhất CNTT, UET. Hệ thống được xây dựng theo mô hình client-server, sử dụng Java để xử lý logic nghiệp vụ và JavaFX để phát triển giao diện người dùng cho hệ thống đấu giá trực tuyến.

## Phân chia công việc

| Thành viên | Phần việc chính | Mô tả chi tiết |
| --- | --- | --- |
| Phú | JavaFX giao diện UI | Phụ trách thiết kế và phát triển giao diện người dùng bằng JavaFX, bao gồm các màn hình như Login, Register, Main Page, Create Auction và các view liên quan. Đồng thời xử lý bố cục, điều hướng giữa các màn hình, thông báo lỗi, trải nghiệm hiển thị và các thành phần tương tác phía client. |
| Sơn | Set-up server và giao tiếp client-server | Phụ trách phần server, socket connection và luồng gửi/nhận dữ liệu giữa client và server. Công việc bao gồm tổ chức server, tiếp nhận request từ client, dispatch request đến đúng handler, trả response về client và duy trì luồng giao tiếp ổn định giữa các phía của hệ thống. |
| Quân | Model và service | Phụ trách xây dựng các lớp model và service phục vụ nghiệp vụ chính của hệ thống, bao gồm các đối tượng dùng chung giữa client-server, các class hỗ trợ xác thực, đấu giá và xử lý dữ liệu. Đồng thời tham gia hoàn thiện cấu trúc dữ liệu cho user, auction, bid transaction và các thành phần liên quan. |
| Phong | Model và service | Phối hợp cùng Quân để xây dựng và mở rộng các model/service của hệ thống. Công việc tập trung vào việc chuẩn hóa các lớp dữ liệu, bổ sung enum, entity, item model, service xử lý nghiệp vụ, cũng như hỗ trợ hoàn thiện phần logic backend cho các tính năng đấu giá. |

## Tiến độ hiện tại

### 1. Phần đã hoàn thành tương đối ổn
- Đã thiết lập cấu trúc project theo hướng tách `client`, `server`, `sharemodel`.
- Đã xây dựng luồng `login/register` cơ bản giữa client và server.
- Đã xử lý cơ chế gửi `Request` từ client lên server và nhận `Response` trả về.
- Đã tách xử lý request phía server theo hướng `RequestDispatcher` và `RequestHandler`, giúp code dễ mở rộng hơn khi thêm nhiều request mới.
- Đã bổ sung `currentUser` trong `LoginResponse` và `RegisterResponse` để client có thể lưu trạng thái người dùng sau khi đăng nhập hoặc đăng ký thành công.
- Đã có các class nền cho hệ thống đấu giá như `Auction`, `BidTransaction`, `Entity`, `User`.
- Đã xây dựng khung model cho `Item`, các lớp con của item và `ItemFactory`.
- Đã có các enum phục vụ nghiệp vụ như `AuctionStatus`, `ItemCondition`, `LoginResponseStatus`.
- Đã có một số màn hình JavaFX chính như `LoginView`, `RegisterView`, `MainPageView`, `CreateAuctionPage`.

### 2. Phần đang triển khai
- Hoàn thiện luồng hiển thị `AuctionList` sau khi đăng nhập vào màn hình chính.
- Hoàn thiện chức năng tạo phiên đấu giá mới và cập nhật lại danh sách phiên đấu giá sau khi tạo.
- Hoàn thiện luồng đặt giá (`PlaceBid`) và kiểm tra quyền thao tác của người dùng đối với từng auction.
- Kết nối chặt hơn giữa phần giao diện JavaFX và các request/response mới của hệ thống đấu giá.
- Tiếp tục tổ chức lại sơ đồ lớp, tài liệu và README để nhóm dễ theo dõi tiến độ hơn.

### 3. Phần dự kiến làm tiếp
- Bổ sung đầy đủ request/response cho các chức năng đấu giá thay vì chỉ dừng ở authentication.
- Hoàn thiện xử lý nghiệp vụ cho seller và bidder trên cùng một tài khoản người dùng.
- Xây dựng phần quản lý danh sách auction ở server và đồng bộ dữ liệu trả về client.
- Hoàn thiện UI cho phần xem chi tiết auction, tạo auction và bid.
- Kiểm tra lại toàn bộ luồng để chuẩn bị cho giai đoạn test và demo.

## Ghi chú
- Hiện tại project đã có nền tảng tốt ở phần authentication và kiến trúc request-response.
- Phần tiếp theo quan trọng nhất là hoàn thiện các tính năng cốt lõi của hệ thống đấu giá để nối từ model, service, server handler đến giao diện người dùng.
- README này có thể tiếp tục cập nhật theo từng mốc tiến độ của nhóm trong quá trình làm bài tập lớn.
