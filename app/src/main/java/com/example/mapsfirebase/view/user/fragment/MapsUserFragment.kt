package com.example.mapsfirebase.view.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mapsfirebase.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_maps_user.*

class MapsUserFragment : Fragment() {
    private lateinit var nav: NavController
    private var reference: DatabaseReference? = null
    private var db: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps_user, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance()
        reference = db?.getReference("maps")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nav = Navigation.findNavController(view)

        initButton()
    }

    private fun initButton() {
        mapsUser_start.setOnClickListener {
            nav.navigate(R.id.action_mapsUserFragment_to_mapsUserActivity)
        }
    }
}