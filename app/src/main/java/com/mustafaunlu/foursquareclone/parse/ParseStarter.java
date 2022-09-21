package com.mustafaunlu.foursquareclone.parse;

import static com.mustafaunlu.foursquareclone.util.Constant.*;
import android.app.Application;

import com.parse.Parse;

public class ParseStarter extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server(SERVER_ADRESS)
                .build()
        );

    }
}
