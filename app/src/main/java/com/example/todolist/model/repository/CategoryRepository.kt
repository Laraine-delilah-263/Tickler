package com.example.todolist.model.repository

import com.example.todolist.model.dao.CategoryDao
import com.example.todolist.model.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    fun observeAllCategory(): Flow<List<Category>> = categoryDao.getAllCategory()

//    分类表初始化方法
    suspend fun initDefaultCategory() {
        if (categoryDao.getCategoryList().isEmpty()) {
            categoryDao.insertCategory(Category(label = "日常事务"))
        }
    }

//    新增分类业务方法
    suspend fun createNewCategory(label: String) {
    categoryDao.insertCategory(Category(label = label))
}

//    根据id删除分类
    suspend fun deleteCategoryById(cataId: Long) {
        categoryDao.deleteCategory(cataId)
    }

//    查询该分类下待办数量（调用Dao跨表统计）
    suspend fun getTodoCountByCataId(cataId: Long): Int {
        return categoryDao.countTodoByCategory(cataId)
    }

}