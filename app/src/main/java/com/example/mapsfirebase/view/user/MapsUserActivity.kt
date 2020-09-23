package com.example.mapsfirebase.view.user

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mapsfirebase.R
import com.example.mapsfirebase.model.Maps
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps_user.*

class MapsUserActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var reference: DatabaseReference? = null
    private var referenceReg: DatabaseReference? = null
    private var db: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_user)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_user) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PackageManager.PERMISSION_GRANTED
        )

        initView()
    }

    private fun initView() {
        db = FirebaseDatabase.getInstance()
        reference = db?.reference?.child("maps")
        referenceReg = db?.reference?.child("maps_register")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        dataMaps()
        initButton()
    }

    private fun dataMaps() {

        val dataMaps = ArrayList<Maps>()

        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (datas in snapshot.children) {
                    val key = datas.key

                    val nama = datas.child("nama").value.toString()
                    val nama2 = datas.child("nama2").value.toString()
                    val lat = datas.child("lat").value.toString()
                    val lon = datas.child("lon").value.toString()

                    val maps = Maps(nama, nama2, lat, lon, key)
                    dataMaps.add(maps)

                    val marker = LatLng(lat.toDouble(), lon.toDouble())
                    mMap.addMarker(MarkerOptions().position(marker).title("Marker in $nama"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 16f))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        //setting zoom in/out
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
    }

    private fun getData() {
        referenceReg?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val namereg = snapshot.child("register_nama").value.toString()
                val name2reg = snapshot.child("register_nama2").value.toString()
                val latreg = snapshot.child("register_lat").value.toString()
                val lonreg = snapshot.child("register_lon").value.toString()

                val youPosition = LatLng(latreg.toDouble(), lonreg.toDouble())
                mMap.addMarker(MarkerOptions().position(youPosition).title("Your Location in $namereg")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))

                val nama = convertCoordinat(latreg.toDouble(), lonreg.toDouble())
                val nama2 = convertCoordinat2(latreg.toDouble(), lonreg.toDouble())
                user_name.text = "$latreg - $lonreg"
                user_kordinat.text = nama
                user_kordinat2.text = nama2
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun initButton() {
        user_btnHybrid.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        user_btnSatelit.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        user_btnTerrain.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
        user_btnNormal.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        user_marker.setOnClickListener {
            getData()
        }
    }

    private fun convertCoordinat(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this)
        val dataLocation = geocoder.getFromLocation(lat, lon, 1)
        val nameLocation = dataLocation.get(0).featureName

        return nameLocation
    }

    private fun convertCoordinat2(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this)
        val dataLocation = geocoder.getFromLocation(lat, lon, 1)
        val nameLocation = dataLocation.get(0).locality

        return nameLocation
    }
}