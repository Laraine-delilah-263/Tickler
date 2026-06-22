package com.example.todolist.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import com.example.todolist.model.dao.TodoJoinData
import kotlinx.coroutines.delay

@Composable
fun TodoExpireTopToast(
    expireTodo: TodoJoinData,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    // 进入弹窗自动倒计时3秒后关闭
    LaunchedEffect(Unit) {
        delay(3000)
        onDismiss()
    }

//    顶部定位
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.Top
            ) {
                // 左侧小苹果图标
                Image(
                    painter = painterResource(id = R.drawable.logo2),
                    contentDescription = "提醒图标",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 5.dp)
                )
                // 中间文字区域
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "待办截止提醒",
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${expireTodo.title}:${expireTodo.detail}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}