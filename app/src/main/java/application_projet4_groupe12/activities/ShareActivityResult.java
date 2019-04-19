package application_projet4_groupe12.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import application_projet4_groupe12.R;

public class ShareActivityResult extends AppCompatActivity {

    ProgressDialog progressDialog;
    RelativeLayout cropView;
    Button share_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_result);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Init Views
        ImageView imgView = (ImageView) findViewById(R.id.finalImage);

        // récup de l'image
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.openFileInput("img"));


            imgView.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap bitmap_final = bitmap;
        share_button = findViewById(R.id.share_pic_button);
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(ShareActivityResult.this);
                progressDialog.setTitle(R.string.app_name);
                progressDialog.setMessage("Traitement de votre belle photo");
                progressDialog.setIndeterminate(false);
                progressDialog.setIcon(R.drawable.ic_launcher);
                progressDialog.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap_final_logo = drawBeerTagLogo(bitmap_final);
                        shareImage(bitmap_final_logo);
                    }
                }, 3000);

            }
        });
    }

    public void shareImage(Bitmap bitmap) {
        progressDialog.dismiss();
        Uri uri = getImageUri(ShareActivityResult.this, bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.intent_share_text)));
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "MyMeme", null);
        return Uri.parse(path);
    }


    public Bitmap drawBeerTagLogo(Bitmap bitmap){
        Bitmap iconBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon);
        int positionLeft=0;
        int positionTop=0;
        Bitmap bitmap_with_logo =Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap_with_logo);
        canvas.drawBitmap(bitmap, positionLeft, positionTop,null);
        positionLeft= bitmap.getWidth()-300;
        positionTop=bitmap.getHeight()-300;
        canvas.drawBitmap(iconBitmap,positionLeft,positionTop,null);
        return bitmap_with_logo;
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            // DEFAULT BACK BUTTON
//            case android.R.id.home:
//                this.finish();
//                return true;
//
//
//            // SHARE BUTTON
//            case R.id.share_button:
//                // Init a ProgressDialog
//                progressDialog = new ProgressDialog(this);
//                progressDialog.setTitle(R.string.app_name);
//                progressDialog.setMessage("Preparing image for sharing...");
//                progressDialog.setIndeterminate(false);
//                progressDialog.setIcon(R.drawable.ic_launcher);
//                progressDialog.show();
//
//
////                cropView.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        takeScreenshotOfCropView();
////                    }
////                }, 1000);
//
//
//                return true;
//        }
//        return (super.onOptionsItemSelected(menuItem));
//    }


}
