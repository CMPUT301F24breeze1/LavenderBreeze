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
    to generate a new code:
        QRCodeGenerator qrCode = new QRCodeGenerator("event_ID", yourImageView);
        qrGenerator.generateQRCode();

    to convert to a string so we can store it in database:
        String base64QRCode = qrCode.getQRCodeAsBase64();

    to retrieve and display the QR code from a Base64 string:
        qrGenerator.displayQRCodeFromBase64(base64QRCode);

 */

public class QRCodeGenerator {
    private String code;      // The code to be encoded as a QR code
    private ImageView qrCodeIV; // ImageView to display the generated QR code

    // Constructor that initializes the code and ImageView
    public QRCodeGenerator(String code, ImageView qrCodeIV) {
        this.code = code;
        this.qrCodeIV = qrCodeIV;
    }

    // Method to generate and display the QR code
    public void generateQRCode() {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            // Generate the QR code as a Bitmap image with 512x512 pixels
            Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.QR_CODE, 512, 512);
            qrCodeIV.setImageBitmap(bitmap); // Sets the Bitmap to ImageView
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    // Method to convert a Bitmap to a Base64 string
    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // Compress the Bitmap to PNG format
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT); // Encode the byte array to Base64
    }

    // Method to convert the generated QR code to a Base64 string
    public String getQRCodeAsBase64() {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            // Generate the QR code as a Bitmap image
            Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.QR_CODE, 512, 512);
            return bitmapToBase64(bitmap); // Convert Bitmap to Base64
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to convert a Base64 string back to a Bitmap
    public Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT); // Decode Base64 to byte array
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // Convert byte array to Bitmap
    }

    // Method to display a Base64 encoded QR code in the ImageView
    public void displayQRCodeFromBase64(String base64String) {
        Bitmap bitmap = base64ToBitmap(base64String); // Convert Base64 string to Bitmap
        qrCodeIV.setImageBitmap(bitmap); // Display Bitmap in ImageView
    }
}
