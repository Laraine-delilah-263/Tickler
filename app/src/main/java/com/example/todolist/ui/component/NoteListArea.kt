package com.example.todolist.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

//待办事务列表
@Composable
fun NoteListArea(
    cardBg: Color,
    textColor: Color,
    mainColor: Color,
    selectStroke: Color
) {
    // 数据：备注内容 + 对应优先级标签
    val noteList = remember {
        listOf(
            "车辆保养提醒 | 下次保养时间：下月5号" to "紧急",
            "高速站点记录 | 途经XX、XX服务区" to "重要",
            "限行提醒 | 今日尾号限行：1、6" to "常规",
            "购物清单 | 矿泉水、纸巾、车载香薰" to "暂缓"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(noteList) { (noteText, tagName) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, selectStroke), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //左侧：竖线+正文
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(40.dp)
                                .background(mainColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = noteText,
                            color = textColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    //右侧：圆角标签
                    Text(
                        text = tagName,
                        color = Color.White,
                        modifier = Modifier
                            .background(mainColor, RoundedCornerShape(99.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}