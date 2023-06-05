/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package rocks.zantsu.buscabeacon.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region
import rocks.zantsu.buscabeacon.R
import rocks.zantsu.buscabeacon.permissions.BeaconScanPermissionsActivity
import rocks.zantsu.buscabeacon.permissions.PermissionsActivity
import rocks.zantsu.buscabeacon.presentation.theme.BuscaBeaconTheme

const val TAG: String = "MainAPP"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContent {
            WearApp("Android");
        }

        // TODO: Add code here to obtain location permission from user
        // TODO: Add beaconParsers for any properietry beacon formats you wish to detect
//        val intent = Intent(this, PermissionsActivity::class.java);
//        intent.putExtra("key", value);
//        startActivity(intent);

        val beaconManager =  BeaconManager.getInstanceForApplication(this);
        val region = Region("all-beacons-region", null, null, null);
        // Set up a Live Data observer so this Activity can get ranging callbacks
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        beaconManager.getRegionViewModel(region).rangedBeacons.observe(this, rangingObserver);
        beaconManager.startRangingBeacons(region);

        val beaconThread = BeaconThread();
        beaconThread.start()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        // You MUST make sure the following dynamic permissions are granted by the user to detect beacons
        //
        //    Manifest.permission.BLUETOOTH_SCAN
        //    Manifest.permission.BLUETOOTH_CONNECT
        //    Manifest.permission.ACCESS_FINE_LOCATION
        //    Manifest.permission.ACCESS_BACKGROUND_LOCATION // only needed to detect in background
        //
        // The code needed to get these permissions has become increasingly complex, so it is in
        // its own file so as not to clutter this file focussed on how to use the library.

            val intent = Intent(this, BeaconScanPermissionsActivity::class.java)
            intent.putExtra("backgroundAccessRequested", true)
            startActivity(intent)
        }
    }

    private val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons");
        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away");
    }
}

class BeaconThread: Thread() {
    public override fun run() {
        while(true){
            Log.d(TAG,"${Thread.currentThread()} is running");
            Thread.sleep(100);
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
    BuscaBeaconTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ){
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text="Frio"
            )
        }
//        {
//            Greeting(greetingName = greetingName)
//        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}