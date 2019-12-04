package com.example.finger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.EditText;

import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private String Key_NAME = "keyname";
    static byte[] iv;
    static Cipher cipher;
    static KeyGenerator keyGenerator;
    KeyStore keyStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KeyguardManager keygouardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


            if (!fingerprintManager.isHardwareDetected()) {
                Log.e("Hardware", "Finger print hardware not detected");
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission", "Fingerprint permission rejected");
                return;
            }
            if (!keygouardManager.isKeyguardSecure()) {
                Log.e("Keyguard", "Keyguard not enabled");
                return;
            }


            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
            } catch (Exception e) {
                Log.e("KeyStore", e.getMessage());
                return;
            }
            try {
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            } catch (Exception e) {
                Log.e("KeyGenerator", e.getMessage());
                return;
            }
            try {
                keyStore.load(null);
                keyGenerator.init(new KeyGenParameterSpec.Builder(Key_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)

                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build());
                keyGenerator.generateKey();
            } catch (Exception e) {
                Log.e("Generating keys", e.getMessage());
                return;
            }


            try {
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
            } catch (Exception e) {
                Log.e("Cipher", e.getMessage());
                return;
            }

            try {
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(Key_NAME, null);
                cipher.init(Cipher.ENCRYPT_MODE, key);

            } catch (Exception e) {
                Log.e("Secret key", e.getMessage());
            }
            iv = cipher.getIV();
            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

            CancellationSignal cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new AuthenticationHandler(this), null);

    }
}
