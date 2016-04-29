package com.example.user.smssecure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.smssecure.backend.SHA1;
import com.example.user.smssecure.backend.dalva.revariscipher.Revaris;
import com.example.user.smssecure.backend.ecceg.ECCEG;

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
        String pb = (String) content.getText();
        try {
            byte[] plaintext = Revaris.RevarisDecrypt(msg.getBytes(),pb);
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
        String publicKeyString = String.valueOf(key.getText());
        boolean isSigned = false;
        boolean isPublicKey = false;
        Pattern signaturePattern = Pattern.compile("\n\n<ds>(.{40}?)</ds>");
        Matcher m = signaturePattern.matcher(data);
        while (m.find()) {
            signature = m.group(1);
            isSigned = true;
        }
        String messageDialog = "";
        if(isSigned){
            signaturePattern = Pattern.compile("(.*?)\n\n<ds>.{40}?</ds>");
            m = signaturePattern.matcher(data);
            while (m.find()) {
                contentMsg = m.group(1);
            }
            signaturePattern = Pattern.compile("(.*?),(.*?)");
            m = signaturePattern.matcher(publicKeyString);
            String publicKeyX = "";
            String publicKeyY = "";
            while (m.find()) {
                publicKeyX = m.group(1);
                publicKeyY = m.group(2);
                isPublicKey = true;
            }

            if(isPublicKey){
                // TODO encrypt contentMsg with ECC public key
                String mdsign = ECCEG.decrypt(signature, new BigInteger(publicKeyX));
                String md = SHA1.hashString(contentMsg);
                if(mdsign.equals(md)) {
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
}
