package com.neurotech.callibrineurosdkdemo.storage

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object HistoryStorage {
    private const val FILE_NAME = "emotions_history.jsonl"
    private lateinit var ctx: Context
    private var lastSaveTime = 0L
    private val client = OkHttpClient()

    // токены
    private var accessToken: String? = null
    private var refreshToken: String? = null

    // автообновление accessToken
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var refreshTask: ScheduledFuture<*>? = null

    @RequiresApi(Build.VERSION_CODES.N)
    fun init(context: Context) {
        ctx = context.applicationContext
        observeNetwork()
    }

    data class LoggedEntry(
        val ts: Long,
        val instRelaxation: Double
    )

    @SuppressLint("StaticFieldLeak")
    fun saveEntry(e: LoggedEntry) {
        Log.i("HistoryStorage", "Saving data")
        val now = System.currentTimeMillis()
        if (now - lastSaveTime < 1000) return
        lastSaveTime = now

        try {
            val f = File(ctx.filesDir, FILE_NAME)
            val jo = JSONObject().apply {
                put("ts", e.ts)
                put("instRelaxation", formatDouble(e.instRelaxation))
            }
            f.appendText(jo.toString() + "\n")
            pruneOldEntries(f)
        } catch (_: Exception) {}
    }

    private fun pruneOldEntries(file: File) {
        try {
            val cutoff = System.currentTimeMillis() - 60 * 60 * 1000L
            if (!file.exists()) return
            val kept = ArrayList<String>()
            for (ln in file.readLines()) {
                if (ln.isBlank()) continue
                try {
                    val jo = JSONObject(ln)
                    val ts = jo.optLong("ts", 0L)
                    if (ts >= cutoff) kept.add(ln)
                } catch (_: Exception) {}
            }
            file.writeText(if (kept.isEmpty()) "" else kept.joinToString("\n") + "\n")
        } catch (_: Exception) {}
    }

    fun readLastHourEntries(): List<LoggedEntry> {
        val out = ArrayList<LoggedEntry>()
        try {
            val file = File(ctx.filesDir, FILE_NAME)
            if (!file.exists()) return out
            for (ln in file.readLines()) {
                if (ln.isBlank()) continue
                try {
                    val jo = JSONObject(ln)
                    val e = LoggedEntry(
                        jo.optLong("ts", 0L),
                        jo.optDouble("instRelaxation", 0.0)
                    )
                    out.add(e)
                } catch (_: Exception) {}
            }
            out.sortByDescending { it.ts }
        } catch (_: Exception) {}
        return out
    }

    fun setTokens(access: String, refresh: String) {
        accessToken = access
        refreshToken = refresh
        scheduleTokenRefresh()
        trySendPendingData()
    }

    private fun scheduleTokenRefresh() {
        refreshTask?.cancel(true)
        refreshTask = scheduler.scheduleAtFixedRate({
            refreshAccessToken()
        }, 55, 55, TimeUnit.MINUTES) // обновляем за 5 мин до истечения
    }

    private fun refreshAccessToken() {
        val ref = refreshToken ?: return
        val url = "http://95.174.104.223:8099/api/v1/auth/refresh"
        val body = JSONObject().apply {
            put("refreshToken", ref)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HistoryStorage", "Refresh token failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val res = response.body?.string()
                    try {
                        val json = JSONObject(res ?: "{}")
                        accessToken = json.optString("accessToken", null)
                        Log.i("HistoryStorage", "Token refreshed")
                    } catch (_: Exception) {}
                } else {
                    Log.e("HistoryStorage", "Refresh failed: ${response.code}")
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeNetwork() {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySendPendingData()
            }
        })
    }

    private fun trySendPendingData() {
        if (accessToken.isNullOrBlank()) return
        val entries = readLastHourEntries()
        if (entries.isEmpty()) return

        val listPoints = JSONArray()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (e in entries) {
            val point = JSONObject()
            point.put("time", sdf.format(Date(e.ts)))
            point.put("value", formatDouble(e.instRelaxation))
            listPoints.put(point)
        }

        val sensorObj = JSONObject()
        sensorObj.put("sensorType", "per")
        sensorObj.put("listPoint", listPoints)

        val outer = JSONObject()
        outer.put("list", JSONArray().put(sensorObj))

        val url = "http://95.174.104.223:8099/api/v1/client/data"
        val body = outer.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Client $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HistoryStorage", "Failed to send data: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("HistoryStorage", "Data sent successfully")
                        //File(ctx.filesDir, FILE_NAME).writeText("")
                } else {
                    Log.e("HistoryStorage", "Error sending data: ${response.code} ${response.message}")
                }
            }
        })
    }

    private fun formatDouble(v: Double): String {
        return if (v % 1.0 == 0.0) String.format(Locale.US, "%.1f", v) else String.format(Locale.US, "%s", v)
    }
    fun getAccessToken(): String? {
        return accessToken
    }


}
