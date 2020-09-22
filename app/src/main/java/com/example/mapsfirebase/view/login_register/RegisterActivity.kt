package com.example.mapsfirebase.view.login_register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapsfirebase.R
import com.example.mapsfirebase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var reference: DatabaseReference? = null
    private var db: FirebaseDatabase? = null

    private val TAG = "Register Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initView()
    }

    private fun initView() {
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        reference = db!!.reference!!.child("user")

        register_back.setOnClickListener {
            onBackPressed()
        }

        btn_register.setOnClickListener {

            val nama = register_nama.text.toString()
            val email = register_email.text.toString()
            val hp = register_hp.text.toString()
            val job = register_job.text.toString()
            val alamat = register_alamat.text.toString()
            val password = register_password.text.toString()

            if (nama.isEmpty()) {
                register_nama.error = "Nama tidak boleh kosong"
            } else if (email.isEmpty()) {
                register_email.error = "Email tidak boleh kosong"
            } else if (hp.isEmpty()) {
                register_hp.error = "No HP tidak boleh kosong"
            } else if (job.isEmpty()) {
                register_job.error = "Pekerjaan tidak boleh kosong"
            } else if (alamat.isEmpty()) {
                register_alamat.error = "Alamat tidak boleh kosong"
            } else if (password.isEmpty()) {
                register_password.error = "Password tidak boleh kosong"
            } else if (password.length < 6) {
                register_password.error = "Password harus lebih dari 6"
            } else {
                actionRegister(nama, email, hp, job, alamat, password)
            }
        }
    }

    private fun actionRegister(
        nama: String, email: String, hp: String,
        job: String, alamat: String, password: String) {

        auth!!.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val userId = auth!!.currentUser!!.uid
                    //Verify Email
                    verifyEmail();
                    //update user profile information
                    val currentUserDb = reference!!.child(userId)

                    var status = "status"
                    if (register_admin.isChecked) {
                        status = "admin"
                        val user = User(status, nama, email, hp, job, alamat, password)
                        currentUserDb.setValue(user)
                        intentAdmin()
                    } else {
                        status = "user"
                        val user = User(status, nama, email, hp, job, alamat, password)
                        currentUserDb.setValue(user)
                        intentUser()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun intentAdmin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun intentUser() {
        val intent = Intent(this@RegisterActivity, MapsRegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = auth!!.currentUser;
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Verification email sent to " + mUser.getEmail(), Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(applicationContext, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}