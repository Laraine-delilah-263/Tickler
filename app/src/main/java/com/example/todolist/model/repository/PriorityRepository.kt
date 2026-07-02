package com.example.todolist.model.repository

import com.example.todolist.model.Enum.PriorityEnum
import com.example.todolist.model.dao.PriorityDao
import com.example.todolist.model.entity.Priority
import kotlinx.coroutines.flow.Flow

class PriorityRepository(private val priorityDao: PriorityDao) {
    fun observeAllPriority(): Flow<List<Priority>> = priorityDao.getAllPriority()

    // 优先级表初始化方法
    suspend fun initDefaultPriority() {
        if (priorityDao.getPriorityList().isEmpty()) {
            val defaultPriorityList= PriorityEnum.getAllPriority().map { enumItem->
                Priority(levelName = enumItem.dbLabel)
            }
            priorityDao.insertAll(defaultPriorityList)
        }
    }
}