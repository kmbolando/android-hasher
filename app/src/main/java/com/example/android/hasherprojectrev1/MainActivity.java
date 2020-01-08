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
import java.util.zip.Adler32;
import java.util.zip.CRC32;

public class MainActivity extends AppCompatActivity {

    public String AlgoType;
    EditText inputText;
    Button btnSubmit;
    RadioButton r_btnCRC32, r_btnMD5, r_btnSHA1;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Target elements
         */
        inputText = findViewById(R.id.editText1);
        btnSubmit = findViewById(R.id.button1);
        message = findViewById(R.id.textView1);
        r_btnCRC32 = findViewById(R.id.radioButton1);
        r_btnMD5 = findViewById(R.id.radioButton2);
        r_btnSHA1 = findViewById(R.id.radioButton3);
    }

    public void submit(View view) {
        String msgHASHED = null;
        if (inputText.length() != 0) {
            String inputMsg = inputText.getText().toString();
            Log.v("MainActivity", "INPUT: " + inputMsg);
            String type = r_BtnAlgoSTATUS();
            Log.v("MainActivity", "TYPE RETURNED: " + type);
            if (zipFunction(AlgoType)) {
                if (AlgoType.equals("CRC-32")) {
                    msgHASHED = Long.toHexString(hashCRC(inputMsg));
                } else if (AlgoType.equals("adler32")) {
                    msgHASHED = Long.toHexString(hashAdler(inputMsg));
                }
            } else {
                msgHASHED = hash(inputMsg, AlgoType);
            }
            message.setText(msgHASHED);
        }
    }

    public String r_BtnAlgoSTATUS() {
        return AlgoType;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
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
            case R.id.radioButton4:
                if (checked)
                    zipFunction("CRC-32");
                break;
            case R.id.radioButton5:
                if (checked)
                    zipFunction("adler32");
                break;
        }
    }

    public boolean zipFunction(String arg) {
        if (arg.equals("CRC-32")) {
            AlgoType = "CRC-32";
            return true;
        } else if (arg.equals("adler32")) {
            AlgoType = "adler32";
            return true;
        } else {
            return false;
        }
    }

    public String hash(String msgToSHA1, String algorithm) {
        String msgHASHED = null;
        try {
            MessageDigest sha1 = MessageDigest.getInstance(algorithm);
            sha1.update(msgToSHA1.getBytes());
            byte[] bytes = sha1.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            msgHASHED = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return msgHASHED;
    }

    public long hashCRC(String input) {
        CRC32 crc32 = new CRC32();
        crc32.update(input.getBytes());
        return crc32.getValue();
    }

    public long hashAdler(String input) {
        Adler32 adler32 = new Adler32();
        adler32.update(input.getBytes());
        return adler32.getValue();
    }

    public void copy(View v) {
        if (message.length() != 0) {
            ClipboardManager copy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Digest", message.getText().toString());
            if (copy != null) {
                copy.setPrimaryClip(clip);
            } else {
                Log.w("MainActivity", "Clipboard Error. \n Try configuring your clipboard manager.");
            }
        }
    }
}