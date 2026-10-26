# CafeManagement FE (Android)

Frontend Android của dự án **CafeManagement**, được xây dựng bằng **Kotlin** và **Jetpack Compose**. Repository này dành cho thành viên nhóm clone mã nguồn, mở bằng Android Studio và chạy thử ứng dụng.

## 1. Công nghệ sử dụng

- Kotlin
- Android SDK
- Jetpack Compose
- Gradle Kotlin DSL
- Retrofit (gọi API backend)

> Đây là frontend Android. Để các chức năng cần dữ liệu máy chủ hoạt động, backend CafeManagement phải được triển khai hoặc chạy và frontend phải được cấu hình đúng địa chỉ API.

## 2. Yêu cầu trước khi chạy

Cài đặt:

1. **Git** để clone repository.
2. **Android Studio** phiên bản hỗ trợ cấu hình Android Gradle Plugin của dự án.
3. **Android SDK** tương ứng với cấu hình trong project.
4. **JDK** tương thích với phiên bản Gradle/Android Gradle Plugin của project. Ưu tiên dùng JDK được cấu hình trong Android Studio tại **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**.
5. Một Android Emulator hoặc thiết bị Android thật đã bật Developer options và USB debugging.

Không cần cài Gradle riêng vì repository có Gradle Wrapper (`gradlew`, `gradlew.bat`).

## 3. Clone repository

Mở Terminal hoặc PowerShell tại thư mục muốn lưu dự án, sau đó chạy:

```bash
git clone <URL_REPOSITORY>
cd <TEN_THU_MUC_REPOSITORY>
```

Thay `<URL_REPOSITORY>` bằng URL GitHub của repository frontend và `<TEN_THU_MUC_REPOSITORY>` bằng tên thư mục được tạo sau khi clone.

Nếu repository đã có trên máy, cập nhật code mới nhất bằng:

```bash
git pull
```

## 4. Mở project bằng Android Studio

1. Mở Android Studio.
2. Chọn **Open**.
3. Chọn thư mục gốc vừa clone — thư mục chứa `settings.gradle.kts`.
4. Chờ **Gradle Sync** hoàn tất.
5. Nếu Android Studio yêu cầu cài Android SDK hoặc Build Tools, cài các thành phần được yêu cầu.
6. Nếu xuất hiện lỗi JDK/Gradle, kiểm tra mục **Gradle JDK** theo phần yêu cầu ở trên.

**Lưu ý:** Không cần tự tạo lại project Android mới và không nên chỉ mở riêng thư mục `app/`. Hãy mở thư mục gốc của repository.

## 5. Build thử bằng Terminal

Trước tiên, mở Terminal ở thư mục gốc dự án.

### Windows (PowerShell)

```powershell
.\gradlew.bat assembleDebug
```

### macOS / Linux

```bash
./gradlew assembleDebug
```

Nếu build thành công, kết quả sẽ có dòng `BUILD SUCCESSFUL`. APK debug thường được tạo tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Nếu build thất bại, xem lỗi đầu tiên trong **Build Output** hoặc Terminal. Đảm bảo máy đã có kết nối Internet để Gradle tải dependencies.

## 6. Chạy ứng dụng trên Emulator

1. Trong Android Studio, mở **Tools → Device Manager**.
2. Tạo hoặc khởi động một Android Virtual Device (AVD).
3. Chờ thiết bị khởi động hoàn toàn.
4. Chọn cấu hình chạy `app` trên thanh công cụ.
5. Chọn emulator đang chạy.
6. Nhấn **Run ▶**.

Có thể dùng thiết bị Android thật thay cho emulator. Khi chạy trên thiết bị thật, cần bảo đảm thiết bị và backend có thể kết nối qua mạng.

## 7. Cấu hình địa chỉ Backend API

Frontend sử dụng Retrofit để gọi API. Kiểm tra các file liên quan, chẳng hạn:

```text
app/src/main/java/com/se114/cafe/network/apiService.kt
app/src/main/java/com/se114/cafe/network/retrofitClient.kt
```

Đảm bảo `baseUrl` trỏ đến địa chỉ backend đang chạy và kết thúc bằng dấu `/` nếu cấu hình Retrofit yêu cầu.

Ví dụ địa chỉ thường dùng:

| Môi trường | Địa chỉ tham khảo |
|---|---|
| Android Emulator truy cập backend chạy trên chính máy tính host | `http://10.0.2.2:<PORT>/` |
| Frontend chạy trên máy tính truy cập backend cùng máy | `http://localhost:<PORT>/` |
| Điện thoại thật truy cập backend trên máy tính qua Wi-Fi | `http://<LAN_IP_MAY_TINH>:<PORT>/` |

Thay `<PORT>` bằng cổng backend thực tế và `<LAN_IP_MAY_TINH>` bằng IP LAN của máy tính. Với điện thoại thật, hai thiết bị cần có kết nối mạng phù hợp và firewall phải cho phép kết nối.

**Quan trọng:**
- `localhost` bên trong Android Emulator là chính emulator, không phải máy tính host.
- Nếu backend dùng HTTPS với chứng chỉ hợp lệ, ưu tiên sử dụng HTTPS.
- Android có thể chặn HTTP không mã hóa tùy cấu hình ứng dụng. Chỉ bật cleartext traffic cho môi trường phát triển khi thật sự cần thiết; không dùng cấu hình này một cách tùy tiện trong production.
- Không commit mật khẩu, token, API key hoặc thông tin bí mật vào GitHub.

## 8. Những file không cần clone thủ công

Các thư mục/file sau được Git bỏ qua hoặc thường được tạo lại trên từng máy:

- `build/`, `.gradle/`: kết quả build và cache.
- `local.properties`: cấu hình cục bộ, thường chứa đường dẫn Android SDK.
- `.idea/`: một số cấu hình Android Studio cá nhân.
- File APK/AAB đã build.

Đây là hành vi bình thường. Không cần copy các file này từ máy của thành viên khác. Android Studio/Gradle sẽ tạo lại những file cần thiết.

## 9. Một số lỗi thường gặp

### Gradle Sync hoặc build thất bại

- Kiểm tra kết nối Internet.
- Kiểm tra **Gradle JDK** trong Android Studio.
- Cài Android SDK/Build Tools mà project yêu cầu.
- Đọc lỗi đầu tiên trong Build Output trước khi thử thay đổi phiên bản dependency.

### Không kết nối được backend

- Kiểm tra backend đã chạy và đúng cổng chưa.
- Kiểm tra `baseUrl` trong Retrofit.
- Nếu dùng emulator, thử địa chỉ host `10.0.2.2` thay cho `localhost`.
- Kiểm tra quyền Internet trong `AndroidManifest.xml` và cấu hình HTTP/HTTPS.
- Kiểm tra firewall hoặc kết nối mạng nếu dùng điện thoại thật.

### Chạy được app nhưng đăng nhập hoặc tải dữ liệu thất bại

Build thành công không đồng nghĩa API hoạt động đúng. Kiểm tra endpoint, request/response, trạng thái backend và log trong **Logcat**.

## 10. Quy trình làm việc nhóm

Trước khi bắt đầu làm việc:

```bash
git pull
```

Sau khi sửa code:

```bash
git status
git add <FILE_HOAC_THU_MUC>
git commit -m "Describe your changes"
git push
```

Nên tạo branch riêng cho tính năng khi nhóm bắt đầu phát triển song song. Không commit `local.properties`, file build, khóa ký ứng dụng hoặc thông tin bí mật.

---

## Trạng thái hiện tại

Mã nguồn đã được build thử trên máy phát triển bằng Gradle Wrapper và nhận kết quả `BUILD SUCCESSFUL`. Thành viên khác vẫn cần đồng bộ SDK/JDK phù hợp và kiểm tra chạy trên thiết bị của mình.
