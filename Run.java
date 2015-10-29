package com.agh.student.mateuszn.mati12.mentorcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Run extends Activity implements AdapterView.OnItemClickListener, SensorEventListener {

    /*Elementy połączenie bluetooth są takie same jak w przypadku klasy NSWE.
    * Dodatkowymi elementami opisywanej aplikacji jest zmiana wyglądu na widok kostki trójwymiarowej
    * po udanym połączeniu.
    * */

    Button sendStuff;

    Float baseAzimuth;

    Float basePitch;

    Float baseRoll;
    //Obiekt klasy Worker reprezentujący element kostki 3D
    private Worker worker;

    /*Float azimuth;
    Float pitch;
    Float roll;*/

    private float azimuthDifference;
    private float pitchDifference;
    private float rollDifference;
    private float lol;
    /* Rotation values */
    private float xrot;
    private float yrot;

    RelativeLayout allLayout;

    private SensorManager manager;



    float f;
    ArrayAdapter<String> listAdapter;
    ListView listView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    public static ArrayList<BluetoothDevice> devices;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    IntentFilter filter;
    BroadcastReceiver receiver;
    String tag = "debugging";
    private int number = 0;
    Context context;

    private SensorManager mSensorManager = null;

    private Sensor mAccelerometer = null;


    Thread thread2, thread3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	worker = new Worker(this);
	number++;
	//Zdefiniowanie początkowego wyglądu jako listView obejmującą cały ekran aktywności.
	//Po udanym połączeniu wygląd ma zostać zmieniony na widok z klasy Worker
	setContentView(R.layout.activity_bluetooth);

	//Nowy wątek w onCreate w celu zmiany widoku aktywności na kostkę 3D oraz limitowanie danych przesyłanych do sterownika.
	//Dane będą przesyłane co 1 sekundę, pomiędzy tym czasem w buforze nie będzie żadnych dodatkowych danych
	thread2 = new Thread() {
	    public void run() {
		while (true) {
		    try {
			Log.d(tag, "local Thread sleeping");
			Thread.sleep(1000);
		    } catch (InterruptedException e) {
			Log.e(tag, "local Thread error", e);
		    }
		}
	    }
	};

	//Zdefiniowanie obsługi czujników.
	mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	init();
	if(btAdapter==null){
	    Toast.makeText(getApplicationContext(), "No bluetooth detected", Toast.LENGTH_SHORT).show();
	    finish();
	}
	else{
	    if(!btAdapter.isEnabled()){
		turnOnBT();
	    }

	    getPairedDevices();
	    startDiscovery();
	}

	manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

    }
    Handler mHandler = new Handler(){

	@Override
	public void handleMessage(Message msg) {

	    // TODO Auto-generated method stub
	    super.handleMessage(msg);
	    Log.i(tag, "in handler");
	    Log.i(tag, "Number " + number);

	    //zmianę wglądu na zdefiniowany w klasie Worker
	    setContentView(worker);



	    switch(msg.what){
		//W przypadku udanego połączenia
		case SUCCESS_CONNECT:

		    while (true) {

			final ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
			Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_LONG).show();
			String s = "successfully connected";
			connectedThread.write(s.getBytes());

			Log.i(tag, "connected");
			try {
			    while(true) {

				//Wątek zdefiniowany w onCreate
				thread2.start();

				//Zabieg w celu uwolnienia pamięci
				try {
				    Thread.sleep(16);

				} catch (Exception e) {
				    e.printStackTrace();
				}
				//Nowy wątek odczytujący dane z wychylenia elementu kostki trójwymiarowej i przesyłający odpowiednie znaki
				//do sterownika w odpowiedzi na te zmiany.
				thread3 = new Thread() {

				    public void run() {

					while(true) {

					    try {
						//x1 oraz y1 są to elementy public static float reprezenujące wychylenie kostki z klasy Worker
						//xrot, yrot

						if ((Worker.x1> -60 && Worker.x1 < -10) && ((Worker.y1 >-30.5 && Worker.y1 < 30.5))) {
						    String n = "n";
						    connectedThread.write(n.getBytes());
						    Log.i(tag, "NORTH");

						}

						if ((Worker.x1> 10 && Worker.x1 < 60) && ((Worker.y1 >-30.5 && Worker.y1 < 30.5))) {
						    String ss = "s";
						    connectedThread.write(ss.getBytes());
						    Log.i(tag, "SOUTH");
						}

						if ((Worker.x1 > -30.5 && Worker.x1 < 30.5) && ((Worker.y1 >-60 && Worker.y1 < -10))) {
						    String w = "w";
						    connectedThread.write(w.getBytes());
						    Log.i(tag, "WEST");
						}

						if ((Worker.x1 > -30.5 && Worker.x1 < 30.5) && ((Worker.y1 >10&& Worker.y1 < 60))) {
						    String e = "e";
						    connectedThread.write(e.getBytes());
						    Log.i(tag, "EAST");
						}

						Thread.sleep(1000);
					    } catch (Exception e) {
						e.printStackTrace();
					    }
					}

				    }

				};
				thread3.start();


			    }
			} catch (Exception e) {
			    e.printStackTrace();

			}

			break;
		    }
		    break;
		case MESSAGE_READ:
		    byte[] readBuf = (byte[]) msg.obj;
		    String string = new String(readBuf);
		    Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
		    break;
	    }
	}
    };



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    finish();
	    System.exit(0);
	    return true;
	}
	return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	finish();
    }


    private void startDiscovery() {
	// TODO Auto-generated method stub
	btAdapter.cancelDiscovery();
	btAdapter.startDiscovery();

    }
    private void turnOnBT() {
	// TODO Auto-generated method stub
	Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	startActivityForResult(intent, 1);
    }
    private void getPairedDevices() {
	// TODO Auto-generated method stub
	devicesArray = btAdapter.getBondedDevices();
	if(devicesArray.size()>0){
	    for(BluetoothDevice device:devicesArray){
		pairedDevices.add(device.getName());

	    }
	}
    }
    private void init() {
	// TODO Auto-generated method stub
	listView=(ListView)findViewById(R.id.listView);
	listView.setOnItemClickListener(this);
	listAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
	listView.setAdapter(listAdapter);
	btAdapter = BluetoothAdapter.getDefaultAdapter();
	pairedDevices = new ArrayList<String>();
	filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	devices = new ArrayList<BluetoothDevice>();
	receiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		if(BluetoothDevice.ACTION_FOUND.equals(action)){
		    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    devices.add(device);
		    String s = "";
		    for(int a = 0; a < pairedDevices.size(); a++){
			if(device.getName().equals(pairedDevices.get(a))){
			    //append
			    s = "(Paired)";
			    break;
			}
		    }

		    listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
		}

		else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
		    // run some code
		}
		else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
		    // run some code



		}
		else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
		    if(btAdapter.getState() == btAdapter.STATE_OFF){
			turnOnBT();
		    }
		}

	    }
	};

	registerReceiver(receiver, filter);
	filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	registerReceiver(receiver, filter);
	filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	registerReceiver(receiver, filter);
	filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
	registerReceiver(receiver, filter);
    }

    /*public void doStartBtleScan() {
	mLeScanCallBack =
    }*/


    @Override
    protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();

	mSensorManager.unregisterListener(this);
	worker.onPause();
	//unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
	super.onResume();
	worker.onResume();
	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	if (!btAdapter.isEnabled()) {
	    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableIntent, 1);
	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if(resultCode == RESULT_CANCELED){
	    Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
	    finish();
	}
    }



    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			    long arg3) {
	// TODO Auto-generated method stub



	if(btAdapter.isDiscovering()){
	    btAdapter.cancelDiscovery();
	}
	if(listAdapter.getItem(arg2).contains("Paired")){

	    BluetoothDevice selectedDevice = devices.get(arg2);
	    ConnectThread connect = new ConnectThread(selectedDevice);
	    connect.start();
	    Log.i(tag, "in click listener");
	    Log.i(tag, devices.get(0).toString());
	}
	else{
	    Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
	}
    }



    @Override
    public void onSensorChanged(final SensorEvent event) {
	final float azimuth = event.values[0];
	float pitch = event.values[1];
	float roll = event.values[2];


	if ( null == baseAzimuth ) {
	    baseAzimuth = azimuth;
	}
	if ( null == basePitch ) {
	    basePitch = pitch;
	}
	if ( null == baseRoll ) {
	    baseRoll = roll;
	}

	setAzimuthDifference(azimuth - baseAzimuth);
	setPitchDifference(pitch - basePitch);
	setRollDifference(roll - baseRoll);

	yrot -= getRollDifference();
	xrot -= getPitchDifference();

	baseRoll = roll;
	basePitch = pitch;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class ConnectThread extends Thread {

	public final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;

	public ConnectThread(BluetoothDevice device) {
	    // Use a temporary object that is later assigned to mmSocket,
	    // because mmSocket is final
	    BluetoothSocket tmp = null;
	    mmDevice = device;
	    Log.i(tag, "construct");
	    // Get a BluetoothSocket to connect with the given BluetoothDevice
	    try {
		// MY_UUID is the app's UUID string, also used by the server code
		tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	    } catch (IOException e) {
		Log.i(tag, "get socket failed");

	    }
	    mmSocket = tmp;
	}

	public void run() {
	    // Cancel discovery because it will slow down the connection
	    btAdapter.cancelDiscovery();
	    Log.i(tag, "connect - run");
	    try {
		// Connect the device through the socket. This will block
		// until it succeeds or throws an exception
		mmSocket.connect();
		Log.i(tag, "connect - succeeded");
		Log.i(tag, "mmSocket " + mmSocket);
	    } catch (IOException connectException) {	Log.i(tag, "connect failed");
		// Unable to connect; close the socket and get out
		try {
		    mmSocket.close();
		} catch (IOException closeException) { }
		return;
	    }

	    // Do work to manage the connection (in a separate thread)
	    try {
		mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	}



	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
	    try {
		mmSocket.close();
	    } catch (IOException e) { }
	}
    }


    private class ConnectedThread extends Thread {
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;

	public ConnectedThread(BluetoothSocket socket) {
	    mmSocket = socket;
	    InputStream tmpIn = null;
	    OutputStream tmpOut = null;

	    // Get the input and output streams, using temp objects because
	    // member streams are final
	    try {
		tmpIn = socket.getInputStream();
		tmpOut = socket.getOutputStream();
	    } catch (IOException e) { }

	    mmInStream = tmpIn;
	    mmOutStream = tmpOut;
	}

	public void run() {
	    byte[] buffer = new byte[1024];  // buffer store for the stream
	    int bytes; // bytes returned from read()

	    // Keep listening to the InputStream until an exception occurs
	    while (true) {
		try {
		    // Read from the InputStream
		    bytes = mmInStream.read(buffer);
		    // Send the obtained bytes to the UI activity
		    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
			    .sendToTarget();
		    //mHandler.obtainMessage(SUCCESS_CONNECT,getAzimuthDifference()).sendToTarget();
		} catch (IOException e) {
		    break;
		}
	    }
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
	    // while(true) {
	    try {
		mmOutStream.write(bytes);

		//mHandler.obtainMessage(CON_MESSAGE,bytes);
	    } catch (IOException e) {
	    }
	    // }
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
	    try {
		mmSocket.close();
	    } catch (IOException e) { }
	}
    }

    public float getAzimuthDifference() {
	return azimuthDifference;
    }

    public void setAzimuthDifference(float azimuthDifference) {
	this.azimuthDifference = azimuthDifference;
    }

    public float getPitchDifference() {
	return pitchDifference;
    }

    public void setPitchDifference(float pitchDifference) {
	this.pitchDifference = pitchDifference;
    }

    public float getRollDifference() {
	return rollDifference;
    }

    public void setRollDifference(float rollDifference) {
	this.rollDifference = rollDifference;
    }

    private Runnable updateTimerThread = new Runnable()
    {
	public void run()
	{
	    //write here whaterver you want to repeat
	    mHandler.postDelayed(this, 1000);
	}
    };
}
