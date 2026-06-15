package com.example.todolist.ui.component

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.dao.TodoJoinData
import com.example.todolist.util.getPriorityColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import com.example.todolist.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import sh.calvin.reorderable.rememberReorderableLazyListState

// 滑动枚举别名简化
private val StartToEnd = SwipeToDismissBoxValue.StartToEnd
private val EndToStart = SwipeToDismissBoxValue.EndToStart
//长按拖拽
private val Settled = SwipeToDismissBoxValue.Settled


//待办事务列表：接收数据库联查完整数据，移除假数据
@SuppressLint("RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteListArea(
    todoList: List<TodoJoinData>,
    cardBg: Color,
    textColor: Color,
    selectStroke: Color,
    mainColor: Color,
    onDeleteTodo:(Long)-> Unit,
    onMarkComplete:(Long)-> Unit,
    // 拖拽排序后回调，保存新顺序
    onOrderChanged: (List<TodoJoinData>) -> Unit
) {

//    全局时间格式化器：年-月-日 时：分
    val dateFormatter= remember{
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(todoList, key={it.affId}) { todo ->
            val swipeState= rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    when (dismissValue) {
                        // 右滑：标记完成
                        StartToEnd -> {
                            onMarkComplete(todo.affId)
                            false // 滑完回弹卡片
                        }
                        // 左滑：删除事务
                        EndToStart -> {
                            onDeleteTodo(todo.affId)
                            true // 滑走后移除条目
                        }
                        else  -> false
                    }
                }
            )
            SwipeToDismissBox(
                state=swipeState,
                modifier = Modifier.fillMaxWidth(),
                backgroundContent = {
                    when(swipeState.currentValue){
                        StartToEnd->{
                            Icon(
                                painter = painterResource(id = R.drawable.check),
                                contentDescription = "标记完成",
                                tint = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind{
                                        drawRect(lerp(Color.LightGray, Color(0xFF2196F3),swipeState.progress))
                                    }
                                    .wrapContentSize(Alignment.CenterStart)
                                    .padding(16.dp)
                            )
                        }
                        // 左滑背景：红色 + 删除图标（删除事务）
                        EndToStart -> {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "删除事务",
                                tint = Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(lerp(Color.LightGray, Color(0xFFF44336), swipeState.progress))
                                    .wrapContentSize(Alignment.CenterEnd)
                                    .padding(16.dp)
                            )
                        }
                        else -> {}
                    }
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, selectStroke), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                    ) {
                        //                时间渲染
                        Text(
                            text = Instant
                                .ofEpochMilli(todo.endTime)
                                .atZone(ZoneId.systemDefault())
                                .format(dateFormatter),
                            color = Color.Gray.copy(alpha = 0.7f),
                            modifier = Modifier.padding(start = 16.dp, top =5.dp ),
                            fontSize = androidx.compose.material3.MaterialTheme.typography.bodySmall.fontSize
                        )
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            //左侧：优先级彩色竖线 + 标题|详情文本
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(40.dp)
                                        .background(getPriorityColor(todo.levelName))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${todo.title} | ${todo.detail}",
                                    color = textColor,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            //右侧：圆角分类标签，背景
                            Text(
                                text = todo.label ?: "未分类",
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
    }
}