package ddwu.mobile.finalproject.ma02_20181022;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ddwu.mobile.finalproject.R;

public class TravelListAdapter extends BaseAdapter {
    public static final String TAG = "adapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<TravelDto> list;
    private NetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;

    public TravelListAdapter(Context context, int resource, ArrayList<TravelDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        imageFileManager = new ImageFileManager(context);
        networkManager = new NetworkManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tvListTitle);
            viewHolder.ivImage = view.findViewById(R.id.ivImage);
            viewHolder.btnAdd = (Button)view.findViewById(R.id.btnAdd);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        TravelDto dto = list.get(position);
        Log.d(TAG, dto.getTitle());

        viewHolder.tvTitle.setText(dto.getTitle());

        /*작성할 부분*/
//         dto의 이미지 주소에 해당하는 이미지 파일이 내부저장소에 있는지 확인
        if(dto.getImageLink() == null){
            return view;
        }
//         ImageFileManager 의 내부저장소에서 이미지 파일 읽어오기 사용
//         이미지 파일이 있을 경우 bitmap, 없을 경우 null 을 반환하므로 bitmap 이 있으면 이미지뷰에 지정
//         없을 경우 GetImageAsyncTask 를 사용하여 이미지 파일 다운로드 수행


        // 파일에 있는지 확인
        Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(dto.getImageLink()); //파일 이름을 잘라서 있는지 없는지
        // dto 의 이미지 주소 정보로 이미지 파일 읽기

        if(savedBitmap != null){
            viewHolder.ivImage.setImageBitmap(savedBitmap);
            Log.d(TAG, "Image loading from file");
        }else{
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
            new GetImageAsyncTask(viewHolder).execute(dto.getImageLink());
            Log.d(TAG, "Image loading from network");
        }

        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(context, RecommendDetail.class);
                detailIntent.putExtra("travel", dto);
                context.startActivity(detailIntent);
            }
        });

        return view;
    }

    public void setList(ArrayList<TravelDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView tvTitle = null;
        public ImageView ivImage = null;
        public Button btnAdd = null;
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask(ViewHolder holder) {
            viewHolder = holder;
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
                viewHolder.ivImage.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }
    }
}
