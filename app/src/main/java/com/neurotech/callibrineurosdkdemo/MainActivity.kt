package com.neurotech.callibrineurosdkdemo

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.databinding.ActivityMainBinding
import com.neurotech.callibrineurosdkdemo.databinding.LoginLayoutBinding
import com.neurotech.callibrineurosdkdemo.databinding.RegistrationLayoutBinding
import com.neurotech.callibrineurosdkdemo.databinding.UsersListBinding
import com.neurotech.callibrineurosdkdemo.storage.HistoryStorage

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var loginBinding: LoginLayoutBinding
    private lateinit var registrationBinding: RegistrationLayoutBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLoginScreen()
    }

    private fun showLoginScreen() {
        loginBinding = LoginLayoutBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.buttonLogin.setOnClickListener {
            val email = loginBinding.editTextLogin.text.toString().trim()
            val password = loginBinding.editTextPassword.text.toString().trim()
            val name = loginBinding.editTextName.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authenticateUser(name, email, password)
        }

        loginBinding.buttonRegister.setOnClickListener {
            showRegistrationScreen()
        }

        loginBinding.buttonIncognito.setOnClickListener {
            showMainApp()
        }

        loginBinding.buttonShowUsers.setOnClickListener {
            showUsersList()
        }
    }

    private fun showRegistrationScreen() {
        registrationBinding = RegistrationLayoutBinding.inflate(layoutInflater)
        setContentView(registrationBinding.root)

        registrationBinding.buttonRegisterComplete.setOnClickListener {
            val name = registrationBinding.editTextFirstName.text.toString().trim()
            val email = registrationBinding.editTextEmail.text.toString().trim()
            val password = registrationBinding.editTextPassword.text.toString().trim()
            val confirmPassword = registrationBinding.editTextConfirmPassword.text.toString().trim()

            if (validateRegistrationFields(name, email, password, confirmPassword)) {
                registerUser(name, email, password)
            }
        }

        registrationBinding.buttonBack.setOnClickListener {
            showLoginScreen()
        }
    }

    private fun validateRegistrationFields(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 4) {
            Toast.makeText(this, "Пароль должен содержать минимум 4 символа", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(name: String, email: String, password: String) {
        val url = "http://95.174.104.223:8099/api/v1/auth/register"
        val json = JSONObject()
        json.put("name", name)
        json.put("email", email)
        json.put("password", password)

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Ошибка регистрации: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        showLoginScreen()
                    } else {
                        Toast.makeText(this@MainActivity, "Ошибка регистрации: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
    private fun authenticateUser(name: String, email: String, password: String) {
        val url = "http://95.174.104.223:8099/api/v1/auth/authenticate"
        val json = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", password)
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Log.e("authentication", "Ошибка входа: ${e.message}")
                    Toast.makeText(this@MainActivity, "Ошибка входа: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val bodyStr = response.body?.string()
                    try {
                        val json = JSONObject(bodyStr ?: "{}")
                        val accessToken = json.optString("accessToken", "")
                        val refreshToken = json.optString("refreshToken", "")

                        if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                            HistoryStorage.setTokens(accessToken, refreshToken) // ✅ теперь оба токена

                            runOnUiThread {
                                Log.e("authentication", "Вход выполнен успешно!}")
                                Toast.makeText(this@MainActivity, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()
                                showMainApp()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Ошибка: токены не получены", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Ошибка разбора ответа", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun showUsersList() {
        val binding = UsersListBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Зарегистрированные пользователи")
            .setView(binding.root)
            .setPositiveButton("Закрыть", null)
            .create()

        binding.usersRecyclerView.adapter = UserAdapter(emptyList())
        binding.usersRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        dialog.show()
    }

    private fun showMainApp() {
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        initMainApp()
    }

    private fun initMainApp() {
        DynamicColors.applyToActivityIfAvailable(this)

        setSupportActionBar(mainBinding.toolbar)

        val color = SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor = color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.navigationBarColor = getColor(R.color.colorDevState)
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        CallibriController.connectionStateChanged = {
            runOnUiThread {
                if (it == com.neurosdk2.neuro.types.SensorState.StateOutOfRange) {
                    mainBinding.txtDevBatteryPower.text = getString(R.string.dev_power_prc, 0)
                    navController.popBackStack(R.id.MenuFragment, false)
                }
            }
        }

        CallibriController.batteryChanged = {
            runOnUiThread {
                mainBinding.txtDevBatteryPower.text = getString(R.string.dev_power_prc, it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    fun performAuthorizedGetRequest(url: String) {
        val accessToken = HistoryStorage.getAccessToken() // ⚡️ возьми токен из своего хранилища
        if (accessToken.isNullOrEmpty()) {
            runOnUiThread {
                Toast.makeText(this, "Нет accessToken", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Ошибка GET: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: "Пустой ответ"
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Успех: $body", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Ошибка: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}
