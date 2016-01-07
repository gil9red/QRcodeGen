package com.example.ipetrash.qrcodegen;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "qrcodegen";
    private EditText mEditText;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d(TAG, String.format("beforeTextChanged=%s start=%s count=%s after=%s", s, start, count, after));
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.d(TAG, String.format("onTextChanged=%s start=%s before=%s count=%s", s, start, before, count));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d(TAG, String.format("afterTextChanged=%s text=%s", s, s.toString()));

                        // TODO: по окончанию ввода, по одиночному таймеру генерировать Qr Code

                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        int size = Math.min(mImageView.getWidth(), mImageView.getHeight());
                        Log.d(TAG, String.format("mImageView width=%s height=%s", mImageView.getWidth(), mImageView.getHeight()));
                        Log.d(TAG, String.format("QrCode size=%s", size));

                        try {
                            Log.d(TAG, "before gen");
                            long t = System.currentTimeMillis();
                            BitMatrix bitMatrix = qrCodeWriter.encode(String.valueOf(s), BarcodeFormat.QR_CODE, size, size);
                            Log.d(TAG, String.format("after gen. Elapsed time %s ms.", System.currentTimeMillis() - t));

                            // TODO: конвертирование занимает больше всего времени: например, генерация 3 мс, а конвертирование 600 мс,
                            Log.d(TAG, "before bitMatrix -> Bitmap");
                            t = System.currentTimeMillis();
                            int height = bitMatrix.getHeight();
                            int width = bitMatrix.getWidth();
                            Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            for (int x = 0; x < width; x++){
                                for (int y = 0; y < height; y++){
                                    bm.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                                }
                            }
                            Log.d(TAG, String.format("after bitMatrix -> Bitmap. Elapsed time %s ms.", System.currentTimeMillis() - t));

                            mImageView.setImageBitmap(bm);

                        } catch (WriterException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }
        );
    }
}
