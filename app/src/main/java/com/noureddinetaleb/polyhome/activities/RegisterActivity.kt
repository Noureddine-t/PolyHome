package com.noureddinetaleb.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.RegisterData

/**
 * Register activity handling register requests.
 */
class RegisterActivity : AppCompatActivity() {

    /**
     * Handle register request.
     *
     * @see RegisterData Class handling register data.
     * @see registerSuccess Function handling register response.
     * @see Api class handling API requests.
     */
    private fun register() {
        val login = findViewById<EditText>(R.id.txtRegisterName).text.toString()
        val password = findViewById<EditText>(R.id.txtRegisterPassword).text.toString()
        val registerData = RegisterData(login, password)
        Api().post("https://polyhome.lesmoulinsdudev.com/api/users/register", registerData, ::registerSuccess, intent.getStringExtra("TOKEN"))
    }

    /**
     * Handle register response
     *
     * @param responseCode The response code.
     */
    private fun registerSuccess(responseCode: Int) {
        runOnUiThread {
            when (responseCode) {
                200 -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Le compte a bien été créé", Toast.LENGTH_SHORT).show()
                }

                409 -> Toast.makeText(this, "Le login est déjà utilisé par un autre compte", Toast.LENGTH_SHORT).show()
                500 -> Toast.makeText(this, "R: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                else -> {
                    Toast.makeText(this, "R: Erreur lors de l'inscription", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            register()
        }
    }

    /**
     * Redirect to login page on click.
     */
    fun goToLogin(view: View) {
        finish();
    }
}