package com.flicker.sayarpc.flickerapplication;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.PhotoAdapterListener {

    Context context;
    private SearchView searchView;
    protected boolean doubleBackToExitPressedOnce = false;
    RecyclerView rView;
    String url;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressDialog;
    List<ItemObject> rowListItem,rowListItemCopy;
    private GridLayoutManager  layoutManager;
    RecyclerViewAdapter rcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        url="https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=9f89151d82e427401680cd48dd2d5cf5&per_page=30&page=1&format=json&nojsoncallback=1";
        getSupportActionBar().setTitle("Flicker App");
        progressDialog=new ProgressDialog(this);
        context = getApplicationContext();
        swipeRefreshLayout=findViewById(R.id.swipeRefresh);
        rView= (ShimmerRecyclerView)findViewById(R.id.recycler_view1);
        rowListItem= new ArrayList<>();
        rowListItem.clear();
        rowListItemCopy=new ArrayList<>();
        layoutManager= new GridLayoutManager(getApplicationContext(),2);
        if(MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            rView.setLayoutManager(new GridLayoutManager(context, 2));
        }
        else{
            rView.setLayoutManager(new GridLayoutManager(context, 4));
        }
        rView.setHasFixedSize(false);
        rView.setLayoutManager(layoutManager);
        rcAdapter = new RecyclerViewAdapter(context, rowListItem,this);
        rView.setAdapter(rcAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetReceiver.isConnected()){
                    new FetchPhotos(url).execute();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toasty.info(MainActivity.this,getString(R.string.PleaseConnectToInternet),Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
            }
        });

        if (InternetReceiver.isConnected()) {
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.show();
            new FetchPhotos(url).execute();
        }
        else{
            Toasty.info(MainActivity.this,getString(R.string.PleaseConnectToInternet),Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    return;
                }
                //recreate();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted

//                if (query.equals("")){
//                    InputMethodManager inputManager = (InputMethodManager)
//                            getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                            InputMethodManager.HIDE_NOT_ALWAYS);
//                    searchView.setIconified(true);
//                    if (!rowListItem.isEmpty()){
//                        rowListItem.clear();
//                        rowListItem.addAll(rowListItemCopy);
//                        rcAdapter.notifyDataSetChanged();
//                    }
////                    rowListItem.clear();
////                    new FetchPhotos(url).execute();
//
//                }


                    rcAdapter.getFilter().filter(query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
//                if (query.equals("")){
//                    InputMethodManager inputManager = (InputMethodManager)
//                            getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                            InputMethodManager.HIDE_NOT_ALWAYS);
//                    searchView.setIconified(true);
//                    if (!rowListItem.isEmpty()){
//                        rowListItem.clear();
//                        rowListItem.addAll(rowListItemCopy);
//                        rcAdapter.notifyDataSetChanged();
//                    }
//                    progressDialog.setMessage(getString(R.string.pleaseWait));
//                    progressDialog.show();
//                    new FetchPhotos(url).execute();
//                    //recreate();
//
//                }

                    rcAdapter.getFilter().filter(query);


                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPhotoSelected(ItemObject contact) {
        //Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getName(), Toast.LENGTH_LONG).show();
    }

    private class FetchPhotos extends AsyncTask<String, Void, String> {
        String photoUrl;
        int count=0;
        FetchPhotos(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        protected String doInBackground(String... urls) {
            String result;
            String inputLine;

            try {
                //Create a URL object holding our url
                URL myUrl = new URL(url);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod("GET");

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            Log.d("FirstPage",url);
            progressDialog.dismiss();
            rowListItem.clear();
            try {
                Log.d("result", result);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("photos");
                JSONArray jsonArray=jsonObject1.getJSONArray("photo");
                for (int i=0;i<jsonArray.length();i++){
                    count++;
                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                    String title=jsonObject2.getString("title");
                    //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
                    int farm=jsonObject2.getInt("farm");
                    int server=jsonObject2.getInt("server");
                    String secret=jsonObject2.getString("secret");
                    String owner=jsonObject2.getString("owner");
                    String id=jsonObject2.getString("id");
                    Log.d("farmn", String.valueOf(farm));
                    Log.d("server", String.valueOf(server));
                    Log.d("secret",secret);
                    Log.d("owner",owner);
                    Log.d("id",id);
                    Log.d("Title",title);
                    String photoUrl="https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
                    Log.d("photoUrl",photoUrl);
                    ItemObject itemObject=new ItemObject(title,photoUrl);
                    rowListItem.add(itemObject);
                    rowListItemCopy.add(itemObject);
                    //lLayout = new GridLayoutManager(MainActivity.this, 4);
                    //new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);




                }
                Log.d("Total",""+count);
                rcAdapter.notifyDataSetChanged();

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }


        }
    }

//    private List<ItemObject> getAllItemList(){
//
//        List<ItemObject> allItems = new ArrayList<ItemObject>();
//        allItems.add(new ItemObject("United States", R.mipmap.ic_launcher));
//        allItems.add(new ItemObject("Canada", R.mipmap.ic_launcher));
//        allItems.add(new ItemObject("United Kingdom", R.mipmap.ic_launcher));
//        allItems.add(new ItemObject("Germany", R.mipmap.ic_launcher));
//        allItems.add(new ItemObject("Sweden", R.mipmap.ic_launcher));
//        allItems.add(new ItemObject("United Kingdom", R.mipmap.ic_launcher));
//        allItems.add(new ItemObject("Germany", R.mipmap.ic_launcher));
//        allItems.add(new ItemObject("Sweden", R.mipmap.ic_launcher));
//        return allItems;
//    }
    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        //FaveoApplication.getInstance().setInternetListener(this);
        checkConnection();
        if (InternetReceiver.isConnected()){
            new FetchPhotos(url).execute();
        }
    }

    private void checkConnection() {
        boolean isConnected = InternetReceiver.isConnected();
        showSnackIfNoInternet(isConnected);
    }

    /**
     * Display the snackbar if network connection is not there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnackIfNoInternet(boolean isConnected) {
        if (!isConnected) {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.sry_not_connected_to_internet, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

    }

    /**
     * Display the snackbar if network connection is there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnack(boolean isConnected) {

        if (isConnected) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.connected_to_internet, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else {
            showSnackIfNoInternet(false);
        }

    }

//    /**
//     * Callback will be triggered when there is change in
//     * network connection
//     */
//    @Override
//    public void onNetworkConnectionChanged(boolean isConnected) {
//        showSnack(isConnected);
//    }

    /**
     * Handling the back button here.
     * As if we clicking twice then it will
     * ask press one more time to exit,we are handling
     * the double back button pressing here.
     */
    @Override
    public void onBackPressed() {
//        if (!searchView.isIconified()) {
//            searchView.setIconified(true);
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;
//            }
//            return;
//        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }


        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(findViewById(android.R.id.content), R.string.press_again_exit, Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2500);
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
//        Snackbar.make(findViewById(android.R.id.content), event.message, Snackbar.LENGTH_LONG).show();
        showSnack(event.message);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();

    }
    public void refresh() {

        if (InternetReceiver.isConnected()) {
//                        loading = true;
            rView.setVisibility(View.VISIBLE);
            if (rowListItem.size() != 0) {
                rowListItem.clear();
                rcAdapter.notifyDataSetChanged();
                new FetchPhotos(url).execute();
            }
        } else {
            rView.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);

        }
    }

}

