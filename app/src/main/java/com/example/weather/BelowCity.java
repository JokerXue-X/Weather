package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class BelowCity extends AppCompatActivity {

    private List<City> city = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_below_city);


        String response = getJson(this);
        handleResponse(response);

        CityAdapter adapter = new CityAdapter(BelowCity.this, R.layout.city_view, city);//创建自定义的适配器，其中ListView的子布局为city_view，其中的数据为province_city列表的数据
        ListView listView = (ListView) findViewById(R.id.list_view1);
        listView.setAdapter(adapter);//配置适配器

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(BelowCity.this,ShowWeather.class);
                String adcode1 =city.get(position).getCity_Adcode();
                intent.putExtra("adcode",adcode1);
                startActivity(intent);
            }
        });

        Button below_return = (Button) findViewById(R.id.below_return);
        below_return.setOnClickListener(new View.OnClickListener() {//取消关注按钮的点击事件
            @Override
            public void onClick(View v) {
                finish();
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


    public void handleResponse(String responseDate) {//处理城市的JSON数据方法，它是来获取点击省份城市后，它的下一级的城市列表信息
        Intent intent = getIntent();
        String  adcode= intent.getStringExtra("adcode");//利用传递过来的省份城市adcode

        try {
            JSONArray city_information = new JSONArray(responseDate);
            for (int i = 0; i < city_information.length() - 1; i++) {
                JSONObject city_infor = city_information.getJSONObject(i);//通过使用JSONArray和JSONObject将城市数据解析出来
                if (city_infor.getString("adcode").substring(0,2).equals(adcode.substring(0,2))
                        &&!city_infor.getString("adcode").substring(4,6).equals("00")) {//由于省份城市的adcode和它下一级城市列表的adcode前2位相同，则可以得到该省份城市的下一级城市列表，并将它们放在适配器中
                    City c=new City(city_infor.getString("中文名"),city_infor.getString("adcode"));
                    city.add(c);//如果是，则将它存放在city列表中，用来为CityAdapter适配器提供省份城市
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}



class City1{//自定义一个类City，表示省份城市下一级的城市列表
    private String city_Name;
    private String city_Adcode;

    public City1(String city_Name, String city_Adcode) {
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


class CityAdapter1 extends ArrayAdapter<City> {//创建一个自定义适配器，其类型为City，此适配器用来显示省份城市下一级的城市列表
    private int resourceId;
    public CityAdapter1(Context context, int textViewResourceId, List<City> objects) {
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




