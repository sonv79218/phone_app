// helper/SharedPrefHelper.java
package com.example.tuan17.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("tendn", null);
    }
}
