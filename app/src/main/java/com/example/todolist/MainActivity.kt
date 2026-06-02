
package com.example.todolist


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.theme.TodoListTheme

import androidx.room.Room
import com.example.todolist.database.AppDatabase

import androidx.compose.ui.graphics.ColorFilter

class MainActivity : ComponentActivity() {

    // 全局数据库实例
//    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {

        // 初始化数据库
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "todo_database" // 数据库文件名
//        ).build()

        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                // 全局明暗模式状态
                var isDarkMode by remember { mutableStateOf(false) }
                // 搜索关键词
                var searchText by remember { mutableStateOf("") }

                // ===================== 全局主题色定义 =====================
                val pageBg: Color
                val sideBarBg: Color
                val contentCardBg: Color
                val textPrimary: Color
                val mainColor: Color
                val dividerColor: Color
                val selectStrokeColor: Color

                if (isDarkMode) {
                    // 夜间主题
                    pageBg = Color(0xFF1E2229)
                    sideBarBg = Color(0xFF252A33)
                    contentCardBg = Color(0xFF2C303A)
                    textPrimary = Color(0xFFE8EBF0)
                    mainColor = Color(0xFF3B7EA1)
                    dividerColor = Color(0xFF444952)
                    selectStrokeColor = Color(0xFF3B7EA1)
                } else {
                    // 日间主题
                    pageBg = Color(0xFFF5F7FA)
                    sideBarBg = Color.White
                    contentCardBg = Color.White
                    textPrimary = Color(0xFF333333)
                    mainColor = Color(0xFF4A90E2)
                    dividerColor = Color(0xFFE0E4EB)
                    selectStrokeColor = Color(0xFF4A90E2)
                }

                // 根布局，全局背景渲染
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = pageBg
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 1. 顶部导航栏
                        TopNavigationBar(
                            isDarkMode = isDarkMode,
                            onModeChange = { isDarkMode = it },
                            searchText = searchText,
                            onSearchChange = { searchText = it },
                            textColor = textPrimary,
                            mainColor = mainColor
                        )

                        // 3. 主体三分栏布局（左侧+中间+右侧）
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            // 左侧侧边栏 固定宽度
                            LeftSideBar(
                                modifier = Modifier.width(220.dp),
                                bgColor = sideBarBg,
                                textColor = textPrimary,
                                mainColor = mainColor,
                                dividerColor = dividerColor
                            )

                            Column() {
                                // 2. 顶部筛选栏（标签分类 + 紧急程度）
                                FilterBar(
                                    textColor = textPrimary,
                                    mainColor = mainColor,
                                    dividerColor = dividerColor
                                )

                                // 中间笔记列表区 占剩余宽度
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    NoteListArea(
                                        cardBg = contentCardBg,
                                        textColor = textPrimary,
                                        mainColor = mainColor,
                                        selectStroke = selectStrokeColor
                                    )

                                    // 中间区域右下角 悬浮加号按钮
                                    FloatingActionButton(
                                        onClick = { /* 新建笔记逻辑 */ },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(24.dp),
                                        shape = CircleShape,
                                        containerColor = mainColor
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.build),
                                            contentDescription = "新建笔记",
                                            modifier = Modifier.size(24.dp),
                                            colorFilter = ColorFilter.tint(Color.White)
                                        )
                                    }
                                }

                                // 右侧详情/编辑区 固定宽度
//                            RightDetailArea(
//                                modifier = Modifier.width(360.dp),
//                                bgColor = sideBarBg,
//                                textColor = textPrimary,
//                                dividerColor = dividerColor
//                            )
                            }

                        }

                        // 4. 底部状态栏
                        BottomStatusBar(
                            bgColor = sideBarBg,
                            textColor = textPrimary,
                            dividerColor = dividerColor,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}

// region 1. 顶部导航栏
@Composable
fun TopNavigationBar(
    isDarkMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    searchText: String,
    onSearchChange: (String) -> Unit,
    textColor: Color,
    mainColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧：APP图标 + 标题
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 替换为你 mipmap 下的图标
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "应用图标",
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "车载记事本",
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )
        }

        // 中间：搜索框
        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 32.dp)
                .height(56.dp),
            placeholder = { Text("搜索标题/正文...",
                color = textColor.copy(alpha = 0.6f),
                maxLines = 1//单行显示
            ) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "搜索",
                    modifier = Modifier.size(24.dp),
//                    colorFilter = ColorFilter.tint(Color.White)
                    // 图标颜色跟随文字，不再用纯白
                    colorFilter = ColorFilter.tint(textColor.copy(alpha = 0.6f))
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // 右侧：日夜模式切换
        Switch(
            checked = isDarkMode,
            onCheckedChange = onModeChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = mainColor,
                checkedTrackColor = mainColor.copy(alpha = 0.5f)
            )
        )
    }
}
// endregion

// region 2. 顶部筛选栏
@Composable
fun FilterBar(
    textColor: Color,
    mainColor: Color,
    dividerColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 第一行：标签分类
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("全部", "行程备忘", "待办事项", "用车记录", "生活备忘").forEach { tag ->
                FilterChip(
                    text = tag,
                    textColor = textColor,
                    selectColor = mainColor
                )
            }
        }

        // 第二行：紧急程度
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
//                .border(width = 1.dp, color = dividerColor)
            ,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("全部等级", "紧急", "重要", "常规", "暂缓").forEach { level ->
                FilterChip(
                    text = level,
                    textColor = textColor,
                    selectColor = mainColor
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
    selectColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = selectColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { /* 筛选点击逻辑 */ }
    ) {
        Text(text = text, color = textColor, style = MaterialTheme.typography.bodyMedium)
    }
}
// endregion

// region 3. 左侧侧边栏
@Composable
fun LeftSideBar(
    modifier: Modifier,
    bgColor: Color,
    textColor: Color,
    mainColor: Color,
    dividerColor: Color
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

        // 功能按钮
        listOf("我的置顶", "批量管理", "自定义标签").forEach { func ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(
                        color = mainColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .clickable { /* 点击逻辑 */ }
            ) {
                Text(text = func, color = textColor)
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = dividerColor
        )

        // 快捷模板
        Text(
            text = "快捷模板",
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))
        listOf("车辆记录", "出行记录", "日常清单").forEach { temp ->
            Text(
                text = temp,
                color = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { /* 模板点击 */ }
            )
        }
    }
}
// endregion

// region 4. 中间笔记列表区
@Composable
fun NoteListArea(
    cardBg: Color,
    textColor: Color,
    mainColor: Color,
    selectStroke: Color
) {
    // 模拟笔记数据
    val noteList = remember {
        listOf(
            "车辆保养提醒 | 下次保养时间：下月5号",
            "高速站点记录 | 途经XX、XX服务区",
            "限行提醒 | 今日尾号限行：1、6",
            "购物清单 | 矿泉水、纸巾、车载香薰"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(noteList) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = selectStroke,
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左侧优先级色条
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .background(mainColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = note,
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
// endregion

// region 5. 右侧详情/编辑区
@Composable
fun RightDetailArea(
    modifier: Modifier,
    bgColor: Color,
    textColor: Color,
    dividerColor: Color
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(bgColor)
            .padding(16.dp)
    ) {
        Text(
            text = "笔记详情 / 编辑",
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = dividerColor
        )

        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("请输入笔记标题", color = textColor.copy(alpha = 0.6f)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text("请输入笔记正文", color = textColor.copy(alpha = 0.6f)) },
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}
// endregion

// region 6. 底部状态栏
@Composable
fun BottomStatusBar(
    bgColor: Color,
    textColor: Color,
    dividerColor: Color,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
//            内边距水平16，上下10
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "当前模式：${if (isDarkMode) "夜间模式" else "日间模式"}",
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}





