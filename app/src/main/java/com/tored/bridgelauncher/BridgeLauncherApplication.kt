package com.tored.bridgelauncher

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.services.BridgeServiceProvider
import com.tored.bridgelauncher.services.PermsManager
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.ISettingsStateProvider
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.services.system.BridgeLauncherBroadcastReceiver
import com.tored.bridgelauncher.utils.checkCanSetSystemNightMode
import kotlinx.coroutines.MainScope

class BridgeLauncherApplication : Application()
{
    lateinit var serviceProvider: BridgeServiceProvider

    lateinit var consoleMessagesHolder: ConsoleMessagesHolder

    var hasStoragePerms by mutableStateOf(false)

    override fun onCreate()
    {
        super.onCreate()

        // this is deliberately set up like this so that whenver a service is added to the provider, the constructor below will complain
        // constructing the services ahead of time helps with manually resolving the dependency graph at compile time
        // yeah this probably could be done by some DI library but I'd rather explicitly know what is happening

        val singletonCoroutineScope = MainScope()

        val pm = packageManager
        val storagePermsManager = PermsManager(this)
        val settingsVM = SettingsVM(this)
        val settingsStateProvider: ISettingsStateProvider = settingsVM
        val installedAppsHolder = InstalledAppsHolder(pm)
        val installedIconPacksHolder = InstalledIconPacksHolder(pm)
        val bridgeToJSAPI = BridgeToJSAPI(this, singletonCoroutineScope, settingsStateProvider, installedAppsHolder, checkCanSetSystemNightMode())
        val jsToBridgeAPI = JSToBridgeAPI(this, singletonCoroutineScope, settingsStateProvider, null)
        val bridgeServer = BridgeServer(
            settingsStateProvider,
            installedAppsHolder,
            installedIconPacksHolder,
        )
        val consoleMessagesHolder = ConsoleMessagesHolder()
        val broadcastReceiver = BridgeLauncherBroadcastReceiver(installedAppsHolder)
        ContextCompat.registerReceiver(
            this,
            broadcastReceiver,
            BridgeLauncherBroadcastReceiver.intentFilter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        serviceProvider = BridgeServiceProvider(
            packageManager = pm,
            settingsVM = settingsVM,
            storagePermsManager = storagePermsManager,

            bridgeServer = bridgeServer,
            installedAppsHolder = installedAppsHolder,
            installedIconPacksHolder = installedIconPacksHolder,
            consoleMessagesHolder = consoleMessagesHolder,
            broadcastReceiver = broadcastReceiver,
            bridgeToJSAPI = bridgeToJSAPI,
            jsToBridgeAPI = jsToBridgeAPI,
        )
    }
}