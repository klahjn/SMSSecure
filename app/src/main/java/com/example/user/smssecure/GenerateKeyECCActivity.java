package com.example.user.smssecure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.smssecure.backend.ecceg.ECCEG;
import com.example.user.smssecure.backend.ecceg.EllipticCurve;
import com.example.user.smssecure.backend.ecceg.Point;

import java.math.BigInteger;

public class GenerateKeyECCActivity extends AppCompatActivity {
    TextView privateKeyTxt;
    TextView publicKeyTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key_ecc);
        privateKeyTxt = (TextView) findViewById(R.id.private_key);
        publicKeyTxt = (TextView) findViewById(R.id.public_key);
    }
    public void generateKeyECC(View v){
        EllipticCurve ec = new EllipticCurve(new BigInteger("-1"), new BigInteger("188"), new BigInteger("98764321261"));
        Point b = ec.solveForY(new BigInteger("11"), new BigInteger("20"));
        ec.setBase(b);
        System.out.println("base " + b);

        BigInteger privateKey = ECCEG.generatePrivateKey(ec);
        Point publicKey = ECCEG.generatePublicKey(ec,privateKey);
        privateKeyTxt.setText("Private Key :" + privateKey);
        publicKeyTxt.setText("Public Key :" + publicKey.getX() + ", " + publicKey.getY());
    }
}
