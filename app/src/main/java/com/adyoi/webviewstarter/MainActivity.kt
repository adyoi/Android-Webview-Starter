package com.adyoi.webviewstarter

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.webkit.*
import android.widget.Toast

@Suppress("OverridingDeprecatedMember")
class MainActivity : AppCompatActivity() {

    private var doubleClick = false

    override fun onBackPressed() {
        val myWebView: WebView = findViewById(R.id.webview)
        when {
            myWebView.canGoBack() -> myWebView.goBack()
            doubleClick -> finish()
            else -> {
                Toast.makeText(this, "please click back again to exit", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    doubleClick = true
                }, 200)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Webview Starter v 1.2
        // Created by Adi Apriyanto
        // Tangerang Selatan, March 2018
        // Permission to copy source code is only permitted for Education

        // Sample Resource Page
        val myUrl = "file:///android_asset/html/index.html"
        val errorUrl = "file:///android_asset/html/error.html"

        // Your Live Page
        // val myUrl = "https://www.domain.com"

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.domStorageEnabled = true
        myWebView.settings.allowContentAccess = true
        myWebView.addJavascriptInterface(
            WebInterface(this, myWebView),
            "Android"
        )
        myWebView.webChromeClient = WebChromeClient()
        myWebView.webViewClient = object : WebViewClient() {
            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                when {
                    url!!.startsWith("tel:") -> {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                    url.startsWith("mailto:") -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                    url.startsWith("whatsapp://") -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.setPackage("com.whatsapp")
                        startActivity(intent)
                        return true
                    }
                    url.contains("https://adyoi.blogspot.com") -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                    else -> return super.shouldOverrideUrlLoading(view, url)
                }
            }
        }
        when {
            isNetworkAvailable() -> myWebView.loadUrl(myUrl)
            else -> myWebView.loadUrl(errorUrl)
        }
        myWebView.reload()
    }

    private inner class WebInterface(val mContext: Context, val mView: WebView) {
        // From Webview to Android
        @JavascriptInterface
        fun showToast(Text: String) {
            Toast.makeText((applicationContext), Text, Toast.LENGTH_SHORT).show()
        }
        @JavascriptInterface
        fun showNotification(id: Int, title: String, text: String) {
            createNotificationChannel(id, title, text)
        }

        // From Android to Webview
        @JavascriptInterface
        fun sendText(Text: String) {
            val myWebView: WebView = findViewById(R.id.webview)
            var myStamp = "Hello :) - "
            myWebView.post{myWebView.loadUrl("javascript:showText(\"$myStamp$Text\")")}
        }
    }

    private fun createNotificationChannel(notificationId: Int, notificationTitle: String, notificationText: String) {
        val channelId = "ANDROID_WEBVIEW_STARTER"
        val channelName = "Notification Default"
        val channelDescription = ""
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                channel.description = channelDescription
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
        val notificationManager = NotificationManagerCompat.from(this)
        val builder = NotificationCompat
            .Builder(this, channelId)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher_legacy))
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            notificationManager.notify(notificationId, builder.build())
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
