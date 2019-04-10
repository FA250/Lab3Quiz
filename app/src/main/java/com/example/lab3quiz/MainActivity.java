package com.example.lab3quiz;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    String[] NombresPersonajes;
    Bitmap[] ImagenesPersonajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ObtenerDatos();
    }

    public void ObtenerDatos(){
        DownloadTask downloadTask = new DownloadTask();
        Document result = null;
        try {
            result = downloadTask.execute("https://www.gamedesigning.org/gaming/characters/").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Log.i("Result", result);

        if(result!=null){
            Document doc= result;

            Elements personajes=doc.select("div .site-inner h2");

            Elements imagenes=doc.select("div .site-inner p noscript img");


            NombresPersonajes = new String[personajes.size()];
            ImagenesPersonajes = new Bitmap[imagenes.size()];

            int i=0;
            for (Element personaje:personajes) {
                NombresPersonajes[i]=personaje.text();

                ImageDownloader imageDownloader = new ImageDownloader();
                try {
                    Bitmap image = imageDownloader.execute(imagenes.get(i).attr("src")).get();
                    ImagenesPersonajes[i]=image;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                i++;
            }

            NuevaPregunta();
        }
        else
            Toast.makeText(this,"No se pudo obtener los datos necesarios",Toast.LENGTH_LONG).show();
    }

    public void NuevaPregunta(){
        Random random=new Random();
        ImageView imageView= findViewById(R.id.imagenPregunta);
        Button btnOpcion1=findViewById(R.id.btnOpcion1);
        Button btnOpcion2=findViewById(R.id.btnOpcion2);
        Button btnOpcion3=findViewById(R.id.btnOpcion3);
        Button btnOpcion4=findViewById(R.id.btnOpcion4);


        int numPregunta= random.nextInt((NombresPersonajes.length-1)+1)+1;
        int numBoton=random.nextInt(4);

        Log.i("Cant personajes: ", String.valueOf(NombresPersonajes.length));
        Log.i("Cant imagenes: ", String.valueOf(ImagenesPersonajes.length));

        Log.i("Numero ramdom: ", String.valueOf(numPregunta));
        //Log.i("Personaje img: ", NombresPersonajes[0]);
        //Log.i("Personaje img: ", NombresPersonajes[49]);
        imageView.setImageBitmap(ImagenesPersonajes[numPregunta]);

        numPregunta--;

        if(numBoton==0){
            btnOpcion1.setText(NombresPersonajes[numPregunta]);
            btnOpcion2.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion3.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion4.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
        }
        else if(numBoton==1){
            btnOpcion1.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion2.setText(NombresPersonajes[numPregunta]);
            btnOpcion3.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion4.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
        }
        else if(numBoton==2){
            btnOpcion1.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion2.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion3.setText(NombresPersonajes[numPregunta]);
            btnOpcion4.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
        }
        else if(numBoton==3){
            btnOpcion1.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion2.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion3.setText(NombresPersonajes[random.nextInt(NombresPersonajes.length)]);
            btnOpcion4.setText(NombresPersonajes[numPregunta]);
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {


            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            try {
                //Toast.makeText(getApplicationContext(),"Cargando página...", Toast.LENGTH_LONG).show();

                Document document = Jsoup.connect(urls[0]).get();
                return document;
            }
            catch (Exception e){
                e.printStackTrace();
                Log.i("Error descarga:",e.toString());
            }
            return null;
        }
    }
}
