package com.example.mapsfirebase.view.admin

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var reference: DatabaseReference? = null
    private var db: FirebaseDatabase? = null
    private var item: Maps? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PackageManager.PERMISSION_GRANTED
        )

        initView()
        getParcel()
    }

    private fun getParcel() {
        item = intent.getParcelableExtra<Maps>("data")
    }

    private fun initView() {
        db = FirebaseDatabase.getInstance()
        reference = db?.reference?.child("maps")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        dataMaps()
        initButton()
    }

    private fun dataMaps() {
        var name = item?.nama
        var lat = item?.lat?.toDouble()
        var lon = item?.lon?.toDouble()

        if (item != null) {
            val update = LatLng(lat!!, lon!!)
            mMap.addMarker(MarkerOptions().position(update).title("Marker in $name")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(update))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(update, 16f))

            val nama = convertCoordinat(lat, lon)
            val nama2 = convertCoordinat2(lat, lon)
            maps_name.text = "$lat - $lon"
            maps_kordinat.text = nama
            maps_kordinat2.text = nama2
            maps_switch.text = "Update ke Database?"
        } else {
            // Add a marker in Sydney and move the camera
            val kampung = LatLng(-6.8148909, 106.6771815)
            mMap.addMarker(MarkerOptions().position(kampung).title("Marker"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(kampung))

            //setting zoom
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kampung, 16f))
        }

        //setting zoom in/out
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
    }

    private fun initButton() {
        btn_hybrid.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        btn_satelit.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        btn_terrain.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
        btn_normal.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        //add to realtime firebase
        mMap.setOnMapClickListener {
            val lat = it.latitude
            val lon = it.longitude

            mMap.clear()

            val nama = convertCoordinat(lat, lon)
            val nama2 = convertCoordinat2(lat, lon)
            maps_name.text = "$lat - $lon"
            maps_kordinat.text = nama
            maps_kordinat2.text = nama2

            saveData(nama, nama2, lat, lon)
        }

    }

    private fun saveData(nama: String,nama2: String, lat: Double, lon: Double) {
        if (maps_switch.isChecked) {

            when (maps_switch.text) {
                "Update ke Database?" -> {

                    AlertDialog.Builder(this).apply {
                        setTitle("Update")
                        setMessage("Yakin ingin mengupdate Marker?")
                        setCancelable(false)

                        setPositiveButton("Ya") {dialog, which ->
                            mMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Marker in $nama")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))

                            val maps = Maps(nama, nama2, lat.toString(), lon.toString())
                            reference?.child(item?.key ?: "")?.setValue(maps)
                            finish()
                        }
                        setNegativeButton("Batal") {dialog, which ->
                            dialog?.dismiss()
                        }
                    }.show()

                }
                else -> {

                    AlertDialog.Builder(this).apply {
                        setTitle("Simpan")
                        setMessage("Yakin ingin menyimpan Marker?")
                        setCancelable(false)

                        setPositiveButton("Ya") {dialog, which ->
                            mMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Marker in $nama")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))

                            val key = reference?.push()?.key
                            val maps = Maps(nama, nama2, lat.toString(), lon.toString())
                            reference?.child(key ?: "")?.setValue(maps)
                            finish()
                        }
                        setNegativeButton("Batal") {dialog, which ->
                            dialog?.dismiss()
                        }
                    }.show()
                }
            }

        } else {
            mMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Marker in $nama")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))
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