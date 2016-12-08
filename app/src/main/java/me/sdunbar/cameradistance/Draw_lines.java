package me.sdunbar.cameradistance;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Draw_lines extends Activity implements OnTouchListener, View.OnClickListener {
    ImageView imageView;
    Canvas canvas;
    Paint paint, textPaint;
    float downx = 0, downy = 0, upx = 0, upy = 0;
    Intent intent;
    Bitmap thePic;
    Button capture;
    Uri picUri;
    static final String EXTRA_MESSAGE  =  "me.sdunbar.intenttest.myKey";
    EditText et;
    File file;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_lines);

        imageView = (ImageView) this.findViewById(R.id.picture);
        et = (EditText) this.findViewById(R.id.editText);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        int dw = currentDisplay.getWidth();
        int dh = currentDisplay.getHeight();

        intent = getIntent();
        capture = (Button)findViewById(R.id.finBtn);

        Bundle extras = intent.getExtras();
        picUri = Uri.parse(extras.getString(EXTRA_MESSAGE));

        try {
            InputStream image_stream = getContentResolver().openInputStream(picUri);
            thePic = BitmapFactory.decodeStream(image_stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        thePic = getResizedBitmap(thePic, dw, dh);

        canvas = new Canvas();
        canvas.setBitmap(thePic);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(7.0f);
        paint.setTextSize(20);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStrokeWidth(7.0f);
        textPaint.setTextSize(50);

        imageView.setImageBitmap(thePic);

        imageView.setOnTouchListener(this);
        capture.setOnClickListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                fillArrow(canvas, downx, downy, upx, upy);

                float textx = (downx + upx) / 2;
                float texty = (downy + upy) / 2;

                canvas.drawText(et.getText().toString(), textx, texty, textPaint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.finBtn) {
            // Here, thisActivity is the current activity
            FileOutputStream out = null;
            String path = Environment.getExternalStorageDirectory().toString();
            try {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "CameraDemo");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = timeStamp;

                file = new File(mediaStorageDir.getPath() + File.separator +
                        imageFileName + "_lines.png");

                out = new FileOutputStream(file);
                thePic.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Intent result = new Intent();

            picUri = Uri.parse(file.toURI().toString());
            result.putExtra(EXTRA_MESSAGE, picUri.toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

    private void fillArrow(Canvas canvas, float x0, float y0, float x1, float y1) {

        paint.setStyle(Paint.Style.FILL);

        float deltaX = x1 - x0;
        float deltaY = y1 - y0;
        double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        float frac = (float) (1 / (distance / 30));

        float point_x_1 = x0 + ((1 - frac) * deltaX + frac * deltaY);
        float point_y_1 = y0 + ((1 - frac) * deltaY - frac * deltaX);

        float point_x_2 = x1;
        float point_y_2 = y1;

        float point_x_3 = x0 + ((1 - frac) * deltaX - frac * deltaY);
        float point_y_3 = y0 + ((1 - frac) * deltaY + frac * deltaX);

        float x_4 = x0 - deltaX + ((1 - frac) * deltaX + frac * deltaY);
        float y_4 = y0 - deltaY + ((1 - frac) * deltaY - frac * deltaX);
        float point_x_4 = x0 + (x0 - x_4);
        float point_y_4 = y0 + (y0 - y_4);

        float point_x_5 = x0;
        float point_y_5 = y0;

        float x_6 = x0 - deltaX + ((1 - frac) * deltaX - frac * deltaY);
        float y_6 = y0 - deltaY + ((1 - frac) * deltaY + frac * deltaX);
        float point_x_6 = x0 + (x0 - x_6);
        float point_y_6 = y0 + (y0 - y_6);

        Path path = new Path();
        Path path2 = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path2.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(point_x_1, point_y_1);
        path.lineTo(point_x_2, point_y_2);
        path.lineTo(point_x_3, point_y_3);
        path.close();

        path2.moveTo(point_x_6,point_y_6);
        path2.lineTo(point_x_5,point_y_5);
        path2.lineTo(point_x_4, point_y_4);
        path2.close();

        canvas.drawPath(path, paint);
        canvas.drawPath(path2, paint);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int maxHeight, int maxWidth) {

        float scale = Math.min(((float)maxHeight / bm.getWidth()), ((float)maxWidth / bm.getHeight()));

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }
}
