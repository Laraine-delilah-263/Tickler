package com.example.todolist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entity.TodoAffair
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoAffairDao {
    @Query("SELECT * FROM todo_affair ORDER BY startTime DESC")
    fun getAllTodoList(): Flow<List<TodoAffair>>

    @Insert
    suspend fun insertTodo(todo: TodoAffair)

    @Query("DELETE FROM todo_affair WHERE affId = :id")
    suspend fun deleteTodo(id: Long)
}