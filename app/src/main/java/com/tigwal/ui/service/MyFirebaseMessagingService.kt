package com.tigwal.ui.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.ui.activity.DashboardActivity
import com.tigwal.ui.activity.SupportChatActivity
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "MyFirebaseMsgService"
    var mNotificationManager: NotificationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "onMessageReceived:noti-> " + Gson().toJson(remoteMessage.notification))
        Log.e(TAG, "onMessageReceived:data-> " + Gson().toJson(remoteMessage.data))
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.e(TAG, "onMessageReceived: remoteMessage -> " + remoteMessage)

        if (remoteMessage != null) {
            GenerateNotification_Static(remoteMessage)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun GenerateNotification_Static(remoteMessage: RemoteMessage) {
        val random = 101
        var body: String? = ""
        var title: String? = ""
        var sender_id: String? = ""
        var type: String? = ""
        if (remoteMessage.data != null && remoteMessage.data.size > 0) {
            // Local BroadCast
            body = remoteMessage.getData()!!.get("body");
            title = remoteMessage.getData()!!.get("title");




            if (remoteMessage.getData().get("data") != null && !remoteMessage.getData().get("data")
                    .equals("")
            ) {
                val jsonObject = JSONObject(remoteMessage.getData().get("data"))
                if (jsonObject.getString("sender_id") != null) {
                    sender_id = jsonObject.getString("sender_id");
                }
            } else {
                sender_id = remoteMessage.getData().get("sender_id");
            }

            type = remoteMessage.getData().get("type");
            val intent = Intent("NOTIFICATION_RECEIVER_FIRE_HOME")
            val channel_id = "notification_channel"
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.setAction(RestConstant.FIREBASE_PUSH_NOTIFICATION)
            intent.putExtra("type", type)
            intent.putExtra("title", title)
            intent.putExtra("sender_id", sender_id)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            val i =
                Intent(applicationContext, SupportChatActivity.CustomBroadcastReceiver::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setAction(RestConstant.FIREBASE_PUSH_NOTIFICATION)
            i.putExtra("type", type)
            i.putExtra("sender_id", sender_id)
            i.putExtra("title", title)
            sendBroadcast(i)


            // Pending Intent
            val notificationIntent = Intent(this, DashboardActivity::class.java)
            notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            notificationIntent.setAction(RestConstant.FIREBASE_PUSH_NOTIFICATION)
            notificationIntent.putExtra("type", type)
            notificationIntent.putExtra("sender_id", sender_id)
            notificationIntent.putExtra("title", title)
            LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            var builder: NotificationCompat.Builder = NotificationCompat.Builder(
                applicationContext,
                channel_id
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(
                    longArrayOf(
                        1000, 1000, 1000,
                        1000, 1000
                    )
                )
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
            builder = builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
            val name: CharSequence = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            var mChannel: NotificationChannel? = null
            if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O
            ) {
                mChannel = NotificationChannel(channel_id, name, importance)
                mNotificationManager!!.createNotificationChannel(
                    mChannel
                )
            }
            mNotificationManager!!.notify(random, builder.build())
        } else {
            // Local BroadCast
            body = remoteMessage.notification!!.body;
            title = remoteMessage.notification!!.title;
            val intent = Intent("NOTIFICATION_RECEIVER_FIRE_HOME")
            val channel_id = "notification_channel"
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.setAction(RestConstant.FIREBASE_PUSH_NOTIFICATION)
            intent.putExtra("type", type)
            intent.putExtra("title", title)
            intent.putExtra("sender_id", sender_id)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            val i =
                Intent(applicationContext, SupportChatActivity.CustomBroadcastReceiver::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setAction(RestConstant.FIREBASE_PUSH_NOTIFICATION)
            i.putExtra("type", type)
            i.putExtra("title", title)
            i.putExtra("sender_id", sender_id)
            sendBroadcast(i)

            // Pending Intent
            val notificationIntent = Intent(this, DashboardActivity::class.java)
            notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            notificationIntent.setAction(RestConstant.FIREBASE_PUSH_NOTIFICATION)
            notificationIntent.putExtra("type", type)
            notificationIntent.putExtra("title", title)
            notificationIntent.putExtra("sender_id", sender_id)
            LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            var builder: NotificationCompat.Builder = NotificationCompat.Builder(
                applicationContext,
                channel_id
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(
                    longArrayOf(
                        1000, 1000, 1000,
                        1000, 1000
                    )
                )
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
            builder = builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
            val name: CharSequence = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            var mChannel: NotificationChannel? = null
            if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O
            ) {
                mChannel = NotificationChannel(channel_id, name, importance)
                mNotificationManager!!.createNotificationChannel(
                    mChannel
                )
            }
            mNotificationManager!!.notify(random, builder.build())
        }
    }

    @SuppressLint("LongLogTag")
    override fun onNewToken(firebaseId: String) {
        Log.e("firebase service==", "" + firebaseId)
    }
}
