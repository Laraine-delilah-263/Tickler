package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolist.ui.component.BottomStatusBar
import com.example.todolist.ui.component.FilterBar
import com.example.todolist.ui.component.LeftSideBar
import com.example.todolist.ui.component.NoteListArea
import com.example.todolist.ui.component.TopNavigationBar
import com.example.todolist.ui.theme.TodoListTheme
//数据库依赖
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.model.entity.TodoAffair
import com.example.todolist.ui.component.AddTodoDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.todolist.ui.component.TodoExpireTopToast
import com.example.todolist.ui.component.AddCategoryDialog
import com.example.todolist.ui.component.TodoDetailDialog
import com.example.todolist.viewmodel.MainViewModel
import com.example.todolist.viewmodel.MainViewModelFactory
import androidx.compose.ui.platform.LocalLifecycleOwner

class MainActivity : ComponentActivity() {
    // 获取ViewModel实例
    private val mainVm: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainVm.initDatabaseDefaultData()
        setContent {
            TodoListTheme {
//                事务删除：收集待删除事务id状态
                val pendingDeleteId by mainVm.pendingDeleteId.collectAsStateWithLifecycle()
//                分类删除弹窗
                var showCateWarnDialog by remember { mutableStateOf(false) }
//                详情弹窗
                var showDetailDialog by remember { mutableStateOf(false) }
                var targetDetailTodo by remember { mutableStateOf<TodoJoinData?>(null) }
//                批量管理模式开关
                var batchManageMode by remember { mutableStateOf(false) }
//                存储选中的待办affId
                val selectedTodoIds = remember { mutableStateListOf<Long>() }
//                新增标签弹窗
                var openAddCateDialog by remember { mutableStateOf(false) }
//                弹窗状态
                var showExpireDialog by remember { mutableStateOf(false) }
                var currentExpireTodo by remember { mutableStateOf<TodoJoinData?>(null) }
                var isSearchFocused by remember { mutableStateOf(false) }
//                全局共享焦点
                val searchFocusRequester = remember { FocusRequester() }
                val keyboardController = LocalSoftwareKeyboardController.current
                var isDarkMode by remember { mutableStateOf(false) }
//                待办数据列表
                val scope = rememberCoroutineScope()
                //弹窗控制标记
                var openAddDialog by remember { mutableStateOf(false) }
                val lifecycleOwner = LocalLifecycleOwner.current
//                筛选搜索
                val currentSearch by mainVm.searchKeyword.collectAsStateWithLifecycle(
                    initialValue = "",
                    lifecycle = lifecycleOwner.lifecycle
                )
                val currentCateFilter by mainVm.filterCategory.collectAsStateWithLifecycle(
                    initialValue = "全部分类",
                    lifecycle = lifecycleOwner.lifecycle
                )
                val currentPrioFilter by mainVm.filterPriority.collectAsStateWithLifecycle(
                    initialValue = "全部等级",
                    lifecycle = lifecycleOwner.lifecycle
                )
                val filterTodoList by mainVm.filteredTodoListFlow.collectAsStateWithLifecycle(
                    initialValue = emptyList(),
                    lifecycle = lifecycleOwner.lifecycle
                )
                val allCategory by mainVm.categoryFlow.collectAsStateWithLifecycle(
                    initialValue = emptyList(),
                    lifecycle = lifecycleOwner.lifecycle
                )
                val allPriority by mainVm.priorityFlow.collectAsStateWithLifecycle(
                    initialValue = emptyList(),
                    lifecycle = lifecycleOwner.lifecycle
                )

                // 实时检测过期未完成待办
                //LaunchedEffect:在 Composable 函数的生命周期内安全地启动和管理协程
                LaunchedEffect(filterTodoList) {
                    while (true) {
                        val nowTime = System.currentTimeMillis()
                        if (showExpireDialog) {
                            delay(1000)
                            continue
                        }
                        val expireTodo = filterTodoList.firstOrNull { todo ->
                            // 条件：截止时间 < 当前时间 + 未完成 + 未提醒过
                            todo.endTime < nowTime
                                    && todo.isExpired == 0
                                    && todo.isFinish == 0
                                    && todo.hasReminded == 0
                        }
                        if (expireTodo != null) {
                            currentExpireTodo = expireTodo
                            showExpireDialog = true
                            mainVm.setTodoReminded(expireTodo.affId)
                        }
                        delay(1000)
                    }
                }

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
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
//                            消除点击波纹
                            indication = null,
                            interactionSource = null
                        ) {
                            keyboardController?.hide()
                            scope.launch {
                                delay(80)
                                searchFocusRequester.freeFocus()
                                onWindowFocusChanged(false)
                            }
                        },
                    color = pageBg
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopNavigationBar(
                            searchText = currentSearch,
                            onSearchChange = { inputText ->
                                mainVm.updateSearchKeyword(inputText)
                            },
                            textColor = textPrimary,
                            searchFocusRequester = searchFocusRequester,
                            onFocusChange = { newState ->
                                isSearchFocused = newState
                            },
                            isSearchFocused = isSearchFocused,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            LeftSideBar(
                                modifier = Modifier.width(220.dp),
                                bgColor = sideBarBg,
                                textColor = textPrimary,
                                mainColor = mainColor,
                                dividerColor = dividerColor,
                                onAddTagClick = { openAddCateDialog = true },
                                onBatchClick = {
                                    batchManageMode = !batchManageMode
                                    selectedTodoIds.clear()
                                }
                            )
                            Column {
                                FilterBar(
                                    textColor = textPrimary,
                                    mainColor = mainColor,
                                    dividerColor = dividerColor,
                                    categoryList = allCategory,
                                    priorityList = allPriority,
                                    onCategorySelect = { cateName ->
                                        mainVm.selectFilterCategory(cateName)
                                    },
                                    onPrioritySelect = { prioName ->
                                        mainVm.selectFilterPriority(prioName)
                                    },
                                    currentSelectCategory = currentCateFilter,
                                    currentSelectPriority = currentPrioFilter,
                                    batchDeleteMode = batchManageMode,
                                    onCateDeleteClick = { cate ->
                                        mainVm.checkAndDeleteCategory(cate.cataId) {
                                            showCateWarnDialog = true
                                        }
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
//                                    传入数据库真实数据
                                    NoteListArea(
                                        cardBg = contentCardBg,
                                        textColor = textPrimary,
                                        mainColor = mainColor,
                                        todoList = filterTodoList,
                                        selectStroke = selectStrokeColor,
                                        onRequestDelete = { targetTodoId ->
                                            mainVm.requestDeleteTodo(targetTodoId)
                                        },
//                                        pendingDeleteId = pendingDeleteId,
                                        onMarkComplete = { targetTodoId ->
                                            mainVm.finishTodoItem(targetTodoId)
                                        },
                                        // 拖拽排序回调：新顺序列表
                                        onOrderChanged = { newSortList ->
                                            scope.launch(Dispatchers.IO) {
                                                // 按拖拽后的顺序重新赋值sortOrder
                                                val updateData =
                                                    newSortList.mapIndexed { index, joinData ->
                                                        val originTodo =
                                                            mainVm.getTodoEntity(joinData.affId)
                                                        originTodo?.copy(sortOrder = index)
                                                    }.filterNotNull()
                                                mainVm.updateTodoSort(updateData)
                                            }
                                        },
                                        batchMode = batchManageMode,
                                        selectedIds = selectedTodoIds,
                                        onToggleSelect = { todoId ->
                                            if (selectedTodoIds.contains(todoId)) {
                                                selectedTodoIds.remove(todoId)
                                            } else {
                                                selectedTodoIds.add(todoId)
                                            }
                                        },
                                        onClickTodoItem = { todo ->
                                            targetDetailTodo = todo
                                            showDetailDialog = true
                                        }
                                    )
//                                    新建笔记按钮
                                    FloatingActionButton(
                                        onClick = {
                                            openAddDialog = true
                                        },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 75.dp, bottom = 75.dp),
                                        shape = CircleShape,
                                        containerColor = mainColor
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.add),
                                            contentDescription = "新建事务",
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
                            mainColor = mainColor,
                            batchMode = batchManageMode,
                            selectedCount = selectedTodoIds.size,
                            onBatchDelete = {
                                // 先复制一份选中ID快照，避免过程中集合变化
                                val idsToDelete = selectedTodoIds.toList()
                                mainVm.batchDeleteTodo(idsToDelete) {
                                    batchManageMode = false
                                    selectedTodoIds.clear()
                                }
                            }
                        )
                    }
                    //挂载截止时间提醒弹窗
                    // 过期提醒顶部弹窗
                    if (showExpireDialog && currentExpireTodo != null) {
                        TodoExpireTopToast(
                            expireTodo = currentExpireTodo!!,
                            onDismiss = {
                                showExpireDialog = false
                                currentExpireTodo = null
                            }
                        )
                    }
                }
                // 事务详情弹窗
                if (showDetailDialog && targetDetailTodo != null) {
                    val currentTodo = targetDetailTodo
                    TodoDetailDialog(
                        todo = currentTodo!!,
                        textColor = textPrimary,
                        bgCardColor = contentCardBg,
                        categoryList = allCategory,
                        priorityList = allPriority,
                        onUpdateTodo = { title, content, cateId, prioId, endTime ->
                            scope.launch(Dispatchers.IO) {
                                val originTodo =
                                    mainVm.getTodoEntity(currentTodo.affId) ?: return@launch
                                val nowTime = System.currentTimeMillis()
                                val newIsExpiredInt = if (endTime < nowTime) 1 else 0
                                val safeCateId = cateId ?: originTodo.categoryId
                                val safePrioId = prioId ?: originTodo.priorityId
                                val updatedEntity = originTodo.copy(
                                    title = title,
                                    detail = content,
                                    categoryId = safeCateId,
                                    priorityId = safePrioId,
                                    endTime = endTime,
                                    isExpired = newIsExpiredInt,
                                    isFinish = 0,         // 强制置为未完成，取消文字划线
                                    hasReminded = 0       // 重置提醒，到期再次弹窗
                                )
                                mainVm.updateTodoEntity(updatedEntity)
//                                主线程关闭弹窗
                                showDetailDialog = false
                                targetDetailTodo = null
                            }
                        },
                        onClose = {
                            showDetailDialog = false
                            targetDetailTodo = null
                        }
                    )
                }

                //挂载新增事务弹窗
                AddTodoDialog(
                    show = openAddDialog,
                    textColor = textPrimary,
                    categoryList = allCategory,//分类数据
                    priorityList = allPriority,
                    bgCardColor = contentCardBg, //跟随页面卡片底色
                    closeDialog = { openAddDialog = false },
                    saveClick = { titleInput, content, cateId, prioId, fullEndTimeMs ->
                        //IO线程插入数据库
                        scope.launch(Dispatchers.IO) {
                            val now = System.currentTimeMillis()
                            //标题固定，内容填输入文字，截止时间=当天+选定时分，分类/优先级暂时null
                            val maxSort = mainVm.getMaxSortNum() ?: 0
                            val newTodo = TodoAffair(
                                title = titleInput,
                                detail = content,
                                startTime = now,
                                endTime = fullEndTimeMs,
                                categoryId = cateId,
                                priorityId = prioId,
                                hasReminded = 0,
                                sortOrder = maxSort + 1
                            )
                            mainVm.addNewTodo(newTodo)
                        }
                    }
                )
                // 新增自定义分类弹窗
                AddCategoryDialog(
                    show = openAddCateDialog,
                    textColor = textPrimary,
                    bgCardColor = contentCardBg,
                    closeDialog = { openAddCateDialog = false },
                    saveNewCategory = { labelName ->
                        mainVm.createCategory(labelName)
                    }
                )
                //                // 分类存在待办时的提醒弹窗
                if (showCateWarnDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showCateWarnDialog = false },
                        title = { Text("无法删除") },
                        text = { Text("该分类下仍有待办事务，请先删除相关事务后再操作！") },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = {
                                showCateWarnDialog = false
                            }) {
                                Text("确定")
                            }
                        }
                    )
                }
                //                分类存在待办时的提醒弹窗
                if (pendingDeleteId!=null) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { mainVm.cancleDeleTodo() },//点击背景或返回键取消
                        title = { Text("确认删除") },
                        text = { Text("确认删除该条待办事项吗？此操作无法撤销。") },
                        dismissButton = {
                            androidx.compose.material3.TextButton(onClick = {
                                mainVm.cancleDeleTodo()
                            }) {
                                Text("取消")
                            }
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = {
                                mainVm.confirmDeleteTodo()
                            }) {
                                Text("确定")
                            }
                        }
                    )
                }
            }
        }
    }
}
