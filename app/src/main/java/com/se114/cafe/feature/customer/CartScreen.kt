package com.se114.cafe.feature.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.TableBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se114.cafe.ui.theme.*

@Composable
fun CartScreen(
    tableName: String = "Giao tới bàn T05 · Tầng 1",
    initialCartItems: List<CartItem> = MockCustomerData.cartItems,
    onBackClick: () -> Unit = {},
    onEditItem: (CartItem) -> Unit = {},
    onOrderClick: (String, List<CartItem>) -> Unit = { _, _ -> }
) {
    var cartItems by remember { mutableStateOf(initialCartItems) }
    var note by remember { mutableStateOf("") }

    val totalQuantity = remember(cartItems) { cartItems.sumOf { it.quantity } }
    val totalPrice = remember(cartItems) { cartItems.sumOf { it.totalPrice } }

    Scaffold(
        containerColor = CafeAppBg,
        topBar = {
            CartTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            CartBottomOrderBar(
                totalQuantity = totalQuantity,
                totalPrice = totalPrice,
                onOrderClick = { onOrderClick(note, cartItems) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // 1. Vị trí giao tới bàn
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CafeGreenLight,
                    border = BorderStroke(1.dp, CafeGreenBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TableBar,
                            contentDescription = null,
                            tint = CafeGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tableName,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CafeGreenPrimary
                            )
                        )
                    }
                }
            }

            // 2. Danh sách các món trong giỏ
            items(cartItems, key = { it.id }) { item ->
                CartItemCard(
                    item = item,
                    onEditClick = { onEditItem(item) },
                    onQuantityMinus = {
                        cartItems = if (item.quantity > 1) {
                            cartItems.map {
                                if (it.id == item.id) it.copy(quantity = it.quantity - 1) else it
                            }
                        } else {
                            // Xoá món nếu số lượng giảm về 0
                            cartItems.filterNot { it.id == item.id }
                        }
                    },
                    onQuantityPlus = {
                        cartItems = cartItems.map {
                            if (it.id == item.id) it.copy(quantity = it.quantity + 1) else it
                        }
                    }
                )
            }

            // 3. Ghi chú cho quán
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ghi chú cho quán",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CafeTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, Color(0xFFE2E8E5)), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = note,
                            onValueChange = { note = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = CafeTextPrimary,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(CafeGreenPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (note.isEmpty()) {
                                    Text(
                                        text = "Ví dụ: mang ra cùng lúc",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            color = CafeTextSecondary,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }

            // 4. Lưu ý phương thức thanh toán
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CafeWarningBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = CafeWarningText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Thanh toán trực tiếp với nhân viên sau khi nhận món (tiền mặt hoặc chuyển khoản QR).",
                            style = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = CafeWarningText
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Quay lại",
            tint = CafeTextPrimary,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBackClick() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Giỏ hàng",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CafeTextPrimary
            )
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onEditClick: () -> Unit,
    onQuantityMinus: () -> Unit,
    onQuantityPlus: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CafeCardBg,
        border = BorderStroke(1.dp, CafeCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tên món + Giá
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeTextPrimary
                    )
                )
                Text(
                    text = formatCurrency(item.totalPrice),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeTextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Chi tiết tùy chọn
            Text(
                text = item.optionsDescription,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = CafeTextSecondary
                )
            )

            // Topping nếu có
            item.toppingDescription?.let { toppingText ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = toppingText,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = CafeTextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dòng dưới: Nút Sửa món + Bộ điều khiển số lượng [-] [1] [+]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sửa món",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CafeGreenPrimary
                    ),
                    modifier = Modifier.clickable { onEditClick() }
                )

                // Bộ tăng giảm số lượng
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Nút trừ (-)
                    Surface(
                        onClick = onQuantityMinus,
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8E5)),
                        modifier = Modifier.size(width = 36.dp, height = 32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Giảm",
                                tint = CafeTextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Số lượng
                    Text(
                        text = "${item.quantity}",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CafeTextPrimary
                        ),
                        modifier = Modifier.widthIn(min = 20.dp),
                        textAlign = TextAlign.Center
                    )

                    // Nút cộng (+)
                    Surface(
                        onClick = onQuantityPlus,
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8E5)),
                        modifier = Modifier.size(width = 36.dp, height = 32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = CafeTextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartBottomOrderBar(
    totalQuantity: Int,
    totalPrice: Int,
    onOrderClick: () -> Unit
) {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFECEFEF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Dòng tạm tính
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tạm tính ($totalQuantity món)",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = CafeTextSecondary
                    )
                )
                Text(
                    text = formatCurrency(totalPrice),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeTextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nút "Đặt món"
            Button(
                onClick = onOrderClick,
                enabled = totalQuantity > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CafeGreenPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFBDC7C2),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "Đặt món",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    CartScreen()
}
