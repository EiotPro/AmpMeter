package com.example.ampmeter.presentation.ui.logs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ampmeter.R
import com.example.ampmeter.domain.model.DeviceReading
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying device readings in a RecyclerView.
 */
class DeviceReadingsAdapter(
    private val onItemClick: (DeviceReading) -> Unit
) : ListAdapter<DeviceReading, DeviceReadingsAdapter.ViewHolder>(DeviceReadingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_reading, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reading = getItem(position)
        holder.bind(reading)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTimestamp: TextView = itemView.findViewById(R.id.text_timestamp)
        private val textCurrentValue: TextView = itemView.findViewById(R.id.text_current_value)
        private val textVoltageValue: TextView = itemView.findViewById(R.id.text_voltage_value)
        private val textBatteryValue: TextView = itemView.findViewById(R.id.text_battery_value)
        private val iconSyncStatus: View = itemView.findViewById(R.id.icon_sync_status)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(reading: DeviceReading) {
            // Format timestamp
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date(reading.timestamp))
            textTimestamp.text = dateStr

            // Set values
            textCurrentValue.text = String.format("%.1f A", reading.current)
            textVoltageValue.text = String.format("%.1f V", reading.voltage)
            textBatteryValue.text = String.format("%d%%", reading.batteryLevel)

            // Show sync status icon if not synced
            iconSyncStatus.visibility = if (reading.isSynced) View.GONE else View.VISIBLE
        }
    }
}

/**
 * DiffUtil callback for DeviceReading items.
 */
class DeviceReadingDiffCallback : DiffUtil.ItemCallback<DeviceReading>() {
    override fun areItemsTheSame(oldItem: DeviceReading, newItem: DeviceReading): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DeviceReading, newItem: DeviceReading): Boolean {
        return oldItem == newItem
    }
} 