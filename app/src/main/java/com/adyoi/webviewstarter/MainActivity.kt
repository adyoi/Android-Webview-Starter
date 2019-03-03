package com.adyoi.webviewstarter

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.webkit.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onBackPressed() {
        val myWebView: WebView = findViewById(R.id.webview)
        if (myWebView.canGoBack()) {
            myWebView.goBack()
        } else {
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                    finish()
                })
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Webview Starter v 1.0
        // Created by Adi Apriyanto
        // Tangerang Selatan, March 2018
        // Permission to copy source code is only permitted for Education

        // Sample Resource Page
        val myUrl = "file:///android_asset/html/index.html"

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
        myWebView.loadUrl(myUrl)
        myWebView.reload()
    }

    private inner class WebInterface(val mContext: Context, val mView: WebView) {

        // From Webview to Android
        @JavascriptInterface
        fun showToast(Text: String) {
            Toast.makeText((applicationContext), Text, Toast.LENGTH_SHORT).show()
        }

        // From Android to Webview
        @JavascriptInterface
        fun sendText(Text: String) {
            val myWebView: WebView = findViewById(R.id.webview)
            var stamp = "Hello :) - "
            myWebView.post{myWebView.loadUrl("javascript:showText(\"$stamp$Text\")")}
        }
    }
}
