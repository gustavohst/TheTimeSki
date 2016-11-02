package com.gustavotenorio.thetimeski;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ItemVideoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //teste
        ListView lista = (ListView) findViewById(R.id.lista);
        //Recebe array de cidades
        adapter = new ItemVideoAdapter(this, new ArrayList<ItemVideo>());

        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetalheActivity.class);
                ItemVideo cidade = (ItemVideo) parent.getItemAtPosition(position);
                intent.putExtra("CIDADE", cidade);
                startActivity(intent);
            }
        });

        new EventoTask().execute();

    }

    class EventoTask extends AsyncTask<Void, Void, List<ItemVideo>>{

        @Override
        protected List<ItemVideo> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL("https://dl.dropboxusercontent.com/s/7x8vvp1lhel43y9/videos.json");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String linha;
                StringBuffer buffer = new StringBuffer();
                while((linha = reader.readLine()) != null){
                    buffer.append(linha);
                    buffer.append("\n");
                }

                return JsonUtil.fromJson(buffer.toString());

            }catch(Exception e) {
                e.printStackTrace(); // Printar os erros
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return null;
        }

         @Override
        protected void onPostExecute(List<ItemVideo> itemVideos) {
             adapter.clear();
             adapter.addAll(itemVideos);
             adapter.notifyDataSetChanged();
        }


    }
}
