package ddwu.mobile.finalproject.ma02_20181022;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ddwu.mobile.finalproject.R;

public class MyTravel extends AppCompatActivity {
    ListView listView;
    MyAdapter adapter;
    ArrayList<TravelDto> travelList = null;
    MyTravelDBManager dbManager;
    TextView tvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        listView = findViewById(R.id.lvMyTravel);
        travelList = new ArrayList();
        adapter = new MyAdapter(this, R.layout.custom_mytravel, travelList);
        listView.setAdapter(adapter);
        dbManager = new MyTravelDBManager(this);
        tvAdd = findViewById(R.id.tvAdd);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TravelDto travel = travelList.get(position);
                Intent intent = new Intent(MyTravel.this, MyTravelDetail.class);
                intent.putExtra("travel", travel);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MyTravel.this);
                builder.setTitle(R.string.delete_dialog_title)
                        .setMessage(travelList.get(pos).getTitle() + " 를 삭제하시겠습니까?" )
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbManager.removeTravel(travelList.get(pos).get_id())) {
                                    Toast.makeText(MyTravel.this, " 삭제 완료", Toast.LENGTH_SHORT).show();
                                    travelList.clear();
                                    travelList.addAll(dbManager.getAllTravel());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MyTravel.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        travelList.clear();
        travelList.addAll(dbManager.getAllTravel());
        adapter.notifyDataSetChanged();
        if(travelList.size() == 0){
            tvAdd.setVisibility(View.VISIBLE);
        }else{
            tvAdd.setVisibility(View.GONE);
        }
    }
}
