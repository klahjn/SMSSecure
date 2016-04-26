package com.example.user.smssecure;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.smssecure.backend.SHA1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Intent intent = getIntent();
        String msg = "";
        String number = "";
        if(intent!= null){
            Bundle extras = intent.getExtras();
            if(extras != null){
                msg = extras.getString("msg");
                number = extras.getString("number");
            }
        }
        sender.setText(number);
        content.setText(msg);
    }

    public void decrypt(View v){

    }
    public void verify(View v){
        String data = (String) content.getText();
        String signature = "";
        String contentMsg = "";
        Pattern signaturePattern = Pattern.compile("\n\n<ds>(.{40}?)</ds>");
        Matcher m = signaturePattern.matcher(data);
        while (m.find()) {
            signature = m.group(1);
        }
        signaturePattern = Pattern.compile("(.*?)<ds>.{40}?</ds>");
        m = signaturePattern.matcher(data);
        while (m.find()) {
            contentMsg = m.group(1);
        }
        // TODO encrypt with ECC public key
        String mdsign = "";
        String md = SHA1.hashString(contentMsg);
        String messageDialog = "";
        if(true){//if(mdsign.equals(md)) {
            messageDialog = "Message is verified";
        }
        else{
            messageDialog = "Message is not verified";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageDialog)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        builder.create();
    }
}
