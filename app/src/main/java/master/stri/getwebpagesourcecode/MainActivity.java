package master.stri.getwebpagesourcecode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] type = {"http://", "https://"};
    TextView urltype, html;
    EditText url;
    Button getcodesource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        html = (TextView) findViewById(R.id.sourcecode);
        urltype = (TextView) findViewById(R.id.urlT);
        url = (EditText) findViewById(R.id.urlEntered);
        getcodesource = (Button) findViewById(R.id.getbutton);

        getcodesource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    String str = urltype.getText().toString();
                    String urlS = url.getText().toString();
                    String URL = str + urlS;
                    httptask task = new httptask();
                    task.execute(URL);
                } else {
                    Toast.makeText(MainActivity.this, "Check your internet connection and try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        urltype.setText(type[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        urltype.setText(type[0]);

    }

    //This method actually checks if device is connected to internet
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class httptask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String httpcode = "";
            for (String urL : strings) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urL);
                try {
                    HttpResponse httpResponse;
                    httpResponse = httpClient.execute(httpGet);
                    HttpEntity entity = httpResponse.getEntity();
                    InputStream in = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "windows-1251"), 8);
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        str.append(line + "\n");
                    }
                    httpcode += str.toString();
                    in.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return httpcode;
        }

        @Override
        protected void onPostExecute(String s) {
            html.setText(s);
        }
    }
}