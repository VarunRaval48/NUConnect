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
    TextView textView_subject, textView_venue, textView_date, textView_time_from, textView_time_to,
            textView_message, textView_from_name, textView_from_id, textView_msg_type, textView_date_sent_on;
    String parentType;

    public Chat_ExtraLecture_ArrayAdapter(Context context, String parentType) {

        this.context = context;
        notification_list = new ArrayList<>();
        this.parentType = parentType;
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

    public void add(String temp){
        notification_list.add(temp);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<String> temp){
        notification_list.addAll(temp);
        notifyDataSetChanged();
        Log.i("Chat_ArrayAdapter", ""+getCount());
    }

    public View getView(int position, View convertView, ViewGroup parent){

        String jsondata = notification_list.get(getCount() - position - 1);
        JSONObject jsonObject = null;
        JSONObject jsonObject_cover = null;
        LinearLayout itemLayout = null;
        try {
            jsonObject_cover = new JSONObject(jsondata);
            jsonObject = jsonObject_cover.getJSONObject("data");
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemLayout =(LinearLayout) inflater.inflate(R.layout.chat_extra_lecture_list_item, null);
            }
            else{
                itemLayout = (LinearLayout)convertView;
            }

            Log.i("Chat_ExtraLecture", jsonObject.toString());
            if(parentType.equals("Home")) {
                textView_from_name = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_from_name);
                textView_from_name.setText(jsonObject_cover.getString("from_name"));
                textView_from_id = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_from_id);
                textView_from_id.setText(jsonObject_cover.getString("from_id"));
                Log.i("Chat", jsonObject_cover.getString("from_name") + " " + jsonObject_cover.getString("from_id"));
            }
            else if(parentType.equals("SentMessages")){
                itemLayout.removeViewInLayout(itemLayout.findViewById(R.id.textView_chat_extraLecture_from_id));
                itemLayout.removeViewInLayout(itemLayout.findViewById(R.id.textView_chat_extraLecture_from_name));
            }
            textView_msg_type = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_msg_type);
            textView_msg_type.setText(jsonObject_cover.getString("msg_type"));
            textView_date_sent_on = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_date_sent_on);
            textView_date_sent_on.setText(jsonObject_cover.getString("date_sent_on"));
            textView_subject = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_subject_val);
            textView_subject.setText(jsonObject.getString("s"));
            textView_venue = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_venue_val);
            textView_venue.setText(jsonObject.getString("v"));
            textView_time_from = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_fromtime_val);
            textView_time_from.setText(jsonObject.getString("t_f"));
            textView_time_to = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_totime_val);
            textView_time_to.setText(jsonObject.getString("t_t"));
            textView_date = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_date_val);
            textView_date.setText(jsonObject.getString("d"));
            textView_message = (TextView) itemLayout.findViewById(R.id.textView_chat_extraLecture_totime_message);
            textView_message.setText(jsonObject.getString("msg_optional"));

            Log.i("Chat_ArrayAdapter", notification_list.get(position));
        }
        catch (JSONException e) {
//            e.printStackTrace();
            Log.e("ChatExtra", "org.json.Exception");
        }
        return itemLayout;
    }
}
