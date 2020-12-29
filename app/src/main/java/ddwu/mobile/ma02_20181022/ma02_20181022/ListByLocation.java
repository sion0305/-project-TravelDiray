package ddwu.mobile.finalproject.ma02_20181022;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ddwu.mobile.finalproject.R;

public class ListByLocation extends AppCompatActivity {

    public static final String TAG = "Recommend";

    TravelListAdapter adapter;
    ArrayList<TravelDto> resultList;
    TravelXmlParserByArea parser;
    NetworkManager networkManager;
    ImageFileManager imgFileManager;

    TextView tvSelect, tvChange;
    ListView lvList;
    Intent intent;
    int type;
    String apiAddress;
    double latitude;//위도
    double longitude;//경도
    String radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_list);

        intent = getIntent();
        latitude = (double) intent.getSerializableExtra("latitude");
        longitude = (double) intent.getSerializableExtra("longitude");
        radius = (String) intent.getSerializableExtra("radius");
        type = (int)intent.getSerializableExtra("type");

        lvList = findViewById(R.id.lvList);
        tvChange = findViewById(R.id.tvChange);
        tvSelect = findViewById(R.id.tvSelect);
        tvSelect.setText("현재위치 기준 " + radius + "m");

        resultList = new ArrayList();

        adapter = new TravelListAdapter(this, R.layout.list_custom, resultList);
        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.api_location_url) + getResources().getString(R.string.t_api_key)
                + "&numOfRows=100&MobileOS=AND&MobileApp=AppTest&arrange=A&contentTypeId=" + type
                + "&mapX=" + longitude + "&mapY=" + latitude + "&radius=" + radius + "&listYN=Y";

        Log.d(TAG, apiAddress);

        parser = new TravelXmlParserByArea();
        networkManager = new NetworkManager(this);
        imgFileManager = new ImageFileManager(this);

        new NetworkAsyncTask().execute(apiAddress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgFileManager.clearTemporaryFiles();
    }

    //thread대신 씀 : 사용하기 편해
    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(ListByLocation.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = null;

            result = networkManager.downloadContents(address);
            if (result == null) return "Error!";

            Log.d(TAG, result);

            // parsing
            resultList = parser.parse(result);

            //검색결과가 없으면 안내문을 띄움
            if(resultList.size() == 0){
                tvChange.setVisibility(View.VISIBLE);
            }else{
                tvChange.setVisibility(View.GONE);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            adapter.setList(resultList);
            progressDlg.dismiss();
        }
    }
}
