package com.agh.student.mateuszn.mati12.mentorcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class NSWE extends Activity implements OnItemClickListener {

    //Zmienna wypełniona nazwami wykrytych urządzeń
    ArrayAdapter<String> listAdapter;
    //Element widoku interfejsu użytkownika
    ListView listView;
    //Zmienna BluetoothAdapter do wykrywania urządzań bluetooth
    BluetoothAdapter btAdapter;
    //Dodanie urządzeń bluetooth do listy
    Set<BluetoothDevice> devicesArray;
    //Lista urządzeń powiązanych w pary
    ArrayList<String> pairedDevices;
    //Lista urządzeń bluetooth
    ArrayList<BluetoothDevice> devices;

    //Elementy interfejsu użytkownika przyciski
    Button bNorthData;
    Button bSouthData;
    Button bWestData;
    Button bEastData;
    Button bnNorthData;
    Button bsSouthData;

    //Element paska wyszukiwania do obsługi chwytaka
    SeekBar seekBarData;

    //Identyfikator urządzenia wykorzystany w funkcji wymiany danych między komórką a sterownikiem
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Wiadomości dla Handlera dotyczące połączenia z urządzeniem bluetooth
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    IntentFilter filter;
    BroadcastReceiver receiver;
    String tag = "debugging";

    Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	//Widok aktywności jako plik w formacie xml activity_nswe
	setContentView(R.layout.activity_nswe);
	//Przypisanie przyciskom elementów z UI
	bNorthData = (Button) findViewById(R.id.bNorthData);
	bSouthData = (Button) findViewById(R.id.bSouthData);
	bWestData = (Button) findViewById(R.id.bWestData);
	bEastData = (Button) findViewById(R.id.bEastData);
	seekBarData = (SeekBar) findViewById(R.id.seekBarData);
	bnNorthData = (Button) findViewById(R.id.bnNorthData);
	bsSouthData = (Button) findViewById(R.id.bsSouthData);

	//Do obsługi wiadomości o stanie połączania z modułem bluetooth
	mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Log.i(tag, "in handler");
		super.handleMessage(msg);
		switch(msg.what){
		    case SUCCESS_CONNECT:
			// DO something
			while (true) {
			    //Połączone urządzenia bluetooth sterownik oraz komórka
			    final ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
			    //Wiadomość Toast udane połaczenie
			    Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_LONG).show();
			    //Wysłanie tekstu udane połączenie do sterownika
			    String s = "successfully connected";
			    //Metoda write przesyła dane typu ciąg znaków (string) jako bajty.
			    connectedThread.write(s.getBytes());
			    Log.i(tag, "connected");
			    //Obsługa pozostałych elementów interfejsu użytkownika
			    try {
				//setOnClickListener wykrywa zdarzenie naciśnięcia odpowiedniego przycisku
				//po naciśnięciu tworzona jest nowa zmienna typu ciąg znaków (String) reprezentująca oczekiwane zachowanie się
				//silników sterownika.
				bNorthData.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					String s1 = "n";
					connectedThread.write(s1.getBytes());
					Toast.makeText(getApplicationContext(),"North",Toast.LENGTH_SHORT).show();

				    }

				});

				bSouthData.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					String s2 = "s";
					connectedThread.write(s2.getBytes());
					Toast.makeText(getApplicationContext(),"South",Toast.LENGTH_SHORT).show();

				    }
				});

				bWestData.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					String s2 = "w";
					connectedThread.write(s2.getBytes());
					Toast.makeText(getApplicationContext(),"West",Toast.LENGTH_SHORT).show();

				    }
				});

				bEastData.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					String s2 = "e";
					connectedThread.write(s2.getBytes());
					Toast.makeText(getApplicationContext(),"East",Toast.LENGTH_SHORT).show();

				    }
				});

				bnNorthData.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					String s2 = "l";
					connectedThread.write(s2.getBytes());
					Toast.makeText(getApplicationContext(),"East",Toast.LENGTH_SHORT).show();

				    }
				});

				bsSouthData.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					String s2 = "p";
					connectedThread.write(s2.getBytes());
					Toast.makeText(getApplicationContext(),"East",Toast.LENGTH_SHORT).show();

				    }
				});

				//Dla elementu paska wyszukiwania ustawiono w interfejsie użytkownika zmienną android:max = 10
				//Zmiana znacznika na suwaku powoduje przesłanie do sterownika odpowiedniej cyfry która spowoduje odpowiednie
				//zachowanie się silników chwytaka.
				seekBarData.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				    @Override
				    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


					String s2 = Integer.toString(progress);
					connectedThread.write(s2.getBytes());
					if (progress < 6 && progress > 0) {
					    Toast.makeText(getApplicationContext(),"Close",Toast.LENGTH_SHORT).show();
					}  else if (progress >= 6 && progress < 10) {
					    Toast.makeText(getApplicationContext(),"Open",Toast.LENGTH_SHORT).show();
					} else {
					    Toast.makeText(getApplicationContext(),"Motor Off",Toast.LENGTH_SHORT).show();
					}
					Log.i(tag,"Progress " + s2);
				    }

				    @Override
				    public void onStartTrackingTouch(SeekBar seekBar) {

				    }

				    @Override
				    public void onStopTrackingTouch(SeekBar seekBar) {

				    }
				});

			    } catch (Exception e) {
				e.printStackTrace();
			    }

			    break;
			}
			break;
		    case MESSAGE_READ:
			byte[] readBuf = (byte[])msg.obj;
			String string = new String(readBuf);
			Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
			break;

		}
	    }
	};
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




    }
    //Wykrywanie urządzeń bluetooth
    private void startDiscovery() {
	// TODO Auto-generated method stub
	btAdapter.cancelDiscovery();
	btAdapter.startDiscovery();

    }
    //Obsługa żądania włączenia modułu bluetooth na komórce
    private void turnOnBT() {
	// TODO Auto-generated method stub
	Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	startActivityForResult(intent, 1);
    }

    //Przesyłanie do widoku listy urządzeń które zostały powiązane w pary
    private void getPairedDevices() {
	// TODO Auto-generated method stub
	devicesArray = btAdapter.getBondedDevices();
	if(devicesArray.size()>0){
	    for(BluetoothDevice device:devicesArray){
		pairedDevices.add(device.getName());

	    }
	}
    }

    //Typowe elementy do obsługi bluetooth dla Androida
    private void init() {
	// TODO Auto-generated method stub
	//W elemencie list view wyświetlane będą wykryte urządzenia bluetooth
	listView=(ListView)findViewById(R.id.listView);
	//Ustawienie nsłuchiwanie zdarzeń kliknięcia na element interfejsu uzytkownika listView
	listView.setOnItemClickListener(this);
	//Element listAdapter jako wbudowany w Android widok listy simple_list_item_1
	listAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
	//Ustawienie listAdapter aby wartości odczytane wypełniły element istView
	listView.setAdapter(listAdapter);
	//BluetoothAdapter żądanie hardwaru bluetooth dla urządzenia mobilnego
	btAdapter = BluetoothAdapter.getDefaultAdapter();
	//Urządzenia połączone w pary. Tylko takie urządzenia mogą między sobą wysyłać dane poprzez połączenie bluetooth.
	pairedDevices = new ArrayList<String>();
	//Nasłuchiwanie wydarzeń ACTION_FOUND kiedy urządzenie bluetooth zostało wykryte. BluetoothDevice reprezentuje zdalne urządzenie bluetooth
	filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	//Zdalne urządzenia bluetooth jako wypełnienie alementu ArrayList
	devices = new ArrayList<BluetoothDevice>();
	//Nasłuchiwanie wiadomości broadcast
	receiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Akcja jaką wykryła aktywność intent jako element typu ciąg znaków
		String action = intent.getAction();

		//Warunek jeżeli zdalne urządzenie bluetooth zostało wykryte oraz akcja jest równa aktywności wykrytej intent
		if(BluetoothDevice.ACTION_FOUND.equals(action)){
		    //Wykryte zdalne urządzenie bluetooth jeżeli jest połączone w pary oraz wykryte zostanie dodane do listy
		    //wykrytych zdalnych urządzeń bluetooth
		    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    //Dodanie nowego elementu device do ArrayList
		    devices.add(device);
		    String s = "";
		    //Pętla w celu wyświetlenia czy wykryte urządzenie jest połączone w pary z telefonem komórkowym
		    //w przypadku gdzy oba elementy końcowe są połączone w pary przy nazwie urządzenia w elemencie interfejsu graficznego listView
		    //wyświetlony zostanie (Paired).
		    for(int a = 0; a < pairedDevices.size(); a++){
			if(device.getName().equals(pairedDevices.get(a))){
			    //append
			    s = "(Paired)";
			    break;
			}
		    }
		    //Dodanie do elementu listView wszystkich informacji o wykrytym urządzeniu tj. jego nazwa, adres MAC oraz informacja czy są
		    //połączone w pary czy nie
		    listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
		}

		else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
		    // run some code
		}
		else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
		    // run some code



		}
		//Wykrywanie stanu hardwaru bluetooth
		else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
		    //W przypadku gdy bluetooth jest wyłączony, załączona zostanie metoda uruchomiająca bluetooth na telefonie komórkowym.
		    if(btAdapter.getState() == btAdapter.STATE_OFF){
			turnOnBT();
		    }
		}

	    }
	};

	//Zapisywanie stanów hardwaru radio bluetooth
	registerReceiver(receiver, filter);
	filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	registerReceiver(receiver, filter);
	filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	registerReceiver(receiver, filter);
	filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
	registerReceiver(receiver, filter);
    }


    @Override
    protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	unregisterReceiver(receiver);
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

    //Definiowanie obsługi naciśnięcia na element interfejsu użytkownika listView.
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			    long arg3) {
	// TODO Auto-generated method stub

	if(btAdapter.isDiscovering()){
	    btAdapter.cancelDiscovery();
	}
	//Jeżeli ciąg znaków wyświetlonych w elemencie listView zawiera ciąg znaków paired można połączyć urządzenia
	if(listAdapter.getItem(arg2).contains("Paired")){
	    //Zdalne urządzenie bluetooth jako te które wybrano z odpowiedniego elementu listy
	    BluetoothDevice selectedDevice = devices.get(arg2);
	    //Po wybraniu urządzenia nastąpi uruchomienie klasy ConnectedThread obsługującej połaczenie urządzeń ze sobą.
	    ConnectThread connect = new ConnectThread(selectedDevice);
	    connect.start();
	    Log.i(tag, "in click listener");
	}
	else{
	    Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
	}
    }

    //W przypadku naciśnięcia przycisku wstecz wyłączenie wszystkich funkcji w tej aplikacji
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


    //Klasa pobrana z stron developerów Google obsługująca utworzenie nowego wątku wymiany informacji przez protokoły RFCOMM między
    //urządzeniami bluetooth.

    private class ConnectThread extends Thread {

	private final BluetoothSocket mmSocket;
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
	    } catch (IOException connectException) {	Log.i(tag, "connect failed");
		// Unable to connect; close the socket and get out
		try {
		    mmSocket.close();
		} catch (IOException closeException) { }
		return;
	    }

	    // Do work to manage the connection (in a separate thread)

	    mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
	}



	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
	    try {
		mmSocket.close();
	    } catch (IOException e) { }
	}
    }

    //Klasa pobrana ze strony developerów Google podczymująca połączenie i ciągłą wymianę danych pomiędzy urządzeniami bluetooth.
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
	    byte[] buffer;  // buffer store for the stream
	    int bytes; // bytes returned from read()

	    // Keep listening to the InputStream until an exception occurs
	    while (true) {
		try {
		    // Read from the InputStream
		    buffer = new byte[1024];
		    bytes = mmInStream.read(buffer);
		    // Send the obtained bytes to the UI activity
		    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
			    .sendToTarget();

		} catch (IOException e) {
		    break;
		}
	    }
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
	    try {
		mmOutStream.write(bytes);
	    } catch (IOException e) { }
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
	    try {
		mmSocket.close();
	    } catch (IOException e) { }
	}
    }
}