package com.example.finger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;


public class NoteActivity extends AppCompatActivity {

    String notepad;
    Button savebutton, encbutton, decbutton;
    EditText tekst;
    KeyStore keyStore;
    String secret;
    private static final String initVector = "encryptionIntVec";
    IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
    private byte[] enc;
    private String Key_NAME = "keyname";
    String decstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        notepad = settings.getString("notepad", "");
        tekst = (EditText) findViewById(R.id.editText);
        tekst.setText(notepad);

        savebutton = findViewById(R.id.button);
        encbutton = findViewById(R.id.button2);
        decbutton = findViewById(R.id.button3);

        encbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = tekst.getText().toString();
                try {
                    enc = MainActivity.cipher.doFinal(text.getBytes("UTF-8"));
                    secret = Base64.getEncoder().encodeToString(enc);
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }catch(UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }
                tekst.setText(secret);


            }

        });

        decbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                        String text = tekst.getText().toString();
                        keyStore = KeyStore.getInstance("AndroidKeyStore");
                        keyStore.load(null);
                        SecretKey Secretkey = (SecretKey) keyStore.getKey(Key_NAME, null);
                        MainActivity.cipher.init(Cipher.DECRYPT_MODE, Secretkey, iv);
                        final byte[] decoded = MainActivity.cipher.doFinal(Base64.getDecoder().decode(text));
                        decstring = new String(decoded, "UTF-8");
                }catch (Exception e){
                    Log.e("KeyStore", e.getMessage());
                    return;

                }

                tekst.setText(decstring);
            }

        });
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tekst.getText().toString();
                SharedPreferences settings = getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("notepad",text);
                editor.apply();


            }

        });

    }

}