package com.example.mapsfirebase.view.user.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import com.example.mapsfirebase.R
import com.example.mapsfirebase.view.login_register.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.custom_dialog_profil.*
import kotlinx.android.synthetic.main.fragment_home_user.*

class HomeUserFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null
    private var referenceReg: DatabaseReference? = null
    private var client: GoogleSignInClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_user, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        referenceReg = db?.reference?.child("maps_register")

        initGmail()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButton()
    }

    private fun initGmail() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        client = context?.let { GoogleSignIn.getClient(it, gso) }
    }

    private fun initButton() {
        user_logout.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Hapus")
                setMessage("Yakin ingin menghapus Marker?")
                setCancelable(false)

                setPositiveButton("Ya") {dialog, which ->
                    client?.signOut()
                    referenceReg?.removeValue()
                    logout()
                }
                setNegativeButton("Batal") {dialog, which ->
                    dialog?.dismiss()
                }
            }.show()
        }

        homeUser_profil.setOnClickListener {
            context?.let { it1 ->
                Dialog(it1).apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(false)
                    setContentView(R.layout.custom_dialog_profil)

                    val mUser = auth!!.currentUser
                    val emailUser = mUser?.email
                    val namaUser = mUser?.displayName

                    dialog_nama.text = "Nama : $namaUser"
                    dialog_email.text = "Email : $emailUser"

                    dialog_btnClose.setOnClickListener {
                        this.dismiss()
                    }
                }.show()
            }
        }
    }

    private fun logout() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}