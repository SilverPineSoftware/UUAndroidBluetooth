package com.silverpine.uu.sample.bluetooth.ui

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.ux.UUAppCompatActivity
import com.silverpine.uu.ux.UUViewModelRecyclerAdapter

abstract class RecyclerActivity: UUAppCompatActivity(layoutResourceId = R.layout.activity_recycler)
{
    protected lateinit var adapter: UUViewModelRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UUViewModelRecyclerAdapter(this::handleRowTapped)
        recyclerView.adapter = adapter
        setupAdapter(recyclerView)
    }

    abstract fun setupAdapter(recyclerView: RecyclerView)

    open fun handleRowTapped(viewModel: ViewModel)
    {

    }
}