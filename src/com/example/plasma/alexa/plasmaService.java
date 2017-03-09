package com.example.plasma.alexa;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class plasmaService extends Service  {
  private static final String EVENTS_ENDPOINT = "/v20160207/events";
  private static final String DIRECTIVES_ENDPOINT = "/v20160207/directives";
  
  @Override
  public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public void onCreate() {
    super.onCreate();
    
    
    
  }


}
