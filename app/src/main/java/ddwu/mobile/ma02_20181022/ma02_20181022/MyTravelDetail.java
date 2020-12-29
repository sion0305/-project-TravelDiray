package ddwu.mobile.finalproject.ma02_20181022;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ddwu.mobile.finalproject.R;

public class MyTravelDetail extends AppCompatActivity {
    public static final String TAG = "mydetail";
    final int DETAIL_CODE = 100;
    private final static int REQUEST_TAKE_THUMBNAIL = 100;
    private static final int REQUEST_TAKE_PHOTO = 200;

    TextView tvTitle, tvTel, tvAddr;
    EditText etMemo, etDate;
    ImageView ivImage;
    TravelDto travel;
    String latitude, longitude;
    String image, photo;
    MyTravelDBManager dbManager;
    ImageFileManager imageFileManager;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_detail);

        dbManager = new MyTravelDBManager(this);
        imageFileManager = new ImageFileManager(this);

        travel = (TravelDto) getIntent().getSerializableExtra("travel");

        tvTitle = findViewById(R.id.tvMyTitle);
        tvAddr = findViewById(R.id.tvMyAddress);
        tvTel = findViewById(R.id.tvMyPhone);
        ivImage  = findViewById(R.id.myImage);
        etMemo = findViewById(R.id.etMyMemo);
        etDate = findViewById(R.id.etMyDate);

        tvTitle.setText(travel.getTitle());
        tvAddr.setText(travel.getAddress());
        tvTel.setText(travel.getTel());

        image = travel.getImageFileName();
        photo = travel.getPhotoFile();

        Log.d(TAG, "사진찍음: " + photo);

        if(photo == null){
            if(image != null){
                ivImage.setImageBitmap(imageFileManager.getBitmapFromExternal(image));
            }
        }else{
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            bmOptions.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(photo, bmOptions);
            ivImage.setImageBitmap(bitmap);
        }

        if(!travel.getMemo().equals("")){
            etMemo.setText(travel.getMemo());
        }
        if(travel.getDate() != null){
            etDate.setText(travel.getDate());
        }

        longitude = travel.getMapx();
        latitude = travel.getMapy();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnMyMap:
                String location = String.format("geo:" + latitude + "," + longitude + "?z=%d", 17);
                Uri uri = Uri.parse(location);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivityForResult(it, DETAIL_CODE);
                break;
            case R.id.btnMySave:
                travel.setMemo(etMemo.getText().toString());
                travel.setDate(etDate.getText().toString());
                travel.setPhotoFile(mCurrentPhotoPath);
                Log.d(TAG, "저장은"+travel.getPhotoFile());
                boolean result = dbManager.modifyTravel(travel);
                if (result) {
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnMyaddCancel:
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MyTravelDetail.this);
                deleteBuilder.setTitle(R.string.delete_dialog_title)
                        .setMessage("MyTravel에서 삭제됩니다. 정말 삭제 하시겠습니까?" )
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbManager.removeTravel(travel.get_id())) {
                                    Toast.makeText(MyTravelDetail.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                                    Intent resultIntent = new Intent();
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                } else {
                                    Toast.makeText(MyTravelDetail.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                break;
            case R.id.btnMyshare:
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_SUBJECT, "여기어때?\n");
                msg.putExtra(Intent.EXTRA_TEXT, travel.getTitle() + "(" + travel.getAddress() + ")\n*"+travel.getMemo());
                msg.putExtra(Intent.EXTRA_TITLE, travel.getTitle());
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, "공유"));
                break;
            case R.id.btnReturn:
                AlertDialog.Builder returnBuilder = new AlertDialog.Builder(MyTravelDetail.this);
                returnBuilder.setTitle(R.string.camera_dialog_title)
                        .setMessage("사진은 복구되지 않습니다. \n그래도 원본 사진으로 바꾸시겠습니까?" )
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                travel.setPhotoFile(null);
                                dbManager.modifyTravel(travel);
                                String fileName = Uri.parse(photo).getLastPathSegment();
                                imageFileManager.removeImageFromExt(fileName);
                                changeToOriginal();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                break;
            case R.id.myImage:
                AlertDialog.Builder cameraBuilder = new AlertDialog.Builder(MyTravelDetail.this);
                cameraBuilder.setTitle(R.string.camera_dialog_title)
                        .setMessage("사진을 변경하시겠습니까?" )
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dispatchTakePictureIntent();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                break;
        }
    }

    public void changeToOriginal(){
        ivImage.setImageBitmap(imageFileManager.getBitmapFromExternal(image));
    }
    /*원본 사진 파일 저장*/
    private void dispatchTakePictureIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//카메라 앱 호출

        if(takePicture.resolveActivity(getPackageManager()) != null){
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(this,
                        "ddwucom.mobile.finalreport.photo.fileprovider",
                        photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePicture, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivImage.getWidth();
        int targetH = ivImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivImage.setImageBitmap(bitmap);
    }

    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_THUMBNAIL && resultCode == RESULT_OK) {
            Bundle extra = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extra.get("data");
            ivImage.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

}
