package com.noureddinetaleb.polyhome.activities

import android.content.Intent
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.RegisterData
import com.noureddinetaleb.polyhome.storage.TokenStorage

/**
 * Register activity
 */
class RegisterActivity : AppCompatActivity() {

    /**
     * Handle register request
     */
    private fun register()
    {
        val login = findViewById<EditText>(R.id.txtRegisterName).text.toString()
        val password = findViewById<EditText>(R.id.txtRegisterPassword).text.toString()
        val registerData = RegisterData(login, password)
        Api().post("https://polyhome.lesmoulinsdudev.com/api/users/register", registerData, ::registerSuccess, intent.getStringExtra("TOKEN"))
    }

    /**
     * Handle register response
     */
    private fun registerSuccess(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Le compte a bien été créé", Toast.LENGTH_SHORT).show()
            }
            else if(responseCode == 409){
                Toast.makeText(this, " Le login est déjà utilisé par un autre compte", Toast.LENGTH_SHORT).show()
            }
            else if (responseCode == 500)
                Toast.makeText(this, "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
            else{
                Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * Create the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            register()
        }
    }

    /**
     * Go to login activity
     */
    public fun goToLogin(view: View)
    {
        finish();
    }
}