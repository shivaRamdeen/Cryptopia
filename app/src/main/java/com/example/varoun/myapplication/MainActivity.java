package com.example.varoun.myapplication;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int[] IMAGES = {R.drawable.bitcoin, R.drawable.ethereum, R.drawable.litecoin, R.drawable.ripple};

    String[] Wallet_TYPE = {"BTC", "ETH", "LTC", "XRP"};

    String[] Wallet_TYPE_OF_ADDRESS = {"1", "2", "3", "4"};

    String[] Wallet_NAME = {"Varoun Wallet", "Wallet From Mining", "Varoun LTC Stash", "Ripple for The Boys"};

    String[] Wallet_ADDRESS = {"112r4JUekDqWcwbaP2hy65qqAQ8xCRsqqv", "0x4055fa29270f001995e4472ed2fed77c86d778ed", "198aMn6ZYAczwrE5NvNTUMyJ5qkfy4g3Hi", "rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"};

    ArrayList<String> Wallet_TYPE_OF_ADDRESS_ARRAY = new ArrayList<String>();
    ArrayList<String> Wallet_NAME_ARRAY = new ArrayList<String>();
    ArrayList<String> Wallet_ADDRESS_ARRAY = new ArrayList<String>();
    ArrayList<String> Wallet_BALANCE = new ArrayList<String>();

    //url for wallet balance api this needs to be tied to each individual wallet
    String url ="https://blockchain.info/q/addressbalance/1EzwoHtiXB4iFwedPr49iywjZn2nnekhoj?confirmations=6";

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int j =0;
        for(int i=0 ; i < IMAGES.length; i++){
            Wallet_TYPE_OF_ADDRESS_ARRAY.add(Wallet_TYPE_OF_ADDRESS[i]);
            Wallet_NAME_ARRAY.add(Wallet_NAME[i]);
            Wallet_ADDRESS_ARRAY.add(Wallet_ADDRESS[i]);
            Wallet_BALANCE.add("1.00");
            /*
            j++;
            if(i==3){
                if(j<20){
                    i=0;
                }
            }
            */
        }
        RequestQueue queue = Volley.newRequestQueue(this);


        ListView listView = (ListView)findViewById(R.id.listView);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        // Instantiate the RequestQueue.


        updateWalletBalance(url,queue,customAdapter,0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new JSONTASK().execute("https://blockchain.info/q/addressbalance/1EzwoHtiXB4iFwedPr49iywjZn2nnekhoj?confirmations=6");
                //Snackbar.make(view, "Figuring it Out", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_address, null);
                final Spinner CoinType = (Spinner) mView.findViewById(R.id.spinner);
                final EditText nameAddress = (EditText) mView.findViewById(R.id.editText);
                final EditText publicAddress = (EditText) mView.findViewById(R.id.editText2);

                mBuilder.setTitle("Add Wallet");
                mBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(!nameAddress.getText().toString().isEmpty() && !publicAddress.getText().toString().isEmpty()){

                            if(CoinType.getSelectedItem().toString().equals("BTC")){
                                Wallet_TYPE_OF_ADDRESS_ARRAY.add("1");
                            }else if(CoinType.getSelectedItem().toString().equals("ETH")){
                                Wallet_TYPE_OF_ADDRESS_ARRAY.add("2");
                            }else if(CoinType.getSelectedItem().toString().equals("LTC")){
                                Wallet_TYPE_OF_ADDRESS_ARRAY.add("3");
                            }else if(CoinType.getSelectedItem().toString().equals("XRP")){
                                Wallet_TYPE_OF_ADDRESS_ARRAY.add("4");
                            }else
                            {
                                //default to btc icon..should never need this if code works
                                Wallet_TYPE_OF_ADDRESS_ARRAY.add("1");
                            }

                            Wallet_NAME_ARRAY.add(nameAddress.getText().toString());
                            Wallet_ADDRESS_ARRAY.add(publicAddress.getText().toString());
                            //add placeholder for wallet balance
                            Wallet_BALANCE.add("0.00");

                            Toast.makeText(MainActivity.this,
                                    R.string.address_added,
                                    Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                        }else{
                            Toast.makeText(MainActivity.this,
                                    R.string.Error_Fields,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
        //get and update wallet balance
        updateWalletBalance(url,queue,customAdapter,0);
        updateWalletBalance(url,queue,customAdapter,1);
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Wallet_ADDRESS_ARRAY.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.custom_layout,null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView_name = (TextView)view.findViewById(R.id.textView_wallet_name);
            TextView textView_address = (TextView)view.findViewById(R.id.textView_wallet_address);
            TextView textView_wallet_balance = (TextView) view.findViewById(R.id.wallet_balance);

            int image_num = Integer.parseInt(Wallet_TYPE_OF_ADDRESS_ARRAY.get(i)) - 1;
            imageView.setImageResource(IMAGES[image_num ]);
            textView_name.setText(Wallet_NAME_ARRAY.get(i));
            textView_address.setText(Wallet_ADDRESS_ARRAY.get(i));
            textView_wallet_balance.setText(Wallet_BALANCE.get(i));
            return view;
        }

    }


    public class JSONTASK extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls)
        {
            HttpURLConnection connection = null;
            BufferedReader reader = null;


            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);

                    return buffer.toString();
                }

                }catch(MalformedURLException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
                }finally{
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            text.setText(result);
        }
    }
    /*
    public class MyDialogFragment extends DialogFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sample_dialog, container, false);
            getDialog().setTitle("Simple Dialog");
            return rootView;
        }
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateWalletBalance(String url, final RequestQueue queue, final CustomAdapter customAdapter, final int Wallet_INDEX)
    {


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //update wallet balance

                        //TODO:calcs based on api used.
                        double response_int = Double.parseDouble(response);
                        double balance_in_btc = response_int*0.00000001;
                        Wallet_BALANCE.set(Wallet_INDEX,Double.toString(balance_in_btc));
                        //notify system to update listview
                        customAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),"Error Contacting API",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
