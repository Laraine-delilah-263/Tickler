package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.component.BottomStatusBar
import com.example.todolist.ui.component.FilterBar
import com.example.todolist.ui.component.LeftSideBar
import com.example.todolist.ui.component.NoteListArea
import com.example.todolist.ui.component.TopNavigationBar
import com.example.todolist.ui.theme.TodoListTheme

//数据库依赖
import androidx.room.Room
import com.example.todolist.database.AppDatabase
import com.example.todolist.entity.TodoAffair
import com.example.todolist.ui.component.AddTodoDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    //初始化数据库与Dao
    private val db by lazy { AppDatabase.getDatabase(applicationContext) }
    private val todoDao by lazy { db.todoDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TodoListTheme {
                var isDarkMode by remember { mutableStateOf(false) }
                var searchText by remember { mutableStateOf("") }
                var queryResult by remember { mutableStateOf("") }

                val scope = rememberCoroutineScope()
                //弹窗控制标记
                var openAddDialog by remember { mutableStateOf(false) }

                val pageBg: Color
                val sideBarBg: Color
                val contentCardBg: Color
                val textPrimary: Color
                val mainColor: Color
                val dividerColor: Color
                val selectStrokeColor: Color

                if (isDarkMode) {
                    pageBg = Color(0xFF1E2229)
                    sideBarBg = Color(0xFF252A33)
                    contentCardBg = Color(0xFF2C303A)
                    textPrimary = Color(0xFFE8EBF0)
                    mainColor = Color(0xFF3B7EA1)
                    dividerColor = Color(0xFF444952)
                    selectStrokeColor = Color(0xFF3B7EA1)
                } else {
                    pageBg = Color(0xFFF5F7FA)
                    sideBarBg = Color.White
                    contentCardBg = Color.White
                    textPrimary = Color(0xFF333333)
                    mainColor = Color(0xFF4A90E2)
                    dividerColor = Color(0xFFE0E4EB)
                    selectStrokeColor = Color(0xFF4A90E2)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = pageBg
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopNavigationBar(

                            searchText = searchText,
                            onSearchChange = { searchText = it },
                            textColor = textPrimary,

                        )
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            LeftSideBar(
                                modifier = Modifier.width(220.dp),
                                bgColor = sideBarBg,
                                textColor = textPrimary,
                                mainColor = mainColor,
                                dividerColor = dividerColor
                            )
                            Column {
                                FilterBar(
                                    textColor = textPrimary,
                                    mainColor = mainColor,
                                    dividerColor = dividerColor
                                )
                                Box(
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                ) {
                                    NoteListArea(
                                        cardBg = contentCardBg,
                                        textColor = textPrimary,
                                        mainColor = mainColor,
                                        selectStroke = selectStrokeColor
                                    )
                                    FloatingActionButton(
                                        onClick = {
                                            openAddDialog = true
                                        },
                                        modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
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
                            }
                        }
                        BottomStatusBar(
                            isDarkMode = isDarkMode,
                            onModeChange = { isDarkMode = it },
                            bgColor = sideBarBg,
                            textColor = textPrimary,
                            dividerColor = dividerColor,
                            mainColor = mainColor
                        )
                    }
                }

                //挂载弹窗
                AddTodoDialog(
                    show = openAddDialog,
                    textColor = textPrimary,
                    bgCardColor = contentCardBg, //跟随页面卡片底色
                    closeDialog = { openAddDialog = false },
                    saveClick = { content, hour, minute ->
                        //IO线程插入数据库
                        scope.launch(Dispatchers.IO) {
                            val now = System.currentTimeMillis()
                            //标题固定，内容填输入文字，截止时间=当天+选定时分，分类/优先级暂时null
                            val todo = TodoAffair(
                                title = "新建待办",
                                detail = content,
                                startTime = now,
                                endTime = now + 24 * 3600_000,
                                categoryId = null,
                                priorityId = null
                            )
                            todoDao.insertTodo(todo)
                        }
                    }
                )
            }
        }
    }
}
