package com.example.tilegame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tilegame.databinding.ActivityWelcomeAcitvityBinding

class WelcomeAcitvity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeAcitvityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeAcitvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun openDialogBox(view: View) {
        // Create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Your Name??")

        // Inflate the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.custom_alert, null)
        builder.setView(customLayout)

        // Add a button
        builder.setPositiveButton("Begin") { dialog, which ->
            // Send data from the AlertDialog to the Activity
            val editText = customLayout.findViewById<EditText>(R.id.editText)
            sendDialogDataToActivity(editText.text.toString())
        }

        // Create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun sendDialogDataToActivity(data: String) {
        if(data.isNotEmpty())
        {
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
            val sp = getSharedPreferences("userData",Context.MODE_PRIVATE)
            val temp = sp.edit()
            temp.putString("name",data)
            temp.apply()
        }

        val intent = Intent(this,GameActivity2::class.java)
        startActivity(intent)
        finish()
    }
}
