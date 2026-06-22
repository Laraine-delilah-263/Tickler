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
    LaunchedEffect(Unit) {
        delay(5000)
        onDismiss()
    }

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
            // 新配色：暖橙浅底色 + 深色文字，视觉突出不刺眼
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0) // 浅橙米色底色
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // 增加阴影，分层更明显
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp), // 加大内边距，更饱满
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo2),
                    contentDescription = "提醒图标",
                    modifier = Modifier
                        .size(26.dp)
                        .padding(end = 8.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFF57C00)) // 图标橙色调统一
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "待办截止提醒",
                        fontSize = 14.sp,
                        color = Color(0xFFE65100), // 深橙标题，强化提醒
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${expireTodo.title}:${expireTodo.detail}",
                        fontSize = 12.sp,
                        color = Color(0xFF424242),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}