package com.example.mapsfirebase.view.login_register

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mapsfirebase.R
import com.example.mapsfirebase.model.MapsUserRegister
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_maps_register.*

class MapsRegisterActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var reference: DatabaseReference? = null
    private var db: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_register)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_register) as SupportMapFragment
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
        reference = db?.reference?.child("maps_register")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        initButton()
        dataMaps()
    }

    private fun dataMaps() {
        // Add a marker in Sydney and move the camera
        val kampung = LatLng(-6.8148909, 106.6771815)
        mMap.addMarker(MarkerOptions().position(kampung).title("Marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kampung))

        //setting zoom
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kampung, 16f))

        //setting zoom in/out
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
    }

    private fun initButton() {
        btn_hybridRegister.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        btn_satelitRegister.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        btn_terrainRegister.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
        btn_normalRegister.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        //add to realtime firebase
        mMap.setOnMapClickListener {
            val lat = it.latitude
            val lon = it.longitude

            mMap.clear()

            val nama = convertCoordinat(lat, lon)
            val nama2 = convertCoordinat2(lat, lon)
            mapsRegister_name.text = "$lat - $lon"
            mapsRegister_kordinat.text = nama
            mapsRegister_kordinat2.text = nama2

            mMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Marker in $nama"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))

            mapsRegister_save.setOnClickListener {
                saveData(nama, nama2, lat, lon)
            }
        }
    }

    private fun saveData(nama: String, nama2: String, lat: Double, lon: Double) {
        AlertDialog.Builder(this).apply {
            setTitle("Simpan")
            setMessage("Yakin ingin menyimpan Marker?")
            setCancelable(false)

            setPositiveButton("Ya") { dialog, which ->
                val maps = MapsUserRegister(nama, nama2, lat.toString(), lon.toString())
                reference?.setValue(maps)
                intentUI()
            }
            setNegativeButton("Batal") { dialog, which ->
                dialog?.dismiss()
            }
        }.show()
    }

    private fun intentUI() {
        val intent = Intent(this@MapsRegisterActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
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