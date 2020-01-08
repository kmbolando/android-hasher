package com.example.android.hasherprojectrev1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

public class FilesActivity extends AppCompatActivity {

    static final int REQUEST_FILE_GET = 1;
    public String filePath; /* Declared in onActivityResult Method */
    public byte[] fileData = new byte[0];
    public String AlgoType = "MD5"; /* Used in onRadioButtonClicked Method */
    Button btnFile, btnSubmit, btnCopy;

    /*
     * TODO: On button click(listener) -> explicit intent --> ACTION_GET_CONTENT
     * TODO: on result function, return information to a (public) variable
     * TODO: Add file interface. Implement a file intent which outputs a byte array.
     */
    TextView message, gotoString, filename;
    Intent fileIntent, toString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        btnFile = findViewById(R.id.button3);
        btnSubmit = findViewById(R.id.button4);
        btnCopy = findViewById(R.id.button5);
        message = findViewById(R.id.textView5);
        gotoString = findViewById(R.id.textView6);
        filename = findViewById(R.id.textView4);

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("*/*");
                if (fileIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(fileIntent, REQUEST_FILE_GET);
                }
            }
        });

        gotoString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toString = new Intent(FilesActivity.this, MainActivity.class);
                startActivity(toString);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                filePath = data.getData().getPath();
                filename.setText(filePath);
                Uri fileURI = data.getData();
                InputStream iStr = null;
                try {
                    iStr = getContentResolver().openInputStream(fileURI);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fileData = getBytes(iStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, len);
        }
        return bufferOut.toByteArray();
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

    public String hash(byte[] msgToSHA1, String algorithm) {
        String msgHASHED = null;
        try {
            MessageDigest sha1 = MessageDigest.getInstance(algorithm);
            sha1.update(msgToSHA1);
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

    public long hashCRC(byte[] input) {
        CRC32 crc32 = new CRC32();
        crc32.update(input);
        return crc32.getValue();
    }

    public long hashAdler(byte[] input) {
        Adler32 adler32 = new Adler32();
        adler32.update(input);
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

    public void submit(View v) {
        String msgHASHED = null;
        if (fileData.length != 0) {
            if (zipFunction(AlgoType)) {
                if (AlgoType.equals("CRC-32")) {
                    msgHASHED = Long.toHexString(hashCRC(fileData));
                } else if (AlgoType.equals("adler32")) {
                    msgHASHED = Long.toHexString(hashAdler(fileData));
                }
            } else {
                msgHASHED = hash(fileData, AlgoType);
            }
            message.setText(msgHASHED);
        }

    }

}
