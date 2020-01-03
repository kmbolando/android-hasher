package com.example.android.hasherprojectrev1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    EditText inputText;
    Button btnSubmit;
    RadioButton r_btnCRC32, r_btnMD5, r_btnSHA1;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* target elements
        *
        * >editText1; radioButton1; radioButton2; radioButton3; textView1; button1^X^X
        *
        *  */
        inputText = findViewById(R.id.editText1);
        btnSubmit = findViewById(R.id.button1);
        message = findViewById(R.id.textView1);
        r_btnCRC32 = findViewById(R.id.radioButton1);
        r_btnMD5 = findViewById(R.id.radioButton2);
        r_btnSHA1 = findViewById(R.id.radioButton3);
    }

    public void submit(View view){
        String msgHASHED;
        if(inputText.length()!=0) {
            String inputMsg = inputText.getText().toString();
            Log.v("MainActivity","INPUT: "+ inputMsg);
            String type = r_BtnAlgoSTATUS();
            Log.v("MainActivity","TYPE RETURNED: "+ type);
            msgHASHED = hash(inputMsg, AlgoType);
            message.setText(msgHASHED);
        }

    }
    public String AlgoType;

    public String r_BtnAlgoSTATUS()
    {
        return AlgoType;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    AlgoType = "MD5";
                    break;
            case R.id.radioButton2:
                if (checked)
                    AlgoType = "SHA-1";
                    break;
            case R.id.radioButton3:
                if (checked)
                    AlgoType = "SHA-256";
                    break;
        }
    }

    public String hash(String msgToSHA1, String algorithm)
    {
        String msgHASHED = null;
        try {
            MessageDigest sha1 = MessageDigest.getInstance(algorithm);
            sha1.update(msgToSHA1.getBytes());
            byte[] bytes = sha1.digest();
            //Convert bytes to hexadecimal
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            msgHASHED = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return msgHASHED;
    }

    public void copy(View v){
        if (message.length()!=0) {
            ClipboardManager copy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Digest", message.getText().toString());
            if (copy != null) {
                copy.setPrimaryClip(clip);
            }else{
                Log.w("MainActivity", "Clipboard manager returned null pointer.\nTry assigning a new clipboard manager.");
            }
        }
    }
}