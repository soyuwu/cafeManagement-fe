# DESIGN.md — Quy tắc giao diện cho 3 phân hệ (Customer · Staff · Admin)

Mỗi thành viên tự thiết kế 1 phân hệ, nhưng cả 3 phải nhìn như cùng một ứng dụng. Tài liệu này là phần dùng chung.

## 1. Quy tắc chung (bắt buộc)

| # | Quy tắc |
|---|---------|
| 1 | **Flat design**: không gradient, không đổ bóng (shadow), không glow, không hiệu ứng nổi 3D. Phân tách bằng màu nền và viền 1px. |
| 2 | **Tuyệt đối không dùng màu neon / bão hòa cao.** Mọi màu trong tài liệu này đều là tông đất, trầm. |
| 3 | Chỉ dùng **bảng màu của phân hệ mình** + màu trạng thái (mục 5). Không tự thêm màu mới. |
| 4 | Cùng một **font, bo góc, khoảng cách, icon** ở cả 3 phân hệ (mục 4). |
| 5 | Chữ thường phải đạt tương phản **≥ 4.5:1** với nền (các cặp màu dưới đây đã chọn để đạt). Màu "trang trí" ghi chú rõ là không dùng cho chữ nhỏ. |
| 6 | Vùng chạm tối thiểu **44×44 px** (Staff/Customer trên điện thoại). |
| 7 | Không dùng emoji làm icon. Dùng 1 bộ icon nét (stroke) đồng nhất. |
| 8 | Màu không là cách duy nhất để phân biệt trạng thái: luôn kèm chữ (ví dụ "Đã thanh toán"). |

## 2. Admin — đen · trắng · nâu nhạt (minimalism)

Cảm giác: sạch, nhiều khoảng trắng, dữ liệu là trọng tâm. Nâu nhạt chỉ làm điểm nhấn.

| Vai trò | Tên | Mã màu | Ghi chú |
|---|---|---|---|
| Nền trang | White | `#FFFFFF` | |
| Nền phụ (card, bảng xen kẽ) | Paper | `#F6F3EF` | |
| Viền / đường kẻ | Line | `#E4DDD5` | 1px |
| Chữ chính | Ink | `#1A1A1A` | trên trắng 17:1 |
| Chữ phụ | Muted | `#6B635B` | trên trắng ~5.9:1 |
| Nút chính / sidebar | Black | `#1A1A1A` | chữ trắng |
| Hover trên nền đen | Charcoal | `#2E2A27` | |
| Nâu nhạt nhấn | Light Brown | `#8B5E3C` | nút phụ, link, cột biểu đồ chính; chữ trắng ~5.9:1 |
| Nâu nhạt nền | Sand | `#EFE6DC` | nền badge, hàng được chọn |
| Nâu trang trí | Tan | `#C9A583` | biểu đồ phụ; **không dùng cho chữ** |

Biểu đồ (doanh thu, top món): dùng `#8B5E3C` làm series chính, `#C9A583` làm series phụ, `#1A1A1A` cho đường xu hướng. Tối đa 3 series.

### Thư viện / API gợi ý (Admin dạng web)
- UI: **shadcn/ui** (Tailwind) hoặc **Ant Design**; đặt theme theo bảng trên, tắt shadow.
- Biểu đồ: **Recharts** hoặc **Chart.js** (tắt gradient, shadow).
- Bảng dữ liệu: **TanStack Table**. Icon: **Lucide**.
- Dữ liệu lấy từ API đã có trong tài liệu dự án:
  `GET /admin/reports/revenue`, `GET /admin/reports/top-products`, `GET /admin/reports/payments`,
  `GET/POST/PUT /admin/products`, `GET/POST /admin/staff`.
- Nếu admin chạy trong app Android: **Material 3 (Compose)** + biểu đồ **Vico**.

## 3. Staff — nâu đậm hơn Admin

Cảm giác: làm việc nhanh, chữ đọc rõ trong quán đông, tương phản cao. Nền sáng, thanh tiêu đề nâu đậm.

| Vai trò | Tên | Mã màu | Ghi chú |
|---|---|---|---|
| Thanh tiêu đề / nút chính | Espresso | `#4A2E1F` | chữ trắng ~12:1 |
| Tiêu đề đậm nhất / nhấn mạnh | Dark Roast | `#33201A` | |
| Nền ứng dụng | Linen | `#F1EAE3` | |
| Nền card | White | `#FFFFFF` | |
| Viền | Mocha Line | `#D8C9BB` | 1px |
| Chữ chính | Bean | `#2B1B14` | |
| Chữ phụ | Muted | `#6F5A4C` | trên Linen ~5.2:1 |
| Nút phụ / đơn đang chọn | Walnut | `#7A4B2F` | chữ trắng ~7:1 |
| Nền badge nhạt | Cocoa Tint | `#E6D6C6` | |
| Trang trí | Caramel | `#B8895F` | **không dùng cho chữ nhỏ** |

Đơn từ app khách và đơn tại quầy phân biệt bằng **nhãn chữ** ("APP KHÁCH" / "QUẦY") trên nền `#E6D6C6`, không dùng thêm màu lạ.

## 4. Customer — các tông ấm thiên nâu

Cảm giác: ấm, thân thiện, giống quán cà phê. Nhiều màu ấm hơn hai phân hệ còn lại nhưng vẫn trầm.

| Vai trò | Tên | Mã màu | Ghi chú |
|---|---|---|---|
| Nền ứng dụng | Cream | `#FBF5EE` | |
| Nền phụ / nền ảnh món | Latte | `#F0E0CC` | |
| Nền card | White | `#FFFFFF` | viền `#EADBC8` |
| Nút chính | Mocha | `#8A5232` | chữ trắng ~6.6:1 |
| Nút chính (nhấn) | Roast | `#6B3E24` | trạng thái pressed |
| Nhấn nổi bật (giá, huy hiệu) | Terracotta | `#B0503A` | chữ trắng ~5.3:1 |
| Chữ chính | Cocoa | `#3B2A20` | |
| Chữ phụ | Muted | `#7A6657` | trên Cream ~4.9:1 |
| Trang trí | Caramel | `#C48A55` | biểu tượng lớn, đường trang trí; **không dùng cho chữ nhỏ** |
| Trang trí | Sage | `#8C9A78` | tag "mới", "ít đường"…; **không dùng cho chữ nhỏ** |

## 5. Màu trạng thái dùng chung (cả 3 phân hệ)

Trầm, không neon. Dùng cặp **chữ đậm trên nền nhạt**.

| Trạng thái | Chữ | Nền nhạt | Dùng cho |
|---|---|---|---|
| Thành công | `#2F5D3A` | `#DDEBDD` | PAID, READY, SERVED |
| Cảnh báo / chờ | `#7A4A0C` | `#F5E3C8` | UNPAID, PENDING |
| Đang xử lý | `#33506B` | `#DCE6EE` | PREPARING |
| Lỗi / huỷ | `#8F2C22` | `#F3DAD5` | CANCELLED, lỗi nhập liệu |

Ánh xạ theo luồng đơn hàng: PENDING = cảnh báo · PREPARING = đang xử lý · READY và SERVED = thành công · CANCELLED = lỗi. `payment_status`: UNPAID = cảnh báo · PAID = thành công · REFUNDED = lỗi.

## 6. Thành phần dùng chung

- **Font**: Be Vietnam Pro (hỗ trợ tiếng Việt tốt). Cỡ chữ: 12 / 14 / 16 / 20 / 24. Tiêu đề 600–700, nội dung 400–500.
- **Bo góc**: nút và ô nhập 12px, card 14px, badge 8px. Admin có thể giảm còn 8/10/6px cho gọn, không đổi thêm.
- **Khoảng cách**: bội số của 4px (4, 8, 12, 16, 24, 32). Lề màn hình điện thoại 16px.
- **Viền**: 1px, không dùng shadow. Phần "nổi" (bottom sheet) chỉ tách bằng viền trên.
- **Icon**: Lucide hoặc Material Symbols (outlined), nét 1.8–2px, cùng một bộ cho cả 3 phân hệ.
- **Nút**: cao 44–56px; nút chính tô đặc, nút phụ viền 1px nền trắng, nút nguy hiểm viền màu lỗi.
- **Tiền tệ**: `87.000đ` (dấu chấm ngăn nghìn, đuôi "đ"). **Giờ**: `14:32`.

## 7. Mã màu để dùng trong code

### CSS (Admin web)
```css
:root {
  --bg: #FFFFFF;        --paper: #F6F3EF;     --line: #E4DDD5;
  --ink: #1A1A1A;      --muted: #6B635B;
  --brown: #8B5E3C;    --sand: #EFE6DC;      --tan: #C9A583;
  --ok: #2F5D3A;       --ok-bg: #DDEBDD;
  --warn: #7A4A0C;     --warn-bg: #F5E3C8;
  --info: #33506B;     --info-bg: #DCE6EE;
  --err: #8F2C22;      --err-bg: #F3DAD5;
}
```

### Kotlin / Jetpack Compose (Staff và Customer)
```kotlin
// Staff
val StaffEspresso = Color(0xFF4A2E1F)
val StaffWalnut   = Color(0xFF7A4B2F)
val StaffLinen    = Color(0xFFF1EAE3)
val StaffLine     = Color(0xFFD8C9BB)
val StaffInk      = Color(0xFF2B1B14)
val StaffMuted    = Color(0xFF6F5A4C)
val StaffTint     = Color(0xFFE6D6C6)

// Customer
val CustCream      = Color(0xFFFBF5EE)
val CustLatte      = Color(0xFFF0E0CC)
val CustMocha      = Color(0xFF8A5232)
val CustTerracotta = Color(0xFFB0503A)
val CustInk        = Color(0xFF3B2A20)
val CustMuted      = Color(0xFF7A6657)
val CustCaramel    = Color(0xFFC48A55)
val CustSage       = Color(0xFF8C9A78)

// Trạng thái dùng chung
val Ok = Color(0xFF2F5D3A);   val OkBg = Color(0xFFDDEBDD)
val Warn = Color(0xFF7A4A0C); val WarnBg = Color(0xFFF5E3C8)
val Info = Color(0xFF33506B); val InfoBg = Color(0xFFDCE6EE)
val Err = Color(0xFF8F2C22);  val ErrBg = Color(0xFFF3DAD5)
```
Trong Material 3 (Compose): map `primary` = nút chính, `background` = nền ứng dụng, `surface` = card, `onPrimary` = trắng; đặt `elevation = 0.dp` cho Card/Button để giữ flat.

## 8. Checklist trước khi nộp màn hình

- [ ] Không có gradient, shadow, màu neon.
- [ ] Chỉ dùng màu trong bảng của phân hệ mình + màu trạng thái.
- [ ] Chữ nhỏ không dùng màu "trang trí" (Tan, Caramel, Sage).
- [ ] Nút/vùng chạm ≥ 44px; icon cùng một bộ.
- [ ] Trạng thái đơn có chữ kèm màu; tiền tệ đúng định dạng.
- [ ] Bố cục theo đúng luồng trong tài liệu dự án (PENDING → PREPARING → READY → SERVED; thanh toán tách riêng).
