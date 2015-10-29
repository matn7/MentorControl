package com.agh.student.mateuszn.mati12.mentorcontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Worker extends GLSurfaceView implements Renderer, SensorEventListener {

    /*Zadanie klasy jest rysowanie elementu używając metod z biblioteki Open GL, rejestrowanie zmian wartości czujników
     * Obsługa elementów onTouch
      * */

    String tag = "debugging";
    private final SensorManager mSensorManager;

    private final Sensor mAccelerometer;

    private float azimuthDifference;
    private float pitchDifference;
    private float rollDifference;

    String devic = "E0:B9:A5:45:CE:0F";

    public static float x1, y1;


    //Definiowanie zmiennych potrzebynych do wykonania obiektu kostki 3D
    /** Zmienna Cube wenątch tej klasy zdefiniowane są elementy potrzebne do skonstruowania kostki 3D*/
    private Cube cube;

    /* Wartości obrotów */
    private float xrot;					//W osi X
    private float yrot;					//W osi Y

    /* Wartości szybkości obrotów użyte w metdzie obsługującej zdarzenia dotknięcia ekranu*/
    private float xspeed;				//W osi X
    private float yspeed;				//W osi Y

    private float z = -5.0f;			//Zmienna w przypadku wyświetlania elementu kostki na ekranie

    private int filter = 0;				//Filtr tekstur

    private boolean light = true;

    //Zmienne używane w metodzie obsługującej zdarzenia onTouch
    private float oldX;
    private float oldY;
    private final float TOUCH_SCALE = 0.2f;

    private Context context;



    public Worker(Context context) {
	super(context);
	//Zdefiniowanie obsługi czujników
	mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	//Czujniki typu accelerometers
	mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	//Do metody do rysowania
	this.setRenderer(this);

	this.requestFocus();
	this.setFocusableInTouchMode(true);

	this.context = context;

	//Obiekt klasy Cube
	cube = new Cube();
    }

    @Override
    public void onPause() {
	mSensorManager.unregisterListener(this);
	super.onPause();
    }

    @Override
    public void onResume() {
	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	super.onResume();
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    Float baseAzimuth;

    Float basePitch;

    Float baseRoll;


    //Metoda po implementacji interfejsu SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event)  {
	//Odczytywanie wartości z czujników jako azimuth, pitch, roll
	float azimuth = event.values[0];
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

	setAzimuthDifference(azimuth - baseAzimuth); //azimuthDifference = azmimuth - baseAzimuth
	setPitchDifference(pitch - basePitch);
	setRollDifference(roll - baseRoll);

	try {
	    Thread.sleep(16);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	//Wartości wychylenia
	yrot -= getRollDifference(); //wartości obrotu wzdłuż osi Y, yrot = yrot - rollDifference;
	xrot -= getPitchDifference(); //wartości wychylenia wzdłuż osi X, xrot = xrot - pitchDifference


	baseRoll = roll;
	basePitch = pitch;

	//Zmienne typu public static float odczytywane wewnątrz thread3 w klasie Run
	x1 = xrot;
	y1 = yrot;

    }




    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

	gl.glClearColor(0.0f, 0.4f, 0.58f, 1.0f);  // Kolor tła
	gl.glEnable(GL10.GL_DEPTH_TEST);   // buffor głębi usunięcie niewidocznej powierzchni
	gl.glDepthFunc(GL10.GL_LEQUAL);    // Typ testowania głębi
	gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // Widok perspektywy, efekt nice
	gl.glShadeModel(GL10.GL_SMOOTH);   // Płynne zanikanie kolorów (efekt wizualny)
	gl.glDisable(GL10.GL_DITHER);

    }

    /**
     * Rysowanie elementów metody biblioteki Open GL
     */
    public void onDrawFrame(GL10 gl) {
	//Czyszczenie bufora ekranu oraz głębi
	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	gl.glLoadIdentity();		//Resetowanie obecnego widoku macierzy

	//Początek rysowania
	gl.glTranslatef(0.0f, 0.0f, z);			//Wartości wyglądu ekranu
	gl.glScalef(0.8f, 0.8f, 0.8f); 			//Widok kostki 3D ma zajmować 80% ekranu

	//Pod wpływam zmiany wartości czujników efekt ma być wizualizowany jako odpowiedni obrót kostki
	gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);	//X
	gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);	//Y

	cube.draw(gl);					//Rysuj kostkę

	//Zmiana wartości zmiennych obrotowych
	xrot += xspeed;
	yrot += yspeed;
    }

    /**
     * Jeżeli zmieni się powierzchnia, resetowanie wyglądu
     */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
	if (height == 0) height = 1;   // Zapobiega dzieleniu przez zero
	float aspect = (float)width / height;

	// Wyświetlona powierzchmia ma zajmować całe okno.
	gl.glViewport(0, 0, width, height);

	// Odwzorowanie parspektywy aby pasowała do powierzchni.
	gl.glMatrixMode(GL10.GL_PROJECTION); // Odwzorowanie macierzy
	gl.glLoadIdentity();                 // Resetowanie odwzorowania macierzy
	// Użycie odwzorowania perspektywy
	GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

	gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
	gl.glLoadIdentity();   				//Reset The Modelview Matrix
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
	//
	if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
	    yspeed -= 0.1f;

	} else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
	    yspeed += 0.1f;

	} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
	    xspeed -= 0.1f;

	} else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
	    xspeed += 0.1f;

	} else if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
	    filter += 1;
	    if(filter > 2) {
		filter = 0;
	    }
	}

	//We handled the event
	return true;
    }


    //Zdarzenie wykrywające dotknięcie ekranu
    @Override
    public boolean onTouchEvent(MotionEvent event) {
	//Pobranie pozycji x oraz y z ekranu (wsp. dotknięcia)
	float x = event.getX();
	float y = event.getY();

	//Akcja przesuwania palcem po ekranie bez odrywania go
	if(event.getAction() == MotionEvent.ACTION_MOVE) {
	    //Wyliczanie zmiany
	    float dx = x - oldX;
	    float dy = y - oldY;
	    //Definiowanie górnego obszaru 10% wielkości ekranu
	    int upperArea = this.getHeight() / 10;

	    // Obrót wokół osi
	    xrot += dy * TOUCH_SCALE;
	    yrot += dx * TOUCH_SCALE;


	    //Naciśnięcie na ekran
	} else if(event.getAction() == MotionEvent.ACTION_UP) {
	    //Definiowanie górnego obszaru 10% wielkości ekranu
	    int upperArea = this.getHeight() / 10;
	    int lowerArea = this.getHeight() - upperArea;

	    if(y > lowerArea) {
		if(light) {
		    light = false;
		} else {
		    light = true;
		}
	    }
	}

	//Zapamiętanie wartości
	oldX = x;
	oldY = y;

	//Zajmujemy się tym zdarzeniem
	return true;
    }

    //Geters i seters wartości azimuth, roll, pitch, w celu możliwości wykorzystania tych wartości w innych klasach

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



}