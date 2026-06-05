package com.example.todolist.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entity.Category
import kotlinx.coroutines.flow.Flow


////分类数据访问对象，对数据库的各项操作进行封装
@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAllCategory(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(category: Category)
}