package com.silverpine.uu.sample.bluetooth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.sample.bluetooth.BR
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.viewmodel.UUPeripheralViewModel

class PeripheralRowAdapter(private val context: Context, private val onClick: ((UUPeripheral)->Unit)): RecyclerView.Adapter<PeripheralRowAdapter.ViewHolder>()
{
    private val tableData: ArrayList<UUPeripheralViewModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.peripheral_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = getItem(position)
        item?.let()
        {
            holder.bind(it)
        }
    }

    override fun onViewRecycled(holder: ViewHolder)
    {
        holder.recycle()
    }

    override fun getItemCount(): Int
    {
        synchronized(tableData)
        {
            return tableData.size
        }
    }

    fun update(list: List<UUPeripheral>)
    {
        synchronized(tableData)
        {
            tableData.clear()

            for (obj in list)
            {
                val vm = UUPeripheralViewModel(obj, context)
                vm.onClick = this.onClick
                tableData.add(vm)
            }
        }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): ViewModel?
    {
        synchronized(tableData)
        {
            if (position >= 0 && position < tableData.size)
            {
                return tableData[position]
            }
        }

        return null
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        private val binding: ViewDataBinding? = DataBindingUtil.bind(itemView)

        fun bind(model: Any)
        {
            binding?.setVariable(BR.vm, model)
            binding?.executePendingBindings()
        }

        fun recycle()
        {
            binding?.unbind()
        }
    }
}