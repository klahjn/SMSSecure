package com.example.user.smssecure;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.smssecure.backend.SHA1;
import com.example.user.smssecure.backend.Util;
import com.example.user.smssecure.backend.dalva.revariscipher.Revaris;
import com.example.user.smssecure.backend.ecceg.ECCEG;

import java.math.BigInteger;
import java.util.ArrayList;

public class NewMessageActivity extends AppCompatActivity {
    private static final String ACTION_SMS_SENT = "SMS_SENT";
    private static final String ACTION_SMS_DELIVERED = "SMS_DELIVERED";
    private static final String EXTRA_MESSAGE_PART = "msg_part";
    Button btnSendSMS;
    Button btnBrowseFile;
    EditText txtPhoneNo;
    EditText txtMessage;
    EditText privateKey;
    EditText keyEncrypt;
    CheckBox encrypt;
    CheckBox sign;
    String privateKeyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        btnSendSMS = (Button) findViewById(R.id.sendbutton);
        btnBrowseFile = (Button) findViewById(R.id.browse_button);
        txtPhoneNo = (EditText) findViewById(R.id.phone_number);
        txtMessage = (EditText) findViewById(R.id.content);
        privateKey = (EditText) findViewById(R.id.private_key);
        keyEncrypt = (EditText) findViewById(R.id.key_encrypt);
        encrypt = (CheckBox) findViewById(R.id.encryptCheckBox);
        sign = (CheckBox) findViewById(R.id.signCheckBox);
        privateKeyString = "";

        btnBrowseFile.setVisibility(View.GONE);
        privateKey.setVisibility(View.GONE);
        keyEncrypt.setVisibility(View.GONE);

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
                        privateKeyString = privateKey.getText().toString();
                        if(!privateKeyString.equals("")) {
//                          System.out.println("Private Key:" + privateKeyString);
                            message = signMessage(message, privateKeyString);
                        }
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
        btnBrowseFile.setVisibility(View.VISIBLE);
    }
    public void setKeyEncryptEditTextVisible(View v){
        keyEncrypt.setVisibility(View.VISIBLE);
    }
    public void setPrivateKeyEditTextGone(View v){
        privateKey.setVisibility(View.GONE);
        btnBrowseFile.setVisibility(View.GONE);
    }
    public void setKeyEncryptEditTextGone(View v){
        keyEncrypt.setVisibility(View.GONE);
    }
    public void browseFile(View v){
        String privateKeyString = Util.browseFile();
        privateKey.setText(privateKeyString);
    }
    private String signMessage(String message, String privateKey){
        String md = SHA1.hashString(message);
        String encrypt = ECCEG.encrypt(new BigInteger(md,16), new BigInteger(privateKey));
        StringBuilder messagesigned = new StringBuilder();
        messagesigned.append(message);
        messagesigned.append("\n\n<ds>");
        messagesigned.append(encrypt);
        messagesigned.append("</ds>");
        System.out.println("Message: "+ message + " " + encrypt);
        return messagesigned.toString();
    }
    private String encryptMessage(String message, String key){
        String encryptedString = null;
        try {
            byte encrypted[] = Revaris.RevarisEncrypt(message.getBytes(), key);
            encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);
//            System.out.println("E: "+encryptedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }
    private void sendSMS(String phoneNumber, String message){
//        PendingIntent pi = PendingIntent.getActivity(this, 0,
//                new Intent(this, NewMessageActivity.class), 0);
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, pi, null);
//        System.out.println("Message: "+message);
        Intent iSent, iDel;
        PendingIntent piSent, piDel;

        SmsManager sm = SmsManager.getDefault();
        ArrayList<String> parts = sm.divideMessage(message);
        final int count = parts.size();

        ArrayList<PendingIntent> sentPis = new ArrayList<>(count);
        ArrayList<PendingIntent> delPis = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            iSent = new Intent(ACTION_SMS_SENT)
                    .putExtra(EXTRA_MESSAGE_PART, i);
            piSent = PendingIntent.getBroadcast(this,
                    i,
                    iSent,
                    PendingIntent.FLAG_ONE_SHOT);
            sentPis.add(piSent);

            iDel = new Intent(ACTION_SMS_DELIVERED)
                    .putExtra(EXTRA_MESSAGE_PART, i);
            piDel = PendingIntent.getBroadcast(this,
                    i,
                    iDel,
                    PendingIntent.FLAG_ONE_SHOT);
            delPis.add(piDel);
        }

        sm.sendMultipartTextMessage(phoneNumber, null, parts, sentPis, delPis);
        ContentValues values = new ContentValues();

        values.put("address", phoneNumber);
        values.put("body", message);

        getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/sent"),    values);
    }
}
