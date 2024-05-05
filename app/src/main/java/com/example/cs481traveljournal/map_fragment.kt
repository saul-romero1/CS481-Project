import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cs481traveljournal.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places


/**
 * A simple [Fragment] subclass.
 * Use the [map_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Set the map coordinates to CSUSM, California.
        val csusm = LatLng(33.1237, -117.1557)

        // Set the map type to Hybrid.
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        // Add a marker on the map coordinates.
        googleMap.addMarker(
            MarkerOptions()
                .position(csusm)
                .title("California State University San Marcos")
        )

        // Move the camera to the map coordinates and zoom in closer.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(csusm, 15f))

        // Display traffic.
        googleMap.isTrafficEnabled = true

        googleMap.isMyLocationEnabled =true

        // Set click listeners
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
    }


    @SuppressLint("MissingPermission")
    override fun onMyLocationButtonClick(): Boolean {
        // Get the last known location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                location?.let {
                    zoomOnMap(LatLng(location.latitude, location.longitude))
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to get current location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        return true
    }



    override fun onMyLocationClick(location: Location) {
        Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    fun zoomOnMap(latLng: LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        googleMap?.animateCamera(newLatLngZoom)
    }

}