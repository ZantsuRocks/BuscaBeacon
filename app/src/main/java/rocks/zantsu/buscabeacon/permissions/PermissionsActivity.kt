package rocks.zantsu.buscabeacon.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
//import android.view.Gravity
//import android.view.View
//import android.widget.Button
//import android.widget.LinearLayout
//import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
//import rocks.zantsu.buscabeacon.presentation.WearApp
import rocks.zantsu.buscabeacon.presentation.theme.BuscaBeaconTheme

open class PermissionsActivity : ComponentActivity() {

    val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle Permission granted/rejected
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    Log.d(TAG, "$permissionName permission granted: $isGranted")
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    Log.d(TAG, "$permissionName permission granted: $isGranted")
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        }

    companion object {
        const val TAG = "PermissionsActivity"
    }
}

class PermissionsHelper(val context: Context) {
    // Manifest.permission.ACCESS_BACKGROUND_LOCATION
    // Manifest.permission.ACCESS_FINE_LOCATION
    // Manifest.permission.BLUETOOTH_CONNECT
    // Manifest.permission.BLUETOOTH_SCAN
    fun isPermissionGranted(permissionString: String): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            permissionString
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun setFirstTimeAskingPermission(permissionString: String, isFirstTime: Boolean) {
        val sharedPreference = context.getSharedPreferences(
            "org.altbeacon.permisisons",
            ComponentActivity.MODE_PRIVATE
        )
        sharedPreference.edit().putBoolean(
            permissionString,
            isFirstTime
        ).apply()
    }

    fun isFirstTimeAskingPermission(permissionString: String): Boolean {
        val sharedPreference = context.getSharedPreferences(
            "org.altbeacon.permisisons",
            ComponentActivity.MODE_PRIVATE
        )
        return sharedPreference.getBoolean(
            permissionString,
            true
        )
    }

    fun beaconScanPermissionGroupsNeeded(backgroundAccessRequested: Boolean = false): List<Array<String>> {
        val permissions = ArrayList<Array<String>>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // As of version M (6) we need FINE_LOCATION (or COARSE_LOCATION, but we ask for FINE)
            permissions.add(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // As of version Q (10) we need FINE_LOCATION and BACKGROUND_LOCATION
            if (backgroundAccessRequested) {
                permissions.add(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // As of version S (12) we need FINE_LOCATION, BLUETOOTH_SCAN and BACKGROUND_LOCATION
            // Manifest.permission.BLUETOOTH_CONNECT is not absolutely required to do just scanning,
            // but it is required if you want to access some info from the scans like the device name
            // and the aditional cost of requsting this access is minimal, so we just request it
            permissions.add(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
        return permissions
    }

}


open class BeaconScanPermissionsActivity : PermissionsActivity() {
    lateinit var permissionGroups: List<Array<String>>
    var scale: Float = 1.0f
        get() {
            return this.getResources().getDisplayMetrics().density
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        layout = LinearLayout(this)
//        layout.setPadding(dp(20))
//        layout.gravity = Gravity.CENTER
//        layout.setBackgroundColor(Color.WHITE)
//        layout.orientation = LinearLayout.VERTICAL
        val backgroundAccessRequested = intent.getBooleanExtra("backgroundAccessRequested", true)
        val title = intent.getStringExtra("title") ?: "Permissions Needed"
        val message = intent.getStringExtra("message") ?: "Dar permissões!"
        val continueButtonTitle = intent.getStringExtra("continueButtonTitle") ?: "Continuar"
//        val permissionButtonTitles = intent.getBundleExtra("permissionBundleTitles") ?: getDefaultPermissionTitlesBundle()

        permissionGroups = PermissionsHelper(this).beaconScanPermissionGroupsNeeded(backgroundAccessRequested)

        setContent {
            PermWidget(message, "", continueButtonTitle);
        }

//        val params = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        params.setMargins(dp(0), dp(10), dp(0), dp(10))
//
//
//        val titleView = TextView(this)
//        titleView.setGravity(Gravity.CENTER)
//        titleView.textSize = dp(10).toFloat()
//        titleView.text = title
//        titleView.layoutParams = params
//
//        layout.addView(titleView)
//        val messageView = TextView(this)
//        messageView.text = message
//        messageView.setGravity(Gravity.CENTER)
//        messageView.textSize = dp(5).toFloat()
//        messageView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//        messageView.layoutParams = params
//        layout.addView(messageView)
//
//
//        var index = 0
//        for (permissionGroup in permissionGroups) {
//            val button = Button(this)
//            val buttonTitle = permissionButtonTitles.getString(permissionGroup.first())
//            button.id = index
//            button.text = buttonTitle
//            button.layoutParams = params
//            button.setOnClickListener(buttonClickListener)
//            layout.addView(button)
//            index += 1
//        }
//
//
//        continueButton = Button(this)
//        continueButton.text = continueButtonTitle
//        continueButton.isEnabled = false
//        continueButton.setOnClickListener {
//            this.finish()
//        }
//        continueButton.layoutParams = params
//        layout.addView(continueButton)
//
//        setContentView(layout)
    }

    @Composable
    fun PermWidget(message: String, buttonTitle: String, continueButtonTitle: String) {
//        val permissionButtonTitles = intent.getBundleExtra("permissionBundleTitles") ?: getDefaultPermissionTitlesBundle()
        val activity = (LocalContext.current as? Activity)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = message
            )
                Button(onClick = {
                    buttonClickListener();
                }) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.secondary,
                        text = "Permitir"
                    )
                }


//            Button(
//                onClick = {
//                    val permissionsGroup = permissionGroups.get(0);
//                    promptForPermissions(permissionsGroup);
//                }
//            ) {
//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Center,
//                    color = MaterialTheme.colors.secondary,
//                    text = buttonTitle
//                )
//            }
            Button(
                onClick = {
                    activity?.finish();
                }
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    text = continueButtonTitle
                )
            }
        }
    }

    fun dp(value: Int): Int {
        return (value * scale + 0.5f).toInt()
    }

    fun buttonClickListener() {
        val backgroundAccessRequested = intent.getBooleanExtra("backgroundAccessRequested", true);
        permissionGroups = PermissionsHelper(this).beaconScanPermissionGroupsNeeded(backgroundAccessRequested);
        for ((i,el) in permissionGroups.withIndex()) {
            val permissionsGroup = permissionGroups.get(i)
            promptForPermissions(permissionsGroup)
        }
    }
//    val buttonClickListener = View.OnClickListener { button ->
//        val permissionsGroup = permissionGroups.get(button.id)
//        promptForPermissions(permissionsGroup)
//    }

    @SuppressLint("InlinedApi")
    fun getDefaultPermissionTitlesBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(Manifest.permission.ACCESS_FINE_LOCATION, "Location")
        bundle.putString(Manifest.permission.ACCESS_BACKGROUND_LOCATION, "Background Location")
        bundle.putString(Manifest.permission.BLUETOOTH_SCAN, "Bluetooth")
        return bundle
    }


    fun allPermissionGroupsGranted(): Boolean {
        for (permissionsGroup in permissionGroups) {
            if (!allPermissionsGranted(permissionsGroup)) {
                return false
            }
        }
        return true
    }

//    fun setButtonColors() {
//        var index = 0
//        for (permissionsGroup in this.permissionGroups) {
//            val button = findViewById<Button>(index)
//            if (allPermissionsGranted(permissionsGroup)) {
//                button.setBackgroundColor(Color.parseColor("#448844"))
//            }
//            else {
//                button.setBackgroundColor(Color.RED)
//            }
//            index += 1
//        }
//    }
//    override fun onResume() {
//        super.onResume()
//        setButtonColors()
//        if (allPermissionGroupsGranted()) {
//            continueButton.isEnabled = true
//        }
//    }

    fun promptForPermissions(permissionsGroup: Array<String>) {
        if (!allPermissionsGranted(permissionsGroup)) {
            val firstPermission = permissionsGroup.first()

            var showRationale = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showRationale = shouldShowRequestPermissionRationale(firstPermission)
            }
            if (showRationale || PermissionsHelper(this).isFirstTimeAskingPermission(firstPermission)) {
                PermissionsHelper(this).setFirstTimeAskingPermission(firstPermission, false)
                requestPermissionsLauncher.launch(permissionsGroup)
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Can't request permission")
                builder.setMessage("This permission has been previously denied to this app.  In order to grant it now, you must go to Android Settings to enable this permission.")
                builder.setPositiveButton("OK", null)
                builder.show()
            }
        }
    }

    fun allPermissionsGranted(permissionsGroup: Array<String>): Boolean {
        val permissionsHelper = PermissionsHelper(this)
        for (permission in permissionsGroup) {
            if (!permissionsHelper.isPermissionGranted(permission)) {
                return false
            }
        }
        return true
    }

    companion object {
        const val TAG = "BeaconScanPermissionActivity"
        fun allPermissionsGranted(context: Context, backgroundAccessRequested: Boolean): Boolean {
            val permissionsHelper = PermissionsHelper(context)
            val permissionsGroups =
                permissionsHelper.beaconScanPermissionGroupsNeeded(backgroundAccessRequested)
            for (permissionsGroup in permissionsGroups) {
                for (permission in permissionsGroup) {
                    if (!permissionsHelper.isPermissionGranted(permission)) {
                        return false
                    }
                }
            }
            return true
        }
    }
}