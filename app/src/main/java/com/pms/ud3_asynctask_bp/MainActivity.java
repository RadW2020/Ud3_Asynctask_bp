package com.pms.ud3_asynctask_bp;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Declara una barra de progreso
    ProgressBar progressBar;
    TareaAsincrona tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referencia al la barra de progreso de la interfz
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        tarea = new TareaAsincrona();
        //Forzar la orientación a vertical ante rotaciones del dispositivo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

//Simula una tarea que dura 1 segundo
private void tareaSegundo(){

    try{
        Thread.sleep(1000);
    }catch(InterruptedException e){
        System.out.println("ERROR: " + e.getMessage());
    }
}

    // onClick botón Iniciar Tarea
    public void iniciaTarea(View v){
        /*********¡¡¡OJO!! hay que controlar que no hay una tarea ya iniciada,
         *  en otro caso aborta programa
         */
        if ( (tarea.getStatus().equals(AsyncTask.Status.PENDING)))
            tarea.execute();

     //lanza la tarea sin parámetros, pues doInBackground no recibe ningún parámetro

    }

    // onClick botón cancelar Tarea
    public void cancelaTarea(View v){
        //cancela la tarea
        /**** ¡¡OJO!! Hay que controlar que esté en ejecución, en otro caso aborta el programa
         *
         */
        if ( tarea.getStatus().equals(AsyncTask.Status.RUNNING))
            tarea.cancel(true);
            tarea.onPreExecute();


    }

    //********Clase que hereda de AsyncTask para gestionar tareas asíncronas*********
    // Primer parámetro --- tipo recibido por doInBackground
    //Segundo parámetro --- tipo recibido por onProgressUpdate() y usado por publishProgress()
    //Tercer parámetro --- tipo devuelto por doInBackground y recibido por onPostExecute

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean>{


       //1º -Se ejecuta en le hilo UI y antes del método doInBackground

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setMax(100); //valor máximo
            progressBar.setProgress(0); //inicialización
        }

        // 2º- Se ejecuta en un hilo secundario y debe contener el código pesado
        // Puede ejecutar el método publissProgres() para comunicar el progreso de la tarea.
        // Ese progreso lo comunica a onProgressUpdate
        //Recibe un parámtro del primer tipo y devuelve un parámetro del tercer tipo
        @Override
        protected Boolean doInBackground(Void... nada) {

            for(int i=1; i<=10;i++){
                tareaSegundo();
                //comunicar el progreso al hilo UI mediante la llamada a onProgressUpdate
                publishProgress(i*10);
                //comprueba si el usuario cancela la tarea
                if(isCancelled())
                    return false;

            }


            return true;
        }

        //3º- Se ejecuta en el hilo UI y es llamado cuando se ejecuta el método publishProgress()
        //Recibe un parámetro del segundo tipo, que es progreso que le manda publishProgresss()
        @Override
        protected void onProgressUpdate(Integer... vi) {
            super.onProgressUpdate(vi);
            //actualiza el progreso de la progressbar con el valor de publishProgress
            progressBar.setProgress(vi[0]);
        }
        //4º Se ejecuta en hilo UI tras finalizar la ejecución de doInBackground
        // Recibe un parámtro del tercer tipo, que es el valor devuelto por doInBackground
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado)
                Toast.makeText(getApplicationContext(),
                        "Tarea larga completada",Toast.LENGTH_SHORT).show();
        }

        // Se ejecuta cuando la tarea asíncrona es cancelada con cancel(true).
        //En este caso no se ejecuta onPostExecute
        @Override
        protected void onCancelled(Boolean cancelado) {
           if(!cancelado)//(cancelado == false
                Toast.makeText(getApplicationContext(),
                        "Tarea larga cancelada",Toast.LENGTH_SHORT).show();
        }
    }
}
