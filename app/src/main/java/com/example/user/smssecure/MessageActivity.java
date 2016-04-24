package com.example.user.smssecure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {
    Button decryptButton;
    Button verifyButton;
    TextView sender;
    TextView content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        decryptButton = (Button) findViewById(R.id.decrypt_button);
        verifyButton = (Button) findViewById(R.id.verify_button);
        sender = (TextView) findViewById(R.id.sender);
        content = (TextView) findViewById(R.id.content);
        sender.setText(savedInstanceState.getString("phone_number"));
        content.setText(savedInstanceState.getString("content"));
    }
}
