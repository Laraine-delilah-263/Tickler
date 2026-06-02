package com.example.todolist.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAllCategory(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(category: Category)
}