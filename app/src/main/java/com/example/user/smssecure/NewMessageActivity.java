package com.example.user.smssecure;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.smssecure.backend.SHA1;
import com.example.user.smssecure.backend.dalva.revariscipher.Revaris;
import com.example.user.smssecure.backend.ecceg.ECCEG;

import java.math.BigInteger;

public class NewMessageActivity extends AppCompatActivity {
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    EditText privateKey;
    EditText keyEncrypt;
    CheckBox encrypt;
    CheckBox sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        btnSendSMS = (Button) findViewById(R.id.sendbutton);
        txtPhoneNo = (EditText) findViewById(R.id.phone_number);
        txtMessage = (EditText) findViewById(R.id.content);
        privateKey = (EditText) findViewById(R.id.private_key);
        keyEncrypt = (EditText) findViewById(R.id.key_encrypt);
        encrypt = (CheckBox) findViewById(R.id.encryptCheckBox);
        sign = (CheckBox) findViewById(R.id.signCheckBox);
        sign.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if(sign.isChecked()){
                      setPrivateKeyEditTextVisible(view);
                  }
                  else setPrivateKeyEditTextGone(view);
              }
        });

        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sign.isChecked()){
                    setKeyEncryptEditTextVisible(view);
                }
                else setKeyEncryptEditTextGone(view);
            }
        });
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();
                if (phoneNo.length()>0 && message.length()>0) {
                    if(sign.isChecked()) {
                        String privateKeyString = privateKey.getText().toString();
                        System.out.println("Private Key:" + privateKeyString);
                        message = signMessage(message, privateKeyString);
                    }
                    if(encrypt.isChecked()){
                        String key = keyEncrypt.getText().toString();
                        message = encryptMessage(message,key);
                    }
                    sendSMS(phoneNo, message);
                }
                else
                    Toast.makeText(getBaseContext(),
                            "Please enter both phone number and message.",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setPrivateKeyEditTextVisible(View v){
        privateKey.setVisibility(View.VISIBLE);
    }
    public void setKeyEncryptEditTextVisible(View v){
        keyEncrypt.setVisibility(View.VISIBLE);
    }
    public void setPrivateKeyEditTextGone(View v){
        privateKey.setVisibility(View.GONE);
    }
    public void setKeyEncryptEditTextGone(View v){
        keyEncrypt.setVisibility(View.GONE);
    }
    private String signMessage(String message, String privateKey){
        String md = SHA1.hashString(message);
        // TODO: decrypt md using private key and ECC algorithm
        String encrypt = ECCEG.encrypt(md, new BigInteger(privateKey));
        StringBuilder messagesigned = new StringBuilder();
        messagesigned.append(message);
        messagesigned.append("\n\n<ds>");
        messagesigned.append(encrypt);
        messagesigned.append("</ds>");
        return messagesigned.toString();
    }
    private String encryptMessage(String message, String key){
        String encryptedString = null;
        try {
            byte encrypted[] = Revaris.RevarisEncrypt(message.getBytes(), key);
            encryptedString = new String(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }
    private void sendSMS(String phoneNumber, String message){
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, NewMessageActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }
}
