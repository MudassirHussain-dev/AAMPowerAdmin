package com.aampower.aampoweradmin.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.aampower.aampoweradmin.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class FullImageActivity extends AppCompatActivity {

    PhotoView imgProfile;

    Toolbar fullToolBar;
    Activity context;

    String accNo, accName;

    int TAKE_PHOTO_CODE = 12345;
    public static int count = 0;
    int LOCATION_REQUEST_CODE = 10111;
    String dir;
    Uri imageUri;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    File thumbFile1;

    MenuItem doneMenuItem;

    Dialog loadingAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = FullImageActivity.this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_image);
        fullToolBar = findViewById(R.id.fullToolBar);
        imgProfile = findViewById(R.id.imgProfile);

        setSupportActionBar(fullToolBar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = context.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(context, R.color.black));

            Fade fade = new Fade();
            View decor = getWindow().getDecorView();
            fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }


        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();

            accNo = bundle.getString("accNo");
            accName = bundle.getString("accName");

            if (getSupportActionBar() != null){


                setTitle(accName);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }

        }

        // checking permissions
//        if (!hasPermissions(this, PERMISSIONS)) {
//
//            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_REQUEST_CODE);
//
//        }

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/images/";
        File newdir = new File(dir);
        if (!newdir.exists()) {
            newdir.mkdir();
        }



    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_menu, menu);

        doneMenuItem = menu.findItem(R.id.action_done);

        doneMenuItem.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
//            finish();

            onBackPressed();

        } else if (id == R.id.action_edit) {

            // checking permissions
            if (!hasPermissions(this, PERMISSIONS)) {

                ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_REQUEST_CODE);

            } else {

                takingPhotoAlert();

            }


        }else if (id == R.id.action_done){



        }

        return true;
    }

    private void choosingFromGallery(){

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);

    }

    private void takingPhoto(){

        //region

            File filee = new File(dir);
            File childfile[] = filee.listFiles();

            count = childfile.length;
            count++;
            String file = dir + count + ".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();

            } catch (IOException e) {
//                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(context, "com.aampower.aampoweradmin.provider", newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

            } else {
                imageUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

            }

        //endregion

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int img_width = Integer.valueOf(getString(R.string.img_width));
        int img_height = Integer.valueOf(getString(R.string.img_height));
        int img_quality = Integer.valueOf(getString(R.string.img_quality));


        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {

            doneMenuItem.setVisible(true);

            Bitmap imageBitmap = null;

            try {
                if (imageUri != null) {
                    if (MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri) != null) {

                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                        Uri uri = rotateImageIfRequired(context,
                                imageBitmap,
                                imageUri);

                        if (imageBitmap != null) {


//                            if (getFilePathFromURI(context, uri) != null){
//                                file1 = new File(getFilePathFromURI(context, uri));

                            thumbFile1 = new Compressor(context)
                                    .setMaxWidth(img_width)
                                    .setMaxHeight(img_height)
                                    .setQuality(img_quality)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .compressToFile(new File(getFilePathFromURI(context, uri)));


                            Picasso.get().load(uri).resize(512, 512).centerCrop().into(imgProfile);


                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == 1 && resultCode == RESULT_OK){

            doneMenuItem.setVisible(true);

            Bitmap imageBitmap = null;

            try {
                if (data.getData() != null) {

                    imageUri = data.getData();

                    if (MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri) != null) {

                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                        Uri uri = rotateImageIfRequired(context,
                                imageBitmap,
                                imageUri);

                        if (imageBitmap != null) {


//                            if (getFilePathFromURI(context, uri) != null){
//                                file1 = new File(getFilePathFromURI(context, uri));

                            thumbFile1 = new Compressor(context)
                                    .setMaxWidth(img_width)
                                    .setMaxHeight(img_height)
                                    .setQuality(img_quality)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .compressToFile(new File(getFilePathFromURI(context, uri)));


                            Picasso.get().load(uri).resize(512, 512).centerCrop().into(imgProfile);


                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public Uri rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap b = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
        return getImageUri(context, b);
    }

    private Uri rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return selectedImage;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {

            File rootDataDir = context.getFilesDir();
            File copyFile = new File(rootDataDir + File.separator + fileName + ".jpg");

            //File copyFile = new File(copyFile + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults.length > 0) {

                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                            takingPhotoAlert();

                        } else {

                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    } else if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        } else {

                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }

                }
            }

        }

    }

    private void takingPhotoAlert(){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Picture taking options:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Take photo");
        arrayAdapter.add("Choose from gallery");

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    takingPhoto();
                } else if (which == 1) {
                    choosingFromGallery();
                }

            }
        });
        builderSingle.show();

    }


    private void insertingNewELP(String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.loading_alert, null);
        builder.setView(view);

        loadingAlert = builder.create();
        loadingAlert.show();

        Map<String, String> params = new HashMap<>();
        params.put("accNo", accNo);

        Map<String, File> imageParams = new HashMap<>();
        imageParams.put("img", thumbFile1);

        AndroidNetworking.upload(url)
                .addMultipartFile(imageParams)
                .addMultipartParameter(params)
                .setTag("MYSQL_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadingAlert.dismiss();

                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError error) {
                        loadingAlert.dismiss();

                        Toast.makeText(context, error.getErrorDetail(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


}
