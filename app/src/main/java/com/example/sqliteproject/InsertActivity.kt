package com.example.sqliteproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sqliteproject.databinding.ActivityInsertBinding
import com.google.android.material.snackbar.Snackbar

class InsertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)

        binding.saveButton.setOnClickListener{

            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty()){
                val value= db.insert(name,email)

                if(value== (-1).toLong()){
                    Snackbar.make(binding.insertLayout,"Insert failed! ", Snackbar.LENGTH_SHORT).show()
                }
                else{
                    Snackbar.make(binding.insertLayout,"Inserted Successfully", Snackbar.LENGTH_SHORT).show()
                }
                binding.nameEditText.text.clear()
                binding.emailEditText.text.clear()
            }
            else{
                Snackbar.make(binding.insertLayout,"Please Insert Data! ", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.viewButton.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        binding.callButton.setOnClickListener {
            val number= binding.phoneEditText.text.toString().trim()
            if(number.isNotEmpty()){
                val intent= Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                startActivity(intent)
            }
            else{
                Snackbar.make(binding.insertLayout,"Please enter phone",Snackbar.LENGTH_SHORT).show()

            }
        }

        binding.youtubeButton.setOnClickListener {
            startActivity(Intent(this,YoutubeActivity:: class.java))
        }

    }
}