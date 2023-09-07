/*
package com.sdsol.mediapicker.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class SendEmail {

    public void sendEmail(){
        File PayslipDir = new java.io.File(Environment.getExternalStorageDirectory(), "/temp/");
        String strFilename = "test.pdf";
        File htmlFile = new java.io.File(PayslipDir, strFilename);

        Uri htmlUri= Uri.parse("file://" + strFile);
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"contact@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pdf attachment");
        if (htmlUri != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM,htmlUri);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi PDF is attached in this mail. ");
        startActivity(Intent.createChooser(emailIntent, "Sending email..."));
    }
}
*/
