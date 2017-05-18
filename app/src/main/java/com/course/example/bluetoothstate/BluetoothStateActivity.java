/*
 * The emulator does not support Bluetooth so use your handset.
 * It demos best when handset Bluetooth is turned off.
 */

package com.course.example.bluetoothstate;

import android.app.Activity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public class BluetoothStateActivity extends Activity {
	
	String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
	String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
	BluetoothAdapter bluetooth = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       //Enabling Bluetooth and tracking the adapter state
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        BroadcastReceiver bluetoothState = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            String prevStateExtra = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);
            int previousState = intent.getIntExtra(prevStateExtra, -1);

            String tt = "";
            switch (state) {
              case (BluetoothAdapter.STATE_TURNING_ON) : {
                tt = "Bluetooth turning on"; break;
              }
              case (BluetoothAdapter.STATE_ON) : {
                tt = "Bluetooth on";
                unregisterReceiver(this);  
                break;
              }
              case (BluetoothAdapter.STATE_TURNING_OFF) : {
                tt = "Bluetooth turning off"; break;
              }
              case (BluetoothAdapter.STATE_OFF) : {
                tt = "Bluetooth off"; break;
              }
              default: break;
            }

            Toast.makeText(getApplicationContext(), tt, Toast.LENGTH_LONG).show();
          }
        };

        if (!bluetooth.isEnabled()) {
          String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
          String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
          registerReceiver(bluetoothState,
                           new IntentFilter(actionStateChanged));
          startActivityForResult(new Intent(actionRequestEnable), 0);
        }
        
      //Monitoring discovery
		BroadcastReceiver discoveryMonitor = new BroadcastReceiver() {

			  @Override
			  public void onReceive(Context context, Intent intent) {
			    if (dStarted.equals(intent.getAction())) { 
			      // Discovery has started.
			      Toast.makeText(getApplicationContext(),
			                     "Discovery Started...", Toast.LENGTH_SHORT).show();
			    }
			    else if (dFinished.equals(intent.getAction())) {
			      // Discovery has completed.
			      Toast.makeText(getApplicationContext(),
			                     "Discovery Completed...", Toast.LENGTH_SHORT).show();
			    }
			  }      
			};
			
			registerReceiver(discoveryMonitor, 
			                 new IntentFilter(dStarted));
			registerReceiver(discoveryMonitor, 
			                 new IntentFilter(dFinished));
			
			
			//Discovering remote Bluetooth Devices 
			BroadcastReceiver discoveryResult = new BroadcastReceiver() {
				  @Override
				  public void onReceive(Context context, Intent intent) {
				    String remoteDeviceName = 
				      intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
				    BluetoothDevice remoteDevice;
				    remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				    Toast.makeText(getApplicationContext(),
				                   "Discovered: " + remoteDeviceName, 
				                   Toast.LENGTH_SHORT).show();

				    // TODO Do something with the remote Bluetooth Device.
				  }
				};
				registerReceiver(discoveryResult, 
				                 new IntentFilter(BluetoothDevice.ACTION_FOUND));

				if (!bluetooth.isDiscovering())
				  bluetooth.startDiscovery();
				
    }//onCreate
    
    @Override
	protected void onActivityResult(int requestCode, 
			                        int resultCode, 
			                        Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Toast.makeText(getApplicationContext(), "Monitor Adapter State Change", Toast.LENGTH_LONG).show();
		
	}// onActivityResult
}