package com.example.grainsmart;

import android.content.Context;
import android.content.SharedPreferences;

public class CustomerSessionManager {
    private static final String PREF_NAME = "MyAppSession";
    private static final String KEY_BUYER_ID = "buyerid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;


    public CustomerSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession( String buyerid , String username, String email, String profile_pic) {
        editor.putString(KEY_BUYER_ID, buyerid);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_PIC, profile_pic);
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getKeyBuyerId(){
        return pref.getString(KEY_BUYER_ID, null);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getFprofilepic() {
        return pref.getString(KEY_PROFILE_PIC, "");
    }


}
