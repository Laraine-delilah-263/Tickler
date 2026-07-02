package com.example.todolist.model.Enum

import androidx.compose.ui.graphics.Color
import com.example.todolist.ui.theme.*

//优先级中文枚举
enum class PriorityEnum(
    val dbLabel:String,
    val color: Color
) {
    URGENT("紧急", priUrgent),
    IMPORTANT(dbLabel = "重要",PriImportant),
    NORMAL(dbLabel = "常规",PriNormal),
    DELAY(dbLabel = "暂缓",PriDelay);

    //和从数据库传来的数据做匹配，拿到对应的枚举去做事务列表的优先级颜色渲染
    companion object{
        fun getByDbLabel(label: String?): PriorityEnum?{
            return values().firstOrNull{
                it.dbLabel==label
            }
        }
        //拿到所有优先级枚举，用于初始化数据库时创建默认等级
        fun getAllPriority(): List<PriorityEnum> =values().toList()

    }
}