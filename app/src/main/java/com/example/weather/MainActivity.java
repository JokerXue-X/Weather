package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Button button_show,button_follow;
    EditText text;
    ListView listView;
    private List<City> city = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //编写button_show按钮其点击事件
        Button button_show = (Button) findViewById(R.id.button_show);
        text = (EditText) findViewById(R.id.text);


        button_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = text.getText().toString();//获得输入的城市id值
                Intent intent = new Intent(MainActivity.this, ShowWeather.class);
                intent.putExtra("adcode", id);//将输入的城市id传递到下一个活动
                startActivity(intent);
                //点击添加按钮之后就会跳转到AddActivi活动来填写作者和日记内容
            }
        });


        String response = getJson(this);//获得城市的JOSN数据
        handleResponse(response);//处理城市JSON数据并将其省份城市放在province_city列表中
        CityAdapter adapter = new CityAdapter(MainActivity.this, R.layout.city_view, city);//创建自定义的适配器，其中ListView的子布局为city_view，其中的数据为city列表的数据
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);//配置适配器


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置省份城市的点击事件，当点击一个省份城市时，会进入到它的下一级城市列表
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(MainActivity.this,BelowCity.class);
                String adcode =city.get(position).getCity_Adcode();
                intent.putExtra("adcode",adcode);//将点击的省份城市的adcode信息传过去
                startActivity(intent);
            }
        });


        Button button_follow = (Button) findViewById(R.id.button_follow);
        button_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyFollowCity.class);
                startActivity(intent);
                //点击添加按钮之后就会跳转到AddActivi活动来填写作者和日记内容
            }
        });


    }




    public String getJson(Context context) {//通过此方法利用city.json文件获得城市的json数据，并将它转化为字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.city);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d("dddd",stringBuilder.toString());
        return stringBuilder.toString();
    }


    public void handleResponse(String responseDate) {//处理城市的JSON数据方法，
        try {
            JSONArray city_information = new JSONArray(responseDate);
            for (int i = 0; i < city_information.length() - 1; i++) {
                JSONObject city_infor = city_information.getJSONObject(i);//通过使用JSONArray和JSONObject将城市数据解析出来
                if (city_infor.getString("adcode").substring(2).equals("0000")&&!(city_infor.getString("adcode").equals("100000"))
                        &&!(city_infor.getString("adcode").equals("900000"))) {//判断该地区的adcode是否为省份级别的adcode
                    City c=new City(city_infor.getString("中文名"),city_infor.getString("adcode"));
                    city.add(c);//如果是，则将它存放在city列表中，用来为CityAdapter适配器提供省份城市
                    //Log.d("ddd",city_infor.getString("adcode"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}


class City{//自定义一个类City
    private String city_Name;
    private String city_Adcode;

    public City(String city_Name, String city_Adcode) {
        this.city_Name = city_Name;
        this.city_Adcode = city_Adcode;
    }

    public String getCity_Adcode() {
        return city_Adcode;
    }

    public void setCity_Adcode(String city_Adcode) {
        this.city_Adcode = city_Adcode;
    }

    public String getCity_Name() {
        return city_Name;
    }

    public void setCity_Name(String city_Name) {
        this.city_Name = city_Name;
    }
}


class CityAdapter extends ArrayAdapter<City> {//创建一个自定义适配器，其类型为City，此适配器用来显示主界面的省份城市
    private int resourceId;
    public CityAdapter(Context context, int textViewResourceId, List<City> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//重新getView方法用来返回布局
        City city = getItem(position);//获得当前滚到屏幕类的实例，然后将这个实例加载到界面上
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView TextView_city = (TextView) view.findViewById(R.id.TextView_city);
        TextView_city.setText(city.getCity_Name());
        return view;
    }
}




