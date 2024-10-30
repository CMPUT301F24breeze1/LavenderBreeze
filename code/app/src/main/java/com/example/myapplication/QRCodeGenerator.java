package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

/*

 */

public class QRCodeGenerator {
    private final String code;      // Code to be encoded as a QR code
    private ImageView qrCodeIV;     // Optional ImageView for displaying the QR code

    // Constructor that initializes only the code (optional ImageView can be set later)
    public QRCodeGenerator(String code) {
        this.code = code;
    }

    // Optional method to set ImageView after creating the instance
    public void setQrCodeImageView(ImageView qrCodeIV) {
        this.qrCodeIV = qrCodeIV;
    }

    // Method to generate the QR code as a Bitmap
    public Bitmap generateQRCodeBitmap() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(code, BarcodeFormat.QR_CODE, 512, 512);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to display the QR code Bitmap in ImageView if set
    public void displayQRCode() {
        if (qrCodeIV != null) {
            Bitmap bitmap = generateQRCodeBitmap();
            qrCodeIV.setImageBitmap(bitmap);
        }
    }

    // Convert Bitmap to Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Convert Base64 to Bitmap
    private Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    // Generate the QR code and return as Base64 encoded string
    public String getQRCodeAsBase64() {
        Bitmap bitmap = generateQRCodeBitmap();
        return bitmap != null ? bitmapToBase64(bitmap) : null;
    }

    // Display a Base64 encoded QR code in the ImageView if set
    public void displayQRCodeFromBase64(String base64String) {
        if (qrCodeIV != null) {
            Bitmap bitmap = base64ToBitmap(base64String);
            qrCodeIV.setImageBitmap(bitmap);
        }
    }
}
