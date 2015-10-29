package com.agh.student.mateuszn.mati12.mentorcontrol;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Menu extends ListActivity {

    //Tablica zmiennych typu String zawierająca nazwy klas
    String classes[] = {"NSWE","Run"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	//Odczytanie wcześniejszych stanów aplikacji
	super.onCreate(savedInstanceState);
	//Przedstawienie listy w interfejsie uzytkownika
	setListAdapter(new ArrayAdapter<String>(Menu.this, android.R.layout.simple_list_item_1, classes));
    }

    //Zdefiniowanie metody obsługującej naciśnięcie odpowiedniego miejsca na liście.
    //Protected oznacza że metoda jest dostępna w całej paczce com.agh.student.mateuszn.mati12.mentorcontrol
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

	super.onListItemClick(l, v, position, id);

	//Zmienna typu string reprezentująca element 0 - NSWE, 1 - Run
	String cheese = classes[position];

	try {
	    //Wybieranie klasy
	    Class ourClass = Class.forName("com.agh.student.mateuszn.mati12.mentorcontrol."+ cheese);

	    //Inicjalizacja nowej aktywnosci którą jest nazwa klasy.
	    Intent ourIntent = new Intent(Menu.this, ourClass);
	    //Rozpoczęcie nowej aktywności
	    startActivity(ourIntent);

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }

}
