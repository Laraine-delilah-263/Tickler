package com.example.todolist.model.repository

import com.example.todolist.model.dao.CategoryDao
import com.example.todolist.model.dao.TodoAffairDao
import com.example.todolist.model.entity.TodoAffair
import kotlinx.coroutines.flow.Flow

class TodoRepository(
    private val todoDao: TodoAffairDao,
) {
    // 对外暴露Flow，供ViewModel转发
    fun observeTodoAll(): Flow<List<com.example.todolist.model.dao.TodoJoinData>> =
        todoDao.queryTodoJoinAll()

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
                priorityId = 4,
                hasReminded = 0
            )
            todoDao.insertTodo(todo)
        }
    }

    //    新增待办入库
    suspend fun insertNewTodo(todo: TodoAffair) {
        todoDao.insertTodo(todo)
    }

    //    获取最大排序号
    suspend fun getMaxSortOrder(): Int? {
        return todoDao.getMaxSortOrder()
    }

    //    根据id删除单条待办
    suspend fun deleteSingleTodoById(affId: Long) {
        todoDao.deleteTodoById(affId)
    }

    //    标记待办为已完成
    suspend fun markTodoFinished(affId: Long) {
        todoDao.markTodoFinish(affId)
    }

    //    新增完整待办实体
    suspend fun updateTodoItem(todo: TodoAffair) {
        todoDao.updateTodo(todo)
    }

    //    根据单条id查询原事务内容返回编辑列表
    suspend fun getTodoEntityById(affId: Long?): TodoAffair? {
        if (affId == null) return null
        return todoDao.getTodoById(affId)
    }

    //    批量删除事务
    suspend fun batchDeleteTodo(ids: List<Long>) {
        todoDao.batchDeleteTodo(ids)
    }

    //    批量更新待办排序
    suspend fun batchUpdateTodoSort(todoList: List<TodoAffair>) {
        todoDao.batchUpdateTodo(todoList)
    }

    //    根据id查询单条原始待办（组装排序实体）
    suspend fun getTodoById(affId: Long): TodoAffair? {
        return todoDao.getTodoById(affId)
    }

    //     将待办标记为已弹出过期提醒
    suspend fun markTodoReminded(affId: Long) {
        todoDao.markTodoReminded(affId)
    }

    //分类标签校验


}