package com.hopeinyang.zeroknowledge.ui.screens.ManageUsers

import com.hopeinyang.zeroknowledge.data.dto.UserInfo

data class ManageUserUiState(

    val department:String = "",
    val role:String = "",
    val accessLevel:Int = 0,
    val isAdmin:Boolean = false,
    val userList:List<UserInfo> = emptyList(),
    val selectedUserEmail:String = "",
    val isUserEmailExpanded:Boolean = false,
    val userId:String ="",
    val departmentList:List<String> = emptyList(),
    val isDeptExpanded:Boolean = false,
    val isRoleExpanded:Boolean = false,
    val isAdminUser:Boolean = false,
    val latitude:String = "",
    val longitude:String = "",

    )
