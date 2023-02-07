package com.example.aankh.utils

import android.view.View


object Extension {

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.GONE
    }


}