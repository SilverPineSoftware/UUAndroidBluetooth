package com.silverpine.uu.sample.bluetooth.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.ux.UUAppCompatActivity

abstract class RecyclerActivity: UUAppCompatActivity(layoutResourceId = R.layout.activity_recycler)
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        setupAdapter(recyclerView)
    }

    abstract fun setupAdapter(recyclerView: RecyclerView)
}