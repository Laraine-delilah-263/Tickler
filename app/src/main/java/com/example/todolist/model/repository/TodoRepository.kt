package com.example.todolist.model.repository

import com.example.todolist.model.dao.CategoryDao
import com.example.todolist.model.dao.PriorityDao
import com.example.todolist.model.dao.TodoAffairDao
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import com.example.todolist.model.entity.TodoAffair
import kotlinx.coroutines.flow.Flow

class TodoRepository(
    private val todoDao: TodoAffairDao
) {
    // 对外暴露Flow，供ViewModel转发（原有数据流不变）
    fun observeTodoAll(): Flow<List<com.example.todolist.model.dao.TodoJoinData>> = todoDao.queryTodoJoinAll()
    // 原MainActivity的数据库初始化逻辑完整迁移
    suspend fun initDefaultTableData() {
        // 3. 初始化默认待办
        val todoList = todoDao.getAllTodo()
        if (todoList.isEmpty()) {
            val now = System.currentTimeMillis()
            val endTime = now + 24 * 3600_000
            val todo = TodoAffair(
                title = "初始待办",
                detail = "系统默认第一条待办事项",
                startTime = now,
                endTime = endTime,
                categoryId = 1,
                priorityId = 1,
                hasReminded = 0
            )
            todoDao.insertTodo(todo)
        }
    }
}