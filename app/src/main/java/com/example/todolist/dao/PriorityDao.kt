package com.example.todolist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entity.Priority
import kotlinx.coroutines.flow.Flow

@Dao
interface PriorityDao {
    @Query("SELECT * FROM priority")
    fun getAllPriority(): Flow<List<Priority>>

    @Insert
    suspend fun insertPriority(priority: Priority)
}