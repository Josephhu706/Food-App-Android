package com.example.foody.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkListener: ConnectivityManager.NetworkCallback() {
    private val isNetworkAvailable = MutableStateFlow(false)

    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean>{
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(this) //this refers to the whole NetworkListener class

        var isConnected = false
        //check if the device has internet connection and set isNetworkAvailable stateFlow to true if we do
        connectivityManager.allNetworks.forEach {network ->
            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            networkCapability?.let {
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    isConnected = true
                    return@forEach
                }
            }
        }
        isNetworkAvailable.value = isConnected
        return isNetworkAvailable
    }

    //triggers when network is available
    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    //triggers when network is lost
    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}