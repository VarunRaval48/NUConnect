package com.nirma.varunraval.nuconnect.body;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nirma.varunraval.nuconnect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Varun on 9/8/2015.
 */
public class Chat_ExtraLecture_ArrayAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> notification_list;
    TextView textView_subject, textView_venue, textView_date, textView_time_from, textView_time_to, textView_message, textView_from_name, textView_from_id;

    public Chat_ExtraLecture_ArrayAdapter(Context context) {

        this.context = context;
        notification_list = new ArrayList<>();
    }


    public int getCount(){
        return notification_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(Object temp){
        notification_list.add((String)temp);
    }

    public void addAll(ArrayList<String> temp){
        notification_list = new ArrayList<>(temp);
        notifyDataSetChanged();
        Log.i("Chat_ARrayAdapter", ""+getCount());
    }

    public View getView(int position, View convertView, ViewGroup parent){

        String jsondata = notification_list.get(getCount() - position - 1);
        JSONObject jsonObject = null;
        LinearLayout itemLayout = null;
        try {
            jsonObject = new JSONObject(jsondata);
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemLayout =(LinearLayout) inflater.inflate(R.layout.chat_extra_lecture_list_item, null);
            }
            else{
                itemLayout = (LinearLayout)convertView;
            }

            Log.i("Chat_ExtraLecture", jsonObject.toString());
            textView_from_name = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_from_name);
            textView_from_name.setText(jsonObject.getString("name"));
            textView_from_id = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_from_id);
            textView_from_id.setText(jsonObject.getString("id"));
            textView_subject = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_subject_val);
            textView_subject.setText(jsonObject.getString("subject"));
            textView_venue = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_venue_val);
            textView_venue.setText(jsonObject.getString("venue"));
            textView_time_from = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_fromtime_val);
            textView_time_from.setText(jsonObject.getString("time_from"));
            textView_time_to = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_totime_val);
            textView_time_to.setText(jsonObject.getString("time_to"));
            textView_date = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_date_val);
            textView_date.setText(jsonObject.getString("date"));
            textView_message = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_totime_message);
            textView_message.setText(jsonObject.getString("message"));

            Log.i("Chat", jsonObject.getString("name")+" "+jsonObject.getString("id"));
            Log.i("Chat_ArrayAdapter", notification_list.get(position));
        }
        catch (JSONException e) {
//            e.printStackTrace();
            Log.e("ChatExtra", "org.json.Exception");
        }
        return itemLayout;
    }
}
