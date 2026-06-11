package com.example.todolist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolist.entity.Category
import com.example.todolist.entity.Priority
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

//分类筛选列表页面
@Composable
fun FilterBar(
    textColor: Color,
    mainColor: Color,
    dividerColor: Color,
    categoryList:List<Category>,//分类标签数据
    priorityList:List<Priority>,//等级标签数据
    onCategorySelect:(String)->Unit,
    onPrioritySelect:(String)->Unit,
    currentSelectCategory:String,
    currentSelectPriority:String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                text = "全部分类",
                textColor = if(currentSelectCategory == "全部分类") Color.White else textColor,
                bgColor = if(currentSelectCategory == "全部分类") mainColor else mainColor.copy(alpha = 0.1f),
                onClick = { onCategorySelect("全部分类")}
            )
//            数据库循环渲染分类
            categoryList.forEach { cate->
                FilterChip(
                    text = cate.label,
                    textColor = if(currentSelectCategory == cate.label) Color.White else textColor,
                    bgColor = if(currentSelectCategory == cate.label) mainColor else mainColor.copy(alpha = 0.1f),
                    onClick = { onCategorySelect(cate.label )}
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 固定全部选项
            FilterChip(
                text = "全部等级",
                textColor = if(currentSelectPriority == "全部等级") Color.White else textColor,
                bgColor = if(currentSelectPriority == "全部等级") mainColor else mainColor.copy(alpha = 0.1f),
                onClick = { onPrioritySelect ("全部等级")}
            )
            // 数据库循环渲染优先级
            priorityList.forEach { prio ->
                FilterChip(
                    text = prio.levelName,
                    textColor = if(currentSelectPriority == prio.levelName) Color.White else textColor,
                    bgColor = if(currentSelectPriority == prio.levelName) mainColor else mainColor.copy(alpha = 0.1f),
                    onClick = { onPrioritySelect (prio.levelName)}
                )
            }
        }

        Divider(color = dividerColor, thickness = 1.dp)
    }
}

@Composable
fun FilterChip(
    text: String,
    textColor: Color,
    bgColor: Color,
    onClick:()->Unit,
) {
    Box(
        modifier = Modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable (onClick = onClick)
    ) {
        Text(text = text, color = textColor)
    }
}