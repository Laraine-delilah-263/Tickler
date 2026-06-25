package com.example.todolist.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import androidx.compose.ui.unit.sp
import com.example.todolist.R

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
    currentSelectPriority:String,
    batchDeleteMode: Boolean, // 批量模式开关，控制是否显示删除叉
    onCateDeleteClick: (Category) -> Unit // 分类删除回调
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
                onClick = { onCategorySelect("全部分类")},
                showDeleteIcon=false,
                onDeleteClick={}
            )
//            数据库循环渲染分类
            categoryList.forEach { cate->
                FilterChip(
                    text = cate.label,
                    textColor = if(currentSelectCategory == cate.label) Color.White else textColor,
                    bgColor = if(currentSelectCategory == cate.label) mainColor else mainColor.copy(alpha = 0.1f),
                    onClick = { onCategorySelect(cate.label )},
                    showDeleteIcon=batchDeleteMode,
                    onDeleteClick={
                        onCateDeleteClick(cate)
                    }
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
                onClick = { onPrioritySelect ("全部等级")},
                showDeleteIcon = false,
                onDeleteClick = {}
            )
            // 数据库循环渲染优先级
            priorityList.forEach { prio ->
                FilterChip(
                    text = prio.levelName,
                    textColor = if(currentSelectPriority == prio.levelName) Color.White else textColor,
                    bgColor = if(currentSelectPriority == prio.levelName) mainColor else mainColor.copy(alpha = 0.1f),
                    onClick = { onPrioritySelect (prio.levelName)},
                    showDeleteIcon = false,
                    onDeleteClick = {}
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
    showDeleteIcon: Boolean,
    onDeleteClick: () -> Unit
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
        if (showDeleteIcon){
            Box(modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x=8.dp,y=(-8.dp))
                .size(24.dp)
                .background(Color.Gray, CircleShape)//Color(0xFFF44336)Color(0xFFEEEEEE)
                .clickable{
                    onDeleteClick()
                },
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.cancle),
                    contentDescription = "删除标签",
                    modifier = Modifier
                        .size(18.dp)
                        .padding(3.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}