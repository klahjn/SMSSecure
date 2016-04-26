package com.example.user.smssecure;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {
    ListView listMessages;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        listMessages = (ListView) findViewById(R.id.listViewInbox);
        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/inbox");

        // List required columns
        String[] reqCols = new String[] { "_id", "address", "body" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);
        // Attached Cursor with adapter and display in listview
        adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                new String[] { "body", "address" }, new int[] {
                R.id.lblMsg, R.id.lblNumber });
        listMessages.setAdapter(adapter);
        listMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView tMsg = (TextView) view.findViewById(R.id.lblMsg);
                TextView tAddress = (TextView) view.findViewById(R.id.lblNumber);
                Intent newIntent = new Intent(InboxActivity.this, MessageActivity.class);
                newIntent.putExtra("msg", tMsg.getText());
                newIntent.putExtra("number", tAddress.getText());
                startActivity(newIntent);
            }
        });
    }
}
