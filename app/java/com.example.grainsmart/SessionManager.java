package com.example.grainsmart;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MyAppSession";

    private static final String KEY_SELLER_ID = "sellerId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_PIC = "profile_pic";

    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession( String sellerId , String username, String email, String profile_pic) {
        editor.putString(KEY_SELLER_ID, sellerId);
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

    public String getKeySellerId(){
        return pref.getString(KEY_SELLER_ID, null);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void setFprofilepic(String profilePicUrl) {
        editor.putString(KEY_PROFILE_PIC, profilePicUrl);
        editor.apply();
    }

    public String getFprofilepic() {
        return pref.getString(KEY_PROFILE_PIC, "");
    }



}

