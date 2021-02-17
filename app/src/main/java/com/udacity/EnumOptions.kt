package com.udacity

import androidx.annotation.StringRes

enum class EnumOptions(
        val option: Int,
        val url: String,
        @StringRes val title: Int
) {
    GLIDE(1, "https://github.com/bumptech/glide/archive/master.zip", R.string.txt_glide_title),
    UDACITY(2, "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip", R.string.txt_udacity_title),
    RETROFIT(3, "https://github.com/square/retrofit/archive/master.zip", R.string.txt_retrofit_title)
}