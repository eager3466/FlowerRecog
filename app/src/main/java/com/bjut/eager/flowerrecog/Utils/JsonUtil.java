package com.bjut.eager.flowerrecog.Utils;

import android.util.Log;

import com.bjut.eager.flowerrecog.Bean.Item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Eager on 2016/9/26.
 */
public class JsonUtil {

    public static ArrayList<Item> parse(String jsonString) {

        ArrayList<Item> items = new ArrayList<>();
        try
        {
            Log.i("YhqTest", "return string: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray data = (JSONArray) jsonObject.get("data");
            for (int i=0; i<data.length(); i++) {
                JSONArray J_item = (JSONArray) data.get(i);
                Item item = new Item();
                item.setIndex(J_item.getInt(0));
                item.setProbability((float)J_item.getDouble(1));
                item.setDescription(J_item.getString(2));
                items.add(item);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return items;
    }
}