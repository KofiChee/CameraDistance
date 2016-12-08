package me.sdunbar.cameradistance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    final int CAMERA_CAPTURE = 1;
    public Uri picUri;
    static final int APP_REQUEST = 69;
    static final String EXTRA_MESSAGE  =  "me.sdunbar.intenttest.myKey";
    Bitmap thePic;
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //retrieve a reference to the UI button
        Button captureBtn = (Button)findViewById(R.id.capture_btn);
        //handle button clicks
        captureBtn.setOnClickListener(this);
        mImageView = (ImageView)findViewById(R.id.picture);

        request_permissions();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.capture_btn) {
            try {
                //use standard intent to capture an image
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                picUri = Uri.fromFile(getOutputMediaFile());
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                // captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                //we will handle the returned data in onActivityResult
                startActivityForResult(captureIntent, CAMERA_CAPTURE);
            }
            catch(ActivityNotFoundException anfe){
                //display an error message
                String errorMessage = "Whoops - your device doesn't support capturing images!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void request_permissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            }
        }
    }

    private File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
               Environment.DIRECTORY_PICTURES), "CameraDemo");


        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;

        return new File(mediaStorageDir.getPath() + File.separator +
                imageFileName + ".png");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if(requestCode == CAMERA_CAPTURE){
                //Bitmap thePic = extras.getParcelable("data");

                //Bitmap thePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);

                try {
                    InputStream image_stream = getContentResolver().openInputStream(picUri);
                    thePic = BitmapFactory.decodeStream(image_stream );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                //retrieve a reference to the ImageView
                ImageView picView = (ImageView)findViewById(R.id.picture);
                //display the returned image
                picView.setImageBitmap(thePic);

                //start intent to draw lines
                Intent intent = new Intent(this, Draw_lines.class);
                intent.putExtra(EXTRA_MESSAGE, picUri.toString());
                startActivityForResult(intent, APP_REQUEST);
            }
            else if (requestCode == APP_REQUEST){
                Bundle extras = data.getExtras();
                Uri picUri = Uri.parse(extras.getString(EXTRA_MESSAGE));

                try {
                    InputStream image_stream = getContentResolver().openInputStream(picUri);
                    thePic = BitmapFactory.decodeStream(image_stream );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                //retrieve a reference to the ImageView
                ImageView picView = (ImageView)findViewById(R.id.picture);
                //display the returned image
                picView.setImageBitmap(thePic);
            }
        }
    }
}
