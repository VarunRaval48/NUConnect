package com.nirma.varunraval.nuconnect.body;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nirma.varunraval.nuconnect.database.MessagesDatabasedbHelper;
import com.nirma.varunraval.nuconnect.MainActivity;
import com.nirma.varunraval.nuconnect.message.sendUpstreamMessage;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.SendIDToServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class BodyActivity extends Activity implements BodyFragmentInform.OnFragmentInformInteractionListener,
        InformExtraLectureFragment.OnFragmentInformExtralectureInteractionListener,
        SelectReceipentFragment.OnReceipentFragmentInteractionListener, BodyFragmentHome.OnFragmentInteractionListener{

    private String[] listTitlesStudent, listTitlesFaculty, selectedTypeList;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence title, drawerTitle;
    static String selectedItem;

    final int PLAY_SERVICES_RESOLUTION_REQUEST=990;

    static String email, name, login_type;

    AsyncTask<Void, Void, String> sendTask;
    AtomicInteger messageID = new AtomicInteger(1);
    GoogleCloudMessaging gcm;
    Bundle data;

    static int countRecInd=1, countRecGrp=101;

    Button dateButton;
    AutoCompleteTextView textView_venue, textView_Subject;
    Button textView_time_to=null, textView_time_from=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);

        gcm = GoogleCloudMessaging.getInstance(this);

        Intent recIntent = getIntent();
        Bundle bundle = new Bundle();

        //In case of some extra things
        Log.i("BodyActivity", recIntent.getStringExtra("intent_type"));
        if(recIntent.getStringExtra("intent_type").equals("new_login")) {
        }
        else if(recIntent.getStringExtra("intent_type").equals("notification")) {
        }

        bundle = recIntent.getBundleExtra("user_info");
        Log.i("BodyActivity", bundle.toString());
        Log.i("BodyActivity", bundle.getString("email")+" "+bundle.getString("name")+" "+bundle.getString("login_type"));
        email = bundle.getString("email");
        name = bundle.getString("name");
        login_type = bundle.getString("login_type");
        Log.i("Name", name + " " + login_type);

        title = getTitle();
        drawerTitle = name;

        listTitlesStudent = getResources().getStringArray(R.array.list_drawer_student);
        listTitlesFaculty = getResources().getStringArray(R.array.list_drawer_faculty);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        if(login_type.equals("Student"))
            selectedTypeList = listTitlesStudent;
        else if(login_type.equals("Faculty"))
            selectedTypeList = listTitlesFaculty;
        else
            selectedTypeList = listTitlesStudent;

        listView.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, R.id.list_item_text_view, selectedTypeList));
        listView.setOnItemClickListener(new DrawerItemClickListener());
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
//                getActionBar().setTitle(title);
                if(!selectedItem.equals("")){
                    getActionBar().setTitle(selectedItem);
                }
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                getActionBar().setTitle(drawerTitle);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        if(savedInstanceState == null){
            selectItem(0);
        }


    }

    protected void onResume(){
        super.onResume();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{

        Calendar c;
        BodyActivity bodyActivity;
        public DatePickerFragment(){
            c = Calendar.getInstance();
        }

        public void onAttach(Activity activity){
            super.onAttach(activity);
            bodyActivity = (BodyActivity)activity;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState){
            return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            bodyActivity.useDate(year, monthOfYear, dayOfMonth);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener{

        BodyActivity bodyActivity;
        Bundle arg;
        public TimePickerFragment(){
        }

        public void onAttach(Activity activity){
            super.onAttach(activity);
            bodyActivity = (BodyActivity)activity;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState){
            arg = getArguments();
            return new TimePickerDialog(getActivity(), this, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            bodyActivity.useTime(hourOfDay, minute, arg.getString("type"));
        }
    }

    Calendar c = Calendar.getInstance();
    public void showDatePickerDialog(View v){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datepicker");
    }

    public void showTimePickerDialogFrom(View v){
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        Bundle arg = new Bundle();
        arg.putString("type", "From");
        timePickerFragment.setArguments(arg);
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTimePickerDialogTo(View v){
        Log.i("BodyActivity", (v.getId()==R.id.button_time_to)+"");
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        Bundle arg = new Bundle();
        arg.putString("type", "To");
        timePickerFragment.setArguments(arg);
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    void useDate(int y, int m, int d){
//        dateButton = (Button)findViewById(R.id.buttonDate);
        dateButton.setText(d + "-" + (m+1) + "-" + y);
    }

    void useTime(int h, int m, String type){
//        timeButton_to = (Button)findViewById(R.id.buttonTimeTo);
//        textView_time_to = (TextView)findViewById(R.id.textView_time_to);
//        textView_time_from = (TextView)findViewById(R.id.textView_time_from);
        if(type.equals("From")){
            textView_time_from.setText(type+" "+h+":"+m);
        }
        else{
            textView_time_to.setText(type+" "+h+":"+m);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_body, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }

    Context getContext(){
        return getApplicationContext();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent in = new Intent(BodyActivity.this, Settings.class);
            startActivity(in);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInformInteraction(String type, String fragmentType) {
        if(type.equals("fragment")){
            if(fragmentType.equals("Extra Lecture")){
                Fragment fragment = new InformExtraLectureFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutInformDynamic, fragment)
                        .commit();

                fragmentManager.executePendingTransactions();
                textView_time_to = (Button)findViewById(R.id.button_time_to);
                textView_time_from = (Button)findViewById(R.id.button_time_from);
                dateButton = (Button)findViewById(R.id.buttonDate);

            }
            else if(fragmentType.equals("Individual")){
                SelectReceipentFragment selectReceipentFragment = SelectReceipentFragment.newInstance("Individual");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutInformDynamicWhoTo, selectReceipentFragment)
                        .commit();

                fragmentManager.executePendingTransactions();
            }
            else if(fragmentType.equals("Group")){
                SelectReceipentFragment selectReceipentFragment = SelectReceipentFragment.newInstance("Group");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutInformDynamicWhoTo, selectReceipentFragment)
                        .commit();

                fragmentManager.executePendingTransactions();
            }
        }
    }

    Spinner spinner_informWhatTo;
    EditText editText_message_optional;
    ArrayList<String> receipentList[] = new ArrayList[2];

    @Override
    public void onFragmentInformExtralectureInteraction(ArrayList<Integer> receipentListID) {
        Log.i("Reciepents", receipentListID.toString());

        for(int i=0; i<receipentList.length; i++){
            receipentList[i] = new ArrayList<>();
        }


        AutoCompleteTextView textView;
        for(int i: receipentListID){
            Log.d("List View", Integer.toString(i));
            textView = (AutoCompleteTextView)findViewById(i);
            Log.i("BodyAct_IND", textView.getText().toString());
            if(!textView.getText().equals("")){
                Log.i(""+i+":", textView.getText().toString());
                receipentList[0].add(textView.getText().toString());
                textView.setText("");
                flag = 1;
            }
        }
//        sendMessage("ECHO", "EXTRA_LECTURE");
        send_message_extra_lecture(receipentList);
    }

    int flag=0;
    //TODO no need of group_info
    @Override
    public void onFragmentInformExtralectureInteraction(ArrayList<Integer> receipentListID, ArrayList<String> group_info) {
        AutoCompleteTextView textView;

        for(int i=0; i<receipentList.length; i++){
            receipentList[i] = new ArrayList<>();
        }

        for(int i: receipentListID){
            Log.d("List View", Integer.toString(i));
            textView = (AutoCompleteTextView)findViewById(i);
            if(textView.getText().length() != 0){
                Log.i("__InGrpInd "+i+":", "textView "+textView.getText().toString());
                receipentList[0].add(textView.getText().toString());
                textView.setText("");
                flag = 1;
            }
        }

        Spinner spinnerInformWhoToYear = (Spinner)findViewById(R.id.spinnerInformWhoToYear);
        Spinner spinnerInformWhoTo = (Spinner)findViewById(R.id.spinnerInformWhoTo);
        Spinner spinnerInformDivision = (Spinner)findViewById(R.id.spinnerInformDivision);
        String temp;
        temp = spinnerInformWhoToYear.getSelectedItem().toString().toLowerCase()+"b"+
                spinnerInformWhoTo.getSelectedItem().toString().toLowerCase()+spinnerInformDivision.getSelectedItem().toString().toLowerCase();
        if(!temp.contains("None")) {
            receipentList[1].add(temp);
            flag = 1;
        }
        Log.i("__InGrp_BodyAct", receipentList[1] + " " + receipentList[0]);

        send_message_extra_lecture(receipentList);
    }

    private void send_message_extra_lecture(ArrayList<String> receipentlist_final[]){
        textView_venue = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView_Venue);
        textView_Subject = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView_Subject);
        editText_message_optional = (EditText)findViewById(R.id.editText_optional_message);

        String venue = textView_venue.getText().toString(), subject = textView_Subject.getText().toString(),
                message_optional = editText_message_optional.getText().toString(), date_button = dateButton.getText().toString(),
                time_from = textView_time_from.getText().toString().split(" ")[1], time_to = textView_time_to.getText().toString().split(" ")[1];
        if(flag!=1 || venue.equals(null) || subject.equals(null) || date_button.equals("Date")
                || time_from.equals("Select FROM") || time_to.equals("Select TO")) {
            Toast.makeText(getApplicationContext(), "Enter Reciepents", Toast.LENGTH_SHORT).show();
        }
        else{
            try {
                spinner_informWhatTo = (Spinner)findViewById(R.id.spinnerInformWhatTo);
                JSONObject jsonObject_data = new JSONObject();

                String msg_type = spinner_informWhatTo.getSelectedItem().toString();

                if(message_optional.equals(null))
                    message_optional = "NO Message";
                Log.i("BodyActivity", message_optional);
                jsonObject_data.accumulate("msg_optional", message_optional);
                jsonObject_data.accumulate("d", date_button);
                jsonObject_data.accumulate("t_f", time_from);
                jsonObject_data.accumulate("t_t", time_to);
                jsonObject_data.accumulate("v", venue);
                jsonObject_data.accumulate("s", subject);

                Log.i("BodyActivity", "Sending Message");
                sendUpstreamMessage sendUpstreamMessage = new sendUpstreamMessage(receipentlist_final, msg_type, jsonObject_data,
                        (getResources().getString(R.string.server_url) + "sendUpstreamMessage.php"), getApplicationContext());
                sendUpstreamMessage.execute();
                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//TODO during implementation of XAMPP
//    void sendMessage(String action, String message_type){
//
//        data = new Bundle();
//        data.putString("ACTION", action);
//        data.putString("MESSAGE_TYPE", message_type);
//
//        sendTask = new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//
//                String id = Integer.toString(messageID.incrementAndGet());
//
//                Log.d("MessageID", id);
//
//                try{
//                    gcm.send(R.string.sender_id+"@gcm.googleapis.com", id, data);
//                    Log.d("After GCM send message", "SUCCEssful");
//                }
//                catch (IOException e) {
//                    Log.d("BodyActivity", "Sending Unsuccessful");
//                    e.printStackTrace();
//                }
//
//
//                return null;
//            }
//        };
//    }


    @Override
    public void onReceipentFragmentInteraction(String type, String fragmentType) {

        if(fragmentType=="AutoCompleteTextViewInd" && SelectReceipentFragment.canAddIndividual()){

            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutIndividual);
            while(findViewById(countRecInd)!=null){
                countRecInd++;
            }
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View rowView = inflater.inflate(R.layout.select_receipent_row, tableLayout);

            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)rowView.findViewById(R.id.autoCompleteViewIndReceipent);

            autoCompleteTextView.setId(countRecInd);

            SelectReceipentFragment.reciepentListID[SelectReceipentFragment.msg_type].add(countRecInd);
        }
        else if(fragmentType=="AutoCompleteTextViewGrp" && SelectReceipentFragment.canAddIndividual()){

            TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayoutGroup);

            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View rowView = layoutInflater.inflate(R.layout.select_receipent_row, tableLayout);

            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)rowView.findViewById(R.id.autoCompleteViewIndReceipent);

            while(findViewById(countRecGrp)!=null){
                countRecGrp++;
            }
            autoCompleteTextView.setId(countRecGrp);

            SelectReceipentFragment.reciepentListID[SelectReceipentFragment.msg_type].add(countRecGrp);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        drawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void selectItem(int position){
        Log.i("Selected Item", " " + position);

        switch (selectedTypeList[position].toString()){
            case "Home":
                goHome();
                break;
            case "Sign Out":
                wantSignOut();
                break;
            case "Inform":
                wantInform();
                break;
            case "Sent Messages":
                sentMessages();
                break;
        }

        selectedItem = selectedTypeList[position];
        listView.setItemChecked(position, true);
        listView.setSelection(position);
        getActionBar().setTitle(selectedTypeList[position]);
        drawerLayout.closeDrawer(listView);
    }

    void goHome(){
        Fragment fragment = new BodyFragmentHome();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    void wantInform(){
        BodyFragmentInform fragment = new BodyFragmentInform();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    void sentMessages(){
        BodyFragmentSentMessages fragmentSentMessages= new BodyFragmentSentMessages();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragmentSentMessages)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    boolean isDeviceOnline() {
        ConnectivityManager comMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = comMng.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;
        return false;
    }

    void wantSignOut(){
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();

        //TODO Call server that session is finished
        new AlertDialog.Builder(this)
                .setTitle("Sign Out?")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email_initials = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .getString("NUConnect_email", "");
                        if(isDeviceOnline())
                            new SignOutServer(email_initials).execute();
                        editor.remove("NUConnect_username");
                        editor.remove("NUConnect_email");
                        editor.remove("NUConnect_accesstoken");
                        editor.remove("NUConnect_login_type");
                        editor.remove("NUConnect_last_sent_message");
                        editor.remove("NUConnect_last_got_message");
                        editor.commit();

                        MessagesDatabasedbHelper messagesDatabasedbHelper = new MessagesDatabasedbHelper(getApplicationContext());
                        messagesDatabasedbHelper.close();
                        getApplicationContext().deleteDatabase(MessagesDatabasedbHelper.DATABASE_NAME);

                        Intent in = new Intent(BodyActivity.this, MainActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(in);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.alert_light_frame)
                .show();
    }

    public class SignOutServer extends AsyncTask<Void, Void, Void> {

        String value, email_initials;

        public SignOutServer(String e){
            email_initials = e;
        }

        @Override

        protected Void doInBackground(Void... params) {

            try {
                String url = (getResources().getString(R.string.server_url) + "signout.php");
                List<NameValuePair> list = new ArrayList<>();
                Log.i("BodyActivity", email_initials);
                if(!email_initials.equals("")) {
                    list.add(new BasicNameValuePair("email", email_initials));

                    Log.i("Body Activity", email_initials);
                    SendIDToServer sendIDToServer = new SendIDToServer(url, list, getApplicationContext());
                    value = sendIDToServer.sendToken();

                    Log.i("On sign Out", value);
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

