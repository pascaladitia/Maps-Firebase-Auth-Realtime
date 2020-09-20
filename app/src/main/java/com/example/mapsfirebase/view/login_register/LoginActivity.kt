package com.example.mapsfirebase.view.login_register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapsfirebase.R
import com.example.mapsfirebase.view.admin.AdminActivity
import com.example.mapsfirebase.view.user.UserActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var reference: DatabaseReference? = null
    private var db: FirebaseDatabase? = null
    private var client: GoogleSignInClient? = null

    private val TAG = "Login Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        initButton()
        initGmail()
    }

    private fun initView() {
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        reference = db!!.reference!!.child("user")
    }

    private fun initGmail() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        client = GoogleSignIn.getClient(this, gso)
    }

    private fun initButton() {
        login_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btn_login.setOnClickListener {
            val email = login_email.text.toString()
            val password = login_password.text.toString()

            if (email.isEmpty()) {
                login_email.error = "Email tidak boleh kosong"
            } else if (password.isEmpty()) {
                login_password.error = "Password tidak boleh kosong"
            } else {
                actionLogin(email, password)
            }
        }

        login_google.setOnClickListener {
            val signIn = client?.signInIntent
            startActivityForResult(signIn, 1)
        }
    }

    private fun actionLogin(email: String, password: String) {

        Log.d(TAG, "Logging in user.")
        auth!!.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun updateUI() {
        val mUser = auth!!.currentUser
        val mUserReference = reference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.child("status").value.toString()

                if (login_admin.isChecked) {
                    if (status.equals("admin")) {
                        startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                    } else {
                        showToast("Anda bukan admin")
                    }
                } else {
                    if (status.equals("user")) {
                        startActivity(Intent(this@LoginActivity, UserActivity::class.java))
                    } else {
                        showToast("Anda bukan user")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast(databaseError.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            handleSignInResult(task)
            firebaseAuthWithGoogle(account.idToken ?: "")
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            //    updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("TAG", "signInResult:failed code=" + e.statusCode)
            // updateUI(null)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth?.currentUser
                    if (login_admin.isChecked) {
                        startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, UserActivity::class.java))
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    // ...
                    Toast.makeText(applicationContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
}