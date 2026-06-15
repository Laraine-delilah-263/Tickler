package com.example.todolist.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.focus.onFocusChanged


//头部列表
@Composable
fun TopNavigationBar(
    searchText: String,
    onSearchChange: (String) -> Unit,
    textColor: Color,
    searchFocusRequester: FocusRequester,
    globalKeyboardController: SoftwareKeyboardController?,
    globalScope: CoroutineScope,
    onFocusChange: (Boolean) -> Unit,
    isSearchFocused: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "应用图标",
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "车载记事本",
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )
        }
//原生自带边框线，不用text额外设置
        Box(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)){
            OutlinedTextField(
                value = searchText,//输入框实时显示的文本
                onValueChange = onSearchChange,//用户输入、删除字符触发回调，把新文本赋值给searchText
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .focusRequester(searchFocusRequester)//绑定焦点控制器
                    .onFocusChanged{ state ->
                        onFocusChange(state.isFocused)
                    },
                placeholder = {
                    Text(
                        "搜索标题/正文...",
                        color = textColor.copy(alpha = 0.6f), //文字透明度60%
                        maxLines = 1
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "搜索",
                        modifier = Modifier.size(24.dp),
//                    根据字体颜色设置
                        colorFilter = ColorFilter.tint(textColor.copy(alpha = 0.6f))
                    )
                },
//            清空图标
                trailingIcon = {
                    if (searchText.isNotBlank()){
                        Image(
                            painter = painterResource(id = R.drawable.cancle),
                            contentDescription = "清空搜索框",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable{
                                    onSearchChange("")
                                    globalKeyboardController?.hide()
                                    globalScope.launch {
                                        delay(80)
                                        searchFocusRequester.freeFocus()
                                    }
                                },
                            colorFilter = ColorFilter.tint(textColor.copy(0.6f))
                        )
                    }
                },
                singleLine = true,//输入控制
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = textColor.copy(alpha = 0.55f), //获取焦点(点击输入)边框色75%透明度
                    unfocusedBorderColor = textColor.copy(alpha = 0.35f), //失焦(未点击)边框色35%透明度
                    focusedContainerColor = Color.Transparent, //选中时输入框背景：完全透明
                    unfocusedContainerColor = Color.Transparent //未选中输入框背景：完全透明
                )
            )
            if(!isSearchFocused){
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(2.dp)
                        .height(24.dp)
                        .background(Color.Transparent)
                )
            }
        }

//        搜索按钮
        Button(
            onClick = {
                globalKeyboardController?.hide()
                globalScope.launch {
                    delay(80)//等待键盘收起动画完成
                    searchFocusRequester.freeFocus()//释放输入框焦点
                }
            },
            modifier = Modifier
                .width(120.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
//            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text(text="搜索", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(2.dp))

    }
}