package ddwu.mobile.finalproject.ma02_20181022;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ddwu.mobile.finalproject.R;

public class MyAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<TravelDto> travelList;
    private LayoutInflater inflater;
    ImageFileManager imageFileManager;

    public MyAdapter(Context context, int layout, ArrayList<TravelDto> travelList) {

        this.context = context;
        this.layout = layout;
        this.travelList = travelList;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageFileManager = new ImageFileManager(context);
    }
    @Override
    public int getCount() {
        return travelList.size();
    }

    @Override
    public Object getItem(int position) {//원본데이터 찾기 위치로(순서로)
        return travelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return travelList.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tvListTitle);
            viewHolder.ivImage = convertView.findViewById(R.id.ivListImage);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvTitle.setText(travelList.get(position).getTitle());
        Bitmap bitmap = imageFileManager.getBitmapFromExternal(travelList.get(position).getImageFileName());
        viewHolder.ivImage.setImageBitmap(bitmap);


        return convertView;
    }

    static class ViewHolder{
        TextView tvTitle;
        ImageView ivImage;
    }
}
