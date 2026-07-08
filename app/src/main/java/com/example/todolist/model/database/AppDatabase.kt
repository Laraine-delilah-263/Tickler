package com.example.todolist.model.database

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.model.dao.CategoryDao
import com.example.todolist.model.dao.PriorityDao
import com.example.todolist.model.dao.TodoAffairDao
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import com.example.todolist.model.entity.TodoAffair

//Database用于定义数据库中的关键信息，包括数据库版本号，包含实体类和提供Dao层的访问实例
@Database(
    entities = [TodoAffair::class, Category::class, Priority::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoAffairDao
    abstract fun categoryDao(): CategoryDao
    abstract fun priorityDao(): PriorityDao

    companion object {
        //    数据库实例
        private var db: AppDatabase? = null

        // 迁移：版本1 → 版本2，新增hasReminded字段，默认值0
        private val MIGRATION_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 给 todo_affair 表新增 hasReminded 整数字段，非空默认0
                database.execSQL(
                    "ALTER TABLE todo_affair ADD COLUMN hasReminded INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        // 版本2→3：新增sortOrder排序字段，默认值0
        private val MIGRATION_2_TO_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE todo_affair ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        @Synchronized
//        Synchronized线程安全注解。它保证了 getDatabase 这个方法同一时间只能被一个线程调用。
//        防止多线程环境下两个线程同时发现 db 是空的，各自创建新的数据库实例，破坏单例的唯一性。
        fun getDatabase(context: Context): AppDatabase {
            db?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "todo_database"
            )
//                .fallbackToDestructiveMigration()//没有配置迁移就销毁旧表重建
                //                允许在主线程中进行数据库操作
                .addMigrations(MIGRATION_1_TO_2, MIGRATION_2_TO_3)//注册全部迁移
                .allowMainThreadQueries()
                .build().apply {
                    db = this
                }
        }
    }
}
