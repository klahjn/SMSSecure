package com.example.user.smssecure;

import android.content.Intent;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.smssecure.backend.SHA1;
import com.example.user.smssecure.backend.Util;
import com.example.user.smssecure.backend.dalva.revariscipher.Revaris;
import com.example.user.smssecure.backend.ecceg.ECCEG;
import com.example.user.smssecure.backend.ecceg.Point;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageActivity extends AppCompatActivity {
    Button decryptButton;
    Button verifyButton;
    TextView sender;
    TextView content;
    TextView status;
    EditText key;
    String msg;
    String publicKeyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        decryptButton = (Button) findViewById(R.id.decrypt_button);
        verifyButton = (Button) findViewById(R.id.verify_button);
        sender = (TextView) findViewById(R.id.sender);
        content = (TextView) findViewById(R.id.content);
        status = (TextView) findViewById(R.id.status);
        key = (EditText) findViewById(R.id.key);
        publicKeyString = "";

        Intent intent = getIntent();
        msg = "";
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
        String pb = key.getText().toString();
        try {
            byte[] cipher = Base64.decode(msg,Base64.DEFAULT);
            byte[] plaintext = Revaris.RevarisDecrypt(cipher,pb);
            content.setText(new String(plaintext));
            System.out.println("decrypted: " + new String(plaintext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void verify(View v){
        String data = (String) content.getText();
        String signature = "";
        String contentMsg = "";
        publicKeyString = key.getText().toString();
        boolean isSigned = false;
        boolean isPublicKey = false;
        Pattern signaturePattern = Pattern.compile("\n\n<ds>(.*?)</ds>");
        Matcher m = signaturePattern.matcher(data);
        while (m.find()) {
            signature = m.group(1);
            isSigned = true;
        }
        String messageDialog = "";
        if(isSigned){
            signaturePattern = Pattern.compile("(.*?)\n\n<ds>.*?</ds>");
            m = signaturePattern.matcher(data);
            while (m.find()) {
                contentMsg = m.group(1);
            }
            signaturePattern = Pattern.compile("(.*?),");
            m = signaturePattern.matcher(publicKeyString);
            String publicKeyX = "";
            String publicKeyY = "";
            if (m.find()) {
                publicKeyX = m.group(1);
            }
            if (m.find()) {
                publicKeyY = m.group(1);
                isPublicKey = true;
            }
            if(isPublicKey){
//                System.out.println("pb: " + publicKeyX + "," + publicKeyY);
                Point publicKey = new Point(new BigInteger(publicKeyX),new BigInteger(publicKeyY));
                String md = SHA1.hashString(contentMsg);
                if(ECCEG.verify(signature,md,publicKey)) {
                    messageDialog = "Message is verified";
                }
                else{
                    messageDialog = "Message is not verified";
                }
            }else{
                messageDialog = "Public key is in wrong format";
            }
        }
        else messageDialog = "Message is not signed";
        status.setText(messageDialog);
    }
    public void browseFile(View v){
        publicKeyString = Util.browseFile();
        key.setText(publicKeyString);
    }
}
