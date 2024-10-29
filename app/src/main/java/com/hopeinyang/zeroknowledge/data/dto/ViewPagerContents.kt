package com.hopeinyang.zeroknowledge.data.dto

import android.net.Uri

data class ViewPagerContents(
    val pageName:String="",
    val pageIndex:Int=0,
    val pageIconUrl:String ="",
    val title:String="",
    val subTitle:String="",
    val homePageCardTitles:HashMap<String,String> = hashMapOf(),
    val cardSubTitle:HashMap<String, String> = hashMapOf(),
    val cardDetail:HashMap<String, String> = hashMapOf(),
    val cardIcons:HashMap<String,String> = hashMapOf()
    )
