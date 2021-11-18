package com.ym.learnkotlin.wifip2p


import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater

import android.view.ViewGroup

import android.net.wifi.p2p.WifiP2pDevice
import android.view.View
import com.ym.learnkotlin.R
import com.ym.learnkotlin.wifip2p.util.WifiP2pUtil


class DeviceAdapter(private val wifiP2pDeviceList: List<WifiP2pDevice>) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    private var clickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        view.setOnClickListener { v ->
            if (clickListener != null) {
                clickListener!!.onItemClick(v.tag as Int)
            }
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDeviceName.text = wifiP2pDeviceList[position].deviceName
        holder.tvDeviceAddress.text = wifiP2pDeviceList[position].deviceAddress
        holder.tvDeviceDetails.text = WifiP2pUtil.getDeviceStatus(wifiP2pDeviceList[position].status)
        holder.itemView.tag = position
    }

    override fun getItemCount(): Int {
        return wifiP2pDeviceList.size
    }

    fun setClickListener(clickListener: OnClickListener) {
        this.clickListener = clickListener
    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDeviceName: TextView = itemView.findViewById(R.id.tv_deviceName)
        val tvDeviceAddress: TextView = itemView.findViewById(R.id.tv_deviceAddress)
        val tvDeviceDetails: TextView = itemView.findViewById(R.id.tv_deviceDetails)
    }
}
