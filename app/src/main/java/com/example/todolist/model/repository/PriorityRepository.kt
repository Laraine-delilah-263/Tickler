package com.example.todolist.model.repository

import com.example.todolist.model.dao.PriorityDao
import com.example.todolist.model.entity.Priority
import kotlinx.coroutines.flow.Flow

class PriorityRepository(private val priorityDao: PriorityDao) {
    fun observeAllPriority(): Flow<List<Priority>> = priorityDao.getAllPriority()
    fun getAllPriorityFlow(): Flow<List<Priority>> = priorityDao.getAllPriority()

    // 优先级表初始化方法
    suspend fun initDefaultPriority() {
        if (priorityDao.getPriorityList().isEmpty()) {
            priorityDao.insertPriority(Priority(levelName = "常规"))
            priorityDao.insertPriority(Priority(levelName = "暂缓"))
            priorityDao.insertPriority(Priority(levelName = "重要"))
            priorityDao.insertPriority(Priority(levelName = "紧急"))
        }
    }
}