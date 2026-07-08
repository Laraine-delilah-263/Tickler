package com.example.todolist.model.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.model.entity.Category
import kotlinx.coroutines.flow.Flow


//分类数据访问对象，对数据库的各项操作进行封装
@Dao
interface CategoryDao {

    //在联查表查询指定分类下是否存在事务（删除分类标签前置检查）
    @Query("SELECT COUNT(*) FROM todo_affair WHERE categoryId = :cateId")
    suspend fun countTodoByCategory(cateId: Long): Int

    //按照指定id删除分类标签（批量删除按钮进入）
    @Query("DELETE FROM category WHERE cataId = :cateId")
    suspend fun deleteCategory(cateId: Long)

    // 将指定待办标记为已提醒
    @Query("UPDATE todo_affair SET hasReminded = 1 WHERE affId = :todoId")
    suspend fun markTodoReminded(todoId: Long)

    //    挂起查询，查询全部分类
    @Query("SELECT * FROM category")
    suspend fun getCategoryList(): List<Category>

    //    插入分类标签数据
    @Insert
    suspend fun insertCategory(category: Category): Long

    //    流式查询全部数据
    @Query("SELECT * FROM category")
    fun getAllCategory(): Flow<List<Category>>

    //    更新数据
    @Update
    suspend fun updataCategory(newCategory: Category)

}
