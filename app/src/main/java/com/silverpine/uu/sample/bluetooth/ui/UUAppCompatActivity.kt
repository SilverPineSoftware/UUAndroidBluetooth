package com.silverpine.uu.sample.bluetooth.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.ux.UUMenuHandler

open class UUAppCompatActivity(@StringRes val titleResourceId: Int = -1, @LayoutRes val layoutResourceId: Int = -1) : AppCompatActivity()
{
    private var menuHandler: UUMenuHandler? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (layoutResourceId != -1)
        {
            setContentView(layoutResourceId)
        }

        if (titleResourceId != -1)
        {
            setTitle(titleResourceId)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean
    {
        menu?.let()
        {
            it.clear()

            val handler = UUMenuHandler(it)
            populateMenu(handler)
            menuHandler = handler
            return true
        }

        return super.onPrepareOptionsMenu(menu)
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        val handler = UUMenuHandler(menu)
        populateMenu(handler)
        menuHandler = handler
        return true
    }*/

    open fun populateMenu(menuHandler: UUMenuHandler)
    {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        menuHandler?.let()
        {
            return it.handleMenuClick(item)
        }
        ?: run()
        {
                return false
        }
    }
}



fun Context.uuShowToast(text: String)
{
    UUThread.runOnMainThread()
    {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}


