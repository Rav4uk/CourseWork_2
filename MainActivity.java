package com.example.raven.autocamera;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {

    private String data;
    private EditText ip_address;
    private TextView check;
    private TextView inet;
    private TextView profile_status;
    int port = 5555;
    private final String title = "title";
    private final String description = "description";
    Boolean stream_flag_start = true;
    Boolean stream_well = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip_address = (EditText) findViewById(R.id.address);
        check = (TextView) findViewById(R.id.check_view);
        inet = (TextView) findViewById(R.id.inet);
        profile_status = (TextView) findViewById(R.id.profile_status);

        final ListView p_list = (ListView) findViewById(R.id.profile_list);
        ArrayList<HashMap<String, Object>> profile_list = new ArrayList<>();

        add_profile(profile_list, "240p", "360*240");
        add_profile(profile_list,"360p","480*360");
        add_profile(profile_list,"480p","720*480");
        add_profile(profile_list,"720p","1280*720");

        SimpleAdapter adapter = new SimpleAdapter(this, profile_list,
                R.layout.profile, new String[]{title, description},
                new int[]{R.id.text1, R.id.text2});

        p_list.setAdapter(adapter);
        p_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, Object> profile = (HashMap<String,Object>) parent.getItemAtPosition(position);
                profile_status.setText(profile.get(title).toString());
                p_list.setVisibility(View.INVISIBLE);
            }
        });


    }

    void add_profile (ArrayList<HashMap<String, Object>> profile_list, String title_str, String description_str) {
        HashMap<String, Object> profile = new HashMap<>();
        profile.put(title, title_str);
        profile.put(description, description_str);
        profile_list.add(profile);
    }

    public void on_profile_click (View v) {
        if (findViewById(R.id.profile_list).getVisibility() == View.INVISIBLE)
            findViewById(R.id.profile_list).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.profile_list).setVisibility(View.INVISIBLE);
    }
    
    public void onClick(View v) {
        String ip = ip_address.getText().toString();

        if (v==findViewById(R.id.Inet_button)) {
            inet.setText("Loading...");
            data="2";
            MyClientTask myClientTask = new MyClientTask(ip, port, data,check);
            myClientTask.execute();
        }
        else {
            check.setText("Loading...");
            if (stream_well) {
                if (stream_flag_start) {
                    if (v == findViewById(R.id.stop_button))
                        check.setText("There is no stream");
                    else {
                        data = profile_status.getText().toString();
                        MyClientTask myClientTask = new MyClientTask(ip, port, data, check);
                        myClientTask.execute();
                        stream_flag_start = false;

                    }
                } else {
                    if (v == findViewById(R.id.start_button)) {
                        check.setText("Stream's already started");
                    } else {
                        data = "3";
                        MyClientTask myClientTask = new MyClientTask(ip, port, data, check);
                        myClientTask.execute();
                        stream_flag_start = true;
                    }
                }
            }
            else
            {
                check.setText("Press I-net to set connection");
            }
        }
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String data;
        TextView check;
        String response = "";
        MyClientTask(String addr, int port, String message, TextView ch) {
            dstAddress = addr;
            dstPort = port;
            data = message;
            check = ch;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(dstAddress, port), 1000);
                socket.getOutputStream().write(data.getBytes());
                
                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                stream_well = true;

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                stream_well = false;
                response = "There is no such IP";
                        //UnknownHostException
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                stream_well = false;
                response = "Connection failed";
                //IOException
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (data.equals("2")) {
                inet.setText(response);
            }
            else {
                check.setText(response);
            }
            super.onPostExecute(result);
        }
    }
}
