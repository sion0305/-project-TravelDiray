package ddwu.mobile.finalproject.ma02_20181022;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ddwu.mobile.finalproject.R;

public class RecommendDetail extends AppCompatActivity {
    public static final String TAG = "detail";
    final int DETAIL_CODE = 100;

    TextView tvTitle, tvTel, tvAddr;
    ImageView ivImage;
    TravelDto travel;
    String mapx, mapy;
    ImageFileManager imageFileManager;
    NetworkManager networkManager;
    MyTravelDBManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        travel = (TravelDto) getIntent().getSerializableExtra("travel");

        dbManager = new MyTravelDBManager(this);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvAddr = findViewById(R.id.tvDetailAddress);
        tvTel = findViewById(R.id.tvDetailTel);
        ivImage  = findViewById(R.id.ivDetail);

        if(travel.getTel() == null)
            travel.setTel("없음");

        tvTitle.setText(travel.getTitle());
        tvAddr.setText(travel.getAddress());
        tvTel.setText(travel.getTel());

        mapx = travel.getMapy();
        mapy = travel.getMapx();
        Log.d(TAG, mapx + ", " + mapy);

        travel.setMemo("");

        imageFileManager = new ImageFileManager(this);

        Log.d(TAG, travel.getImageLink());
        Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(travel.getImageLink()); //파일 이름을 잘라서 있는지 없는지

        // dto 의 이미지 주소 정보로 이미지 파일 읽기
        if(savedBitmap != null){
            ivImage.setImageBitmap(savedBitmap);
            Log.d(TAG, "Image loading from file");
        }else{
            ivImage.setImageResource(R.mipmap.ic_launcher);
            new GetImageAsyncTask().execute(travel.getImageLink());
            Log.d(TAG, "Image loading from network");
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnDetailMap:
                String location = String.format("geo:" + mapx + "," + mapy + "?z=%d", 17);
                Uri uri = Uri.parse(location);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivityForResult(it, DETAIL_CODE);
                break;
            case R.id.btnDetailAdd:
                String fileName = imageFileManager.moveFileToExt(travel.getImageLink());
                travel.setImageFileName(fileName);

                boolean result = dbManager.addNewTravel(travel);

                if (result) {
                    Toast.makeText(this, "myTravel에 추가!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "추가 실패!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDetailShare:
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_SUBJECT, "여기어때?\n");
                msg.putExtra(Intent.EXTRA_TEXT, travel.getTitle() + "(" + travel.getAddress() + ")");
                msg.putExtra(Intent.EXTRA_TITLE, travel.getTitle());
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, "공유"));
                break;
        }

    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        String imageAddress;
        ProgressDialog progressDlg;

        public GetImageAsyncTask() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(RecommendDetail.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = null;

            result = networkManager.downloadImage(imageAddress);
            return result;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*작성할 부분*/
            /*네트워크에서 다운 받은 이미지 파일을 ImageFileManager 를 사용하여 내부저장소에 저장
             * 다운받은 bitmap 을 이미지뷰에 지정*/
            if(bitmap != null){
                ivImage.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }
    }

}
