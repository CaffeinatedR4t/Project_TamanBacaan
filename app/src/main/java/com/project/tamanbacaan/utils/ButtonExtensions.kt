package com.caffeinatedr4t.tamanbacaan.utils

import android.view.animation.AnimationUtils
import android.widget.Button
import com.caffeinatedr4t.tamanbacaan.R

fun Button.animateClick() {
    val scale = AnimationUtils.loadAnimation(context, R.anim.button_scale)
    startAnimation(scale)
}