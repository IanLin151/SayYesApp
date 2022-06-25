package org.altbeacon.beaconreference

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.altbeacon.beacon.*

import org.altbeacon.bluetooth.BluetoothMedic
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.HashMap

class BeaconReferenceApplication: Application() {
    lateinit var region: Region


    override fun onCreate() {
        super.onCreate()

        val beaconManager = BeaconManager.getInstanceForApplication(this)
        BeaconManager.setDebug(true)

        // 默認情況下，AndroidBeaconLibrary 只會找到 AltBeacons。如果你想實現
        // 查找不同類型的信標，您必須指定該信標的字節佈局
        // 帶有如下行的廣告。該示例顯示瞭如何使用
        // 與 AltBeacon 相同的字節佈局，但 beaconTypeCode 為 0xaabb。去尋找合適的
        // 其他信標類型的佈局表達式，在網絡上搜索“setBeaconLayout”
        // 包括引號。
        //
        //beaconManager.getBeaconParsers().clear();
        //beaconManager.getBeaconParsers().add(new BeaconParser().
        // setBeaconLayout("m:0-1=4c00,i:2-24v,p:24-24"));


        // 默認情況下，AndroidBeaconLibrary 只會找到 AltBeacons。如果你想實現
        // 找到不同類型的信標，如 Eddystone 或 iBeacon，您必須指定字節佈局
        // 對於該信標的廣告，如下所示。
        //
        // 如果你不關心 AltBeacon，你可以從默認值中清除它：
        beaconManager.getBeaconParsers().clear()

        // 這個例子展示瞭如何找到 iBeacon。
        beaconManager.getBeaconParsers().add(
            BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))

        // 啟用調試會將大量詳細的調試信息從庫發送到 Logcat
        // 這對於解決問題很有用
        // BeaconManager.setDebug(true)


        // 此處的 BluetoothMedic 代碼（如果包含）將監視藍牙問題
        // 堆棧和可選：
        // - 重啟藍牙以恢復藍牙問題
        // - 定期進行主動掃描或傳輸以驗證藍牙堆棧是否正常
        // BluetoothMedic.getInstance().enablePowerCycleOnFailures(this)
        // BluetoothMedic.getInstance().enablePeriodicTests(this, BluetoothMedic.SCAN_TEST + BluetoothMedic.TRANSMIT_TEST)

        // 默認情況下，該庫在 Android 4-7 上每 5 分鐘在後台掃描一次，
        // 這將僅限於在 Android 8+ 上每隔約 15 分鐘掃描一次計劃的作業
        // 如果您想要更頻繁的掃描（需要 Android 8+ 上的前台服務），
        // 在這裡配置。
        // 如果您想在後台連續定位信標，而不是每 15 分鐘一次，
        // 您可以使用庫的內置前台服務在 Android 上解鎖此行為
        // 8+。下面的方法顯示了您如何設置它。
        setupForegroundService()
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);

        // 如果沒有檢測到信標，測距回調將退出
        // 區域退出時監控回調將延遲最多 25 分鐘
        // beaconManager.setIntentScanningStrategyEnabled(true)

        // 下面的代碼將開始“監控”與下面區域定義匹配的信標
        // 區域定義是一個通配符，它匹配所有信標，而不考慮標識符。
        // 如果您只想檢測具有特定 UUID 的信標，請將 id1 參數更改為
        // 像 Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6") 這樣的 UUID
        region = Region("radius-uuid", null, null, null)
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
        // 這兩行設置了一個 Live Data 觀察者，因此這個 Activity 可以從 Application 類中獲取信標數據
        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)
        // 每次監控的 regionState 發生變化時都會調用觀察者（區域內與區域外）
        regionViewModel.regionState.observeForever( centralMonitoringObserver)
        // 每次確定一個新的信標列表時都會調用觀察者（通常在前台約 1 秒）
        regionViewModel.rangedBeacons.observeForever( centralRangingObserver)
    }

    fun setupForegroundService() {
        val builder = Notification.Builder(this, "BeaconReferenceApp")
        builder.setSmallIcon(R.drawable.app)
        builder.setContentTitle("背景掃描")
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(pendingIntent);
        val channel =  NotificationChannel("beacon-ref-notification-id",
            "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT)
        channel.setDescription("My Notification Channel Description")
        val notificationManager =  getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());
        BeaconManager.getInstanceForApplication(this).enableForegroundServiceScanning(builder.build(), 456);
    }

    val centralMonitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.OUTSIDE) {
            Log.d(TAG, "outside beacon region: "+region)

        }
        else {
            Log.d(TAG, "inside beacon region: "+region)
            sendNotification()
            sendNotification2()
            sendNotification3()
        }
    }

    val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(SetActivity.TAG, "Ranged: ${beacons.count()} beacons")
        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
        }
    }







    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
            .setContentTitle("!!豪大大雞排買一送一!!")
            .setContentText("憑此推播可以享有優惠")
            .setSmallIcon(R.drawable.app)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(Intent(this, FoodActivity::class.java))
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)
        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())


    }
    private fun sendNotification2() {
        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
            .setContentTitle("!!士林炒羊肉買炒羊肉送白飯!!")
            .setContentText("憑此推播可以享有優惠")
            .setSmallIcon(R.drawable.app)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(Intent(this, FoodActivity::class.java))
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)
        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, builder.build())
    }

    private fun sendNotification3() {
        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
            .setContentTitle("!!好朋友涼麵買涼麵送味增湯!!")
            .setContentText("憑此推播可以享有優惠")
            .setSmallIcon(R.drawable.app)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(Intent(this, FoodActivity::class.java))
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)
        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(3, builder.build())
    }

    companion object {
        val TAG = "BeaconReference"
    }

}