package com.agh.student.mateuszn.mati12.mentorcontrol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {
    private FloatBuffer vertexBuffer;  // Buffer for vertex-array
    private int numFaces = 6;

    private float[][] colors = {  // Kolory ścian
	    {0.0f, 1.0f, 0.0f, 1.0f},  // 0. Zielony
	    {1.0f, 0.0f, 1.0f, 1.0f},  // 1. Fioletowy
	    {0.0f, 0.0f, 1.0f, 1.0f},  // 2. Niebieski
	    {1.0f, 0.0f, 0.0f, 1.0f},  // 3. Czerwony
	    {1.0f, 1.0f, 0.0f, 1.0f},  // 4. Żółty
	    {1.0f, 0.5f, 0.0f, 1.0f}   // 5. Pomarańczowy
    };

    private float[] vertices = {  // Wierzchołki 6 ścian
	    // Przód
	    -1.0f, -1.0f,  1.0f,  // 0. lewy-dolny-przód
	    1.0f, -1.0f,  1.0f,  // 1. prawy-dolny-przód
	    -1.0f,  1.0f,  1.0f,  // 2. lewy-górny-przód
	    1.0f,  1.0f,  1.0f,  // 3. prawy-górny-przód
	    // Tył
	    1.0f, -1.0f, -1.0f,  // 6. prawy-dolny-tył
	    -1.0f, -1.0f, -1.0f,  // 4. lewy-dolny-tył
	    1.0f,  1.0f, -1.0f,  // 7. prawy-górny-tył
	    -1.0f,  1.0f, -1.0f,  // 5. lewy-górny-tył
	    // Lewa
	    -1.0f, -1.0f, -1.0f,  // 4. lewy-dolny-tył
	    -1.0f, -1.0f,  1.0f,  // 0. lewy-dolny-przód
	    -1.0f,  1.0f, -1.0f,  // 5. lewy-górny-tył
	    -1.0f,  1.0f,  1.0f,  // 2. lewy-górny-przód
	    // Prawa
	    1.0f, -1.0f,  1.0f,  // 1. prawy-dolny-przód
	    1.0f, -1.0f, -1.0f,  // 6. prawy-dolny-tył
	    1.0f,  1.0f,  1.0f,  // 3. prawy-górny-przód
	    1.0f,  1.0f, -1.0f,  // 7. prawy-górny-tył
	    // Góra
	    -1.0f,  1.0f,  1.0f,  // 2. lewy-górny-przód
	    1.0f,  1.0f,  1.0f,  // 3. prawy-górny-przód
	    -1.0f,  1.0f, -1.0f,  // 5. lewy-górny-tył
	    1.0f,  1.0f, -1.0f,  // 7. prawy-górny-tył
	    // Dół
	    -1.0f, -1.0f, -1.0f,  // 4. lewy-dolny-tył
	    1.0f, -1.0f, -1.0f,  // 6. prawy-dolny-tył
	    -1.0f, -1.0f,  1.0f,  // 0. lewy-dolny-przód
	    1.0f, -1.0f,  1.0f   // 1. prawy-dolny-przód
    };

    // Konstruktor
    public Cube() {
	ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4); //Lokowanie pamięci 4 bajtów
	vbb.order(ByteOrder.nativeOrder()); // Ustawienie kolejności bajtów
	vertexBuffer = vbb.asFloatBuffer(); // Konwersja z bajtów na Float
	vertexBuffer.put(vertices);         // Kopiowanie danych w buforze
	vertexBuffer.position(0);           // Przewijanie
    }

    // Rysowanie kształtów
    public void draw(GL10 gl) {
	gl.glFrontFace(GL10.GL_CCW);    // Przód w orientacji lewoskrętnej
	gl.glCullFace(GL10.GL_BACK);    // Nie wyświetlaj tyłu

	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

	// Pętla wykonująca rysowanie kostki
	for (int face = 0; face < numFaces; face++) {
	    // Zdefiniowanie kolorów dla każdej ściany
	    gl.glColor4f(colors[face][0], colors[face][1], colors[face][2], colors[face][3]);
	    // Rysowanie kształtu z tablicy wierzchołków
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, face*4, 4);
	}
	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	gl.glDisable(GL10.GL_CULL_FACE);
    }
}
