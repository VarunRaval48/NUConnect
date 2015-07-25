package com.nirma.varunraval.nuconnect;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class BodyActivity extends Activity implements BodyFragmentInform.OnFragmentInformInteractionListener,
        InformExtraLectureFragment.OnFragmentInformExtralectureInteractionListener,
        SelectReceipentFragment.OnReceipentFragmentInteractionListener{

    private String[] listTitlesStudent, listTitlesFaculty, selectedTypeList;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence title, drawerTitle;
    static String selectedItem;

    static String email, name, login_type;

    AsyncTask<Void, Void, String> sendTask;
    AtomicInteger messageID = new AtomicInteger(1);
    GoogleCloudMessaging gcm;
    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);

        gcm = GoogleCloudMessaging.getInstance(this);

        Intent recIntent = getIntent();
        Bundle bundle = recIntent.getBundleExtra("user_info");
        email = bundle.getString("email");
        name = bundle.getString("name");
        login_type = bundle.getString("login_type");
        Log.i("Name", name+" "+login_type);

        title = getTitle();
        drawerTitle = name;

        listTitlesStudent = getResources().getStringArray(R.array.list_drawer_student);
        listTitlesFaculty = getResources().getStringArray(R.array.list_drawer_faculty);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.ic_drawer, GravityCompat.START);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_body, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        return super.onPrepareOptionsMenu(menu);
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

    @Override
    public void onFragmentInformExtralectureInteraction() {
        Log.i("Receipents", SelectReceipentFragment.receipentIndividualList.toString());

        for(int i: SelectReceipentFragment.receipentIndividualList){
            AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(i);
            if(textView!=null){
                Log.i(""+i+":", textView.getText().toString());

                textView.setText("");
            }
        }

        sendMessage("ECHO", "EXTRA_LECTURE");

        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();

    }

    void sendMessage(String action, String message_type){

        data = new Bundle();
        data.putString("ACTION", action);
        data.putString("MESSAGE_TYPE", message_type);

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String id = Integer.toString(messageID.incrementAndGet());

                Log.d("MessageID", id);

                try{
                    gcm.send(R.string.sender_id+"@gcm.googleapis.com", id, data);
                    Log.d("After GCM send message", "SUCCEssful");
                }
                catch (IOException e) {
                    Log.d("BodyActivity", "Sending Unsuccessful");
                    e.printStackTrace();
                }


                return null;
            }
        };
    }


    @Override
    public void onReceipentFragmentInteraction(Uri uri) {

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

    void wantSignOut(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();

        //TODO Call server that session is finished
        new SignOutServer().execute();

        editor.remove("NUConnect_username");
        editor.remove("NUConnect_email");
        editor.remove("NUConnect_accesstoken");
        editor.remove("NUConnect_login_type");
        editor.commit();

        Intent in = new Intent(BodyActivity.this, MainActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(in);
        finish();
    }

    public class SignOutServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL("http://"+ LoginActivity.serverURL+"/nuconnect/signout.php");

                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("email", email));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url.toURI());

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

                String line;
                StringBuffer value = new StringBuffer();

                while((line=bufferedReader.readLine())!=null){
                    value.append(line);
                }

                Log.i("On sign Out", value.toString());
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }



            return null;
        }
    }
}

