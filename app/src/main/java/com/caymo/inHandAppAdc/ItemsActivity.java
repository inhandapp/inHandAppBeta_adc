package com.caymo.inHandAppAdc;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ItemsActivity extends Activity
//implements OnItemClickListener {
implements OnItemClickListener, OnClickListener {

    private eBayURL url;
    private eBayFileIO io;
    
    private TextView titleTextView;
    private EditText searchFieldEditTextView;
    private ListView itemsListView;
    private Button submitButton;

    private static String keywordsString = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        
        io = new eBayFileIO(getApplicationContext());

        searchFieldEditTextView = (EditText) findViewById(R.id.searchFieldEditText);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        itemsListView = (ListView) findViewById(R.id.itemsListView);
        submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(this);
        itemsListView.setOnItemClickListener(this);

        //new DownloadURL().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitButton:
                keywordsString = searchFieldEditTextView.getText().toString();
                new DownloadURL().execute();
                break;
        }
    }

    class DownloadURL extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //io.downloadFile();
            io.downloadFile(keywordsString);
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            Log.d("eBay", "Search results downloaded");
            new ReadURL().execute();
        }
    }
    
    class ReadURL extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            url = io.readFile();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            Log.d("eBay", "Search results read");
            
            // update the display for the activity
            ItemsActivity.this.updateDisplay();
        }
    }
    
    public void updateDisplay()
    {
        if (url == null) {
            titleTextView.setText("Unable to get eBay search results");
            return;
        }

        // set the title for the url
        //titleTextView.setText(url.getTitle());
        
        // get the items for the url
        ArrayList<eBayItem> items = url.getAllItems();

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = 
                new ArrayList<HashMap<String, String>>();
        for (eBayItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", item.getTitle());
            map.put("currentPrice", item.getPrice());
            data.add(map);
        }
        
        // create the resource, from, and to variables 
        int resource = R.layout.listview_item;
        String[] from = {"title", "currentPrice"};
        int[] to = {R.id.titleTextView, R.id.currentPriceTextView};

        // create and set the adapter
        SimpleAdapter adapter = 
            new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
        
        Log.d("eBay", "Search results displayed");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, 
            int position, long id) {

        // get the item at the specified position
        eBayItem item = url.getItem(position);

        // create an intent
        Intent intent = new Intent(this, ItemActivity.class);

        intent.putExtra("title", item.getTitle());
        intent.putExtra("currentPrice", item.getPrice());
        intent.putExtra("link", item.getLink());

        this.startActivity(intent);
    }


}