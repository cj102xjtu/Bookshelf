package com.example.bookshelf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookshelf.R;
import com.fedorvlasov.lazylist.ImageLoader;

public class BookListAdapter extends BaseAdapter {

    private static final String ICON_RUL = "iconUrl";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String AUTHOR = "author";
    private static final String LENT = "lent";

    private JSONArray mData = null;
    private LayoutInflater mInflater = null;
    public ImageLoader mImageLoader = null;

    public BookListAdapter(JSONArray d, LayoutInflater inflater,
            ImageLoader imageLoader) {
        mData = d;
        mInflater = inflater;
        mImageLoader = imageLoader;
    }
    
    public void setData(JSONArray d)
    {
        mData = d;
//        try {
//            mData = new JSONArray(d.toString());
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public int getCount() {
        int lenth = 0;
        if (mData != null) {
            lenth = mData.length();
        }
        return lenth;
    }

    public Object getItem(int position) {
        JSONObject jsonObject = null;
        try {
            jsonObject = mData.getJSONObject(position);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

    public long getItemId(int position) {
        long id = 0;
        try {
            JSONObject jsonObject = mData.getJSONObject(position);
            id= jsonObject.getLong(ID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public boolean isEnabled(int position) {
        boolean result = true;
        if (true) {
            JSONObject jsonObject = (JSONObject) getItem(position);
            try {
                String lent = jsonObject.getString(LENT);
                result = lent.isEmpty();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = mInflater.inflate(R.layout.list_item, null);

        TextView bookName = (TextView) vi.findViewById(R.id.text1);
        TextView author = (TextView) vi.findViewById(R.id.text2);
        ImageView image = (ImageView) vi.findViewById(R.id.image);

        JSONObject jsonObject = (JSONObject) getItem(position);
        try {
            jsonObject = mData.getJSONObject(position);
            // set name
            bookName.setText(jsonObject.getString(NAME));

            // set author
            author.setText(jsonObject.getString(AUTHOR));

            // set icon
            String iconUrl = jsonObject.getString(ICON_RUL);
            mImageLoader.DisplayImage(iconUrl, image);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return vi;
    }
}