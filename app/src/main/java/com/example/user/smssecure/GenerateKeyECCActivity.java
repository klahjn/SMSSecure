package com.example.user.smssecure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.smssecure.backend.Util;
import com.example.user.smssecure.backend.ecceg.ECCEG;
import com.example.user.smssecure.backend.ecceg.EllipticCurve;
import com.example.user.smssecure.backend.ecceg.Point;

import java.math.BigInteger;

public class GenerateKeyECCActivity extends AppCompatActivity {
    TextView privateKeyTxt;
    TextView publicKeyTxt;
    TextView privateKeyTxtView;
    TextView publicKeyTxtView;
    EditText filename;
    TextView status;
    Point publicKey;
    BigInteger privateKey;
    Button saveAs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key_ecc);
        privateKeyTxt = (TextView) findViewById(R.id.private_key);
        publicKeyTxt = (TextView) findViewById(R.id.public_key);
        privateKeyTxtView = (TextView) findViewById(R.id.private_key_textView);
        publicKeyTxtView = (TextView) findViewById(R.id.public_key_textView);
        filename = (EditText) findViewById(R.id.file_name);
        status = (TextView) findViewById(R.id.status);
        saveAs = (Button) findViewById(R.id.save_key);
        privateKeyTxtView.setVisibility(View.GONE);
        publicKeyTxtView.setVisibility(View.GONE);
        filename.setVisibility(View.GONE);
        saveAs.setVisibility(View.GONE);
    }
    public void generateKeyECC(View v){
        EllipticCurve ec = new EllipticCurve(new BigInteger("-3"), new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16), new BigInteger("6277101735386680763835789423207666416083908700390324961279"));
        ec.setOrder(new BigInteger("6277101735386680763835789423176059013767194773182842284081"));
        Point b = new Point(new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16), new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16));
        ec.setBase(b);
//        System.out.println("base " + b);

        privateKey = ECCEG.generatePrivateKey(ec);
        publicKey = ECCEG.generatePublicKey(ec,privateKey);
        publicKeyTxtView.setVisibility(View.VISIBLE);
        privateKeyTxtView.setVisibility(View.VISIBLE);
        saveAs.setVisibility(View.VISIBLE);
        filename.setVisibility(View.VISIBLE);
        privateKeyTxt.setText("" + privateKey);
        publicKeyTxt.setText(publicKey.getX() + "," + publicKey.getY());
    }
    public void saveKey(View v){
        String fname = filename.getText().toString();
        String statusString = "";
        if(!fname.equals(""))
            if(Util.isExternalStorageWritable()){
                Util.saveFile("" + ".pri", privateKey.toString());
                Util.saveFile("" + ".pub", publicKey.getX() + "," + publicKey.getY() + ",");
                statusString = "Key is saved";
            }
            else statusString = "Key is not saved";
        else
            statusString = "No filename inserted";
        status.setText(statusString);
    }
}

