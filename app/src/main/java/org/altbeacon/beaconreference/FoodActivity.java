package org.altbeacon.beaconreference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class FoodActivity extends AppCompatActivity {
    private ImageButton ibtn_1;
    String TAG = MainActivity.class.getSimpleName()+"My";
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    ArrayList<HashMap<String,String>> arrayListFilter;
    RecyclerViewAdapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        catchData();


        recyclerView = findViewById(R.id.recyclerView);
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        ibtn_1 = (ImageButton) findViewById(R.id.imageButton2);
        ibtn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FoodActivity.this,"回到首頁",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FoodActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //抓取api資料
    public void catchData(){
        String catchData = "http://172.18.252.191/TEST/sayyes.php"; //10.0.2.2已為本地端不需要更改 只須改後面路徑
        ProgressDialog dialog = ProgressDialog.show(this,"讀取中"
                ,"請稍候",true);
        new Thread(()->{
            try {
                URL url = new URL(catchData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line = in.readLine();
                StringBuffer json = new StringBuffer();
                while (line != null) {
                    json.append(line);
                    line = in.readLine();
                }

                JSONArray jsonArray= new JSONArray(String.valueOf(json));
                for (int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String ID = jsonObject.getString("ID");
                    String username = jsonObject.getString("username");
                    String password_ = jsonObject.getString("password_");
                    String shop_name = jsonObject.getString("shop_name");
                    String user_label = jsonObject.getString("user_label");
                    String user_location = jsonObject.getString("user_location");
                    String booth = jsonObject.getString("booth");
                    String user_number = jsonObject.getString("user_number");
                    String user_time_open = jsonObject.getString("user_time_open");
                    String user_time_close = jsonObject.getString("user_time_close");
                    String meal = jsonObject.getString("meal");
                    String user_introduce = jsonObject.getString("user_introduce");
                    String user_menu = jsonObject.getString("user_menu");
                    String push_title = jsonObject.getString("push_title");
                    String push_content = jsonObject.getString("push_content");
                    String promotional_content = jsonObject.getString("promotional_content");
                    String push_times = jsonObject.getString("push_times");
                    String activity_time_start = jsonObject.getString("activity_time_start");
                    String activity_time_end = jsonObject.getString("activity_time_end");
                    String picture = jsonObject.optString("picture");

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("shop_name", shop_name);
                    hashMap.put("user_introduce", user_introduce);
                    hashMap.put("user_label", user_label);
                    hashMap.put("user_number", user_number);
                    hashMap.put("user_time_open", user_time_open);
                    hashMap.put("user_time_close", user_time_close);
                    hashMap.put("user_location", user_location);
                    hashMap.put("booth", booth);
                    hashMap.put("meal", meal);
                    hashMap.put("user_menu",user_menu);
                    hashMap.put("picture",picture);
                    arrayList.add(hashMap);

                }
                Log.d(TAG, "catchData: "+arrayList);

                runOnUiThread(()->{
                    dialog.dismiss();


                    mAdapter = new RecyclerViewAdapter(arrayList, FoodActivity.this);
                    recyclerView.setAdapter(mAdapter);
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }
//
    /**初始化Toolbar內SearchView的設置*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        /**SearchView設置，以及輸入內容後的行動*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /**調用RecyclerView內的Filter方法*/
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
