package com.example.finger;


import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.widget.Toast;

public class AuthenticationHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;


    public AuthenticationHandler(Context mContex){

        this.context = mContex;



    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        Toast.makeText(context, "Auth Error: " + errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        Toast.makeText(context, "Auth help: " + helpString, Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Toast.makeText(context, "Auth Succeeded: " , Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, NoteActivity.class);
        context.startActivity(intent);







    }



    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context, "Auth Failed: " , Toast.LENGTH_SHORT).show();
    }


}
