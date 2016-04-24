package com.example.user.smssecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void generateKeyECC(View v){
        Intent intent = new Intent(this, GenerateKeyECCActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void newMessage(View v){
        Intent intent = new Intent(this, NewMessageActivity.class);
        startActivity(intent);
    }

    public void inbox(View v){
        Intent intent = new Intent(this, InboxActivity.class);
        startActivity(intent);
    }

    public void sentMessages(View v){
        Intent intent = new Intent(this, SentMessagesActivity.class);
        startActivity(intent);
    }
}
