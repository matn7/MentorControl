package com.agh.student.mateuszn.mati12.mentorcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {
    /*Ekran powitania ma się wyświetlać przez 1 sekundę po czym nastąpi przejście do aktywności menu głównego.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	//Wygląd interfejsu użytkownika
	setContentView(R.layout.splash);

	//Nowa zmienna timer jako nowy wątek
	Thread timer = new Thread(){
	    public void run() {
		try {
		    //Przez 1 sekundę ma się wyświetlać ekran powitania Splash
		    sleep(1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} finally {
		    //Po upływie tego czasu ma nastąpi przejście do nowej aktywności MENU
		    Intent openStartingPoint = new Intent("com.agh.student.mateuszn.mati12.mentorcontrol.MENU");
		    startActivity(openStartingPoint);
		}
	    }
	};
	//Uruchomienie wątku
	timer.start();
    }
}
