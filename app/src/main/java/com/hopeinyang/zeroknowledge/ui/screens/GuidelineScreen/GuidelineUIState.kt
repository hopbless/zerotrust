package com.hopeinyang.zeroknowledge.ui.screens.GuidelineScreen

import com.hopeinyang.zeroknowledge.data.dto.ArticleContent

data class GuidelineUIState(
    val title:String = "",
    val docList:List<ArticleContent> = emptyList(),
    val selectedDocTitle:String = "",
    val selectedDoc:ArticleContent = ArticleContent(),
    val isExpanded:Boolean = false,
    val userId:String = "",
    val role:String = ""

)
