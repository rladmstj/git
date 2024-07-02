package com.example.firstweek

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firstweek.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 권한 요청
        requestPermissions()
        navView.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack(R.id.navigation_home, false)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        if (navController.currentBackStackEntry != null) {
            navController.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private val PERM_STORAGE = 9
    private val PERM_CAMERA = 10

    private fun requestPermissions() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERM_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERM_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 승인되었습니다. 필요한 작업을 수행하십시오.
                    permissionGranted(PERM_STORAGE)
                } else {
                    // 권한이 거부되었습니다.
                    permissionDenied(PERM_STORAGE)
                }
            }
            PERM_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 승인되었습니다. 필요한 작업을 수행하십시오.
                    permissionGranted(PERM_CAMERA)
                } else {
                    // 권한이 거부되었습니다.
                    permissionDenied(PERM_CAMERA)
                }
            }
        }
    }

    private fun permissionGranted(requestCode: Int) {
        // 권한이 승인되었을 때의 작업을 여기에 추가하십시오.
    }

    private fun permissionDenied(requestCode: Int) {
        // 권한이 거부되었을 때의 작업을 여기에 추가하십시오.
    }
}
