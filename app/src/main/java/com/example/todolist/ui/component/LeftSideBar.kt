package com.example.todolist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


//左侧边栏
@Composable
fun LeftSideBar(
    modifier: Modifier,
    bgColor: Color,
    textColor: Color,
    mainColor: Color,
    dividerColor: Color,
    onAddTagClick: () -> Unit,
    onBatchClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(bgColor)
            .padding(16.dp)
    ) {
        Text(
            text = "功能分类",
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        listOf("批量管理", "自定义标签").forEach { func ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(
                        color = mainColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .clickable {
                        when (func) {
                            "批量管理" -> onBatchClick()
                            "自定义标签" -> onAddTagClick()
                        }
                    }
            ) {
                Text(text = func, color = textColor)
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = dividerColor
        )

//        Text(
//            text = "快捷模板",
//            style = MaterialTheme.typography.titleMedium,
//            color = textColor
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        listOf( "日常清单","语音速记","位置提醒").forEach { temp ->
//            Text(
//                text = temp,
//                color = textColor,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp)
//                    .clickable { }
//            )
//        }
    }
}