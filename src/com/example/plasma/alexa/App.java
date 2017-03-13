package com.example.plasma.alexa;

import android.app.Application;

public class App extends Application {
  private static PlasmaService plasmaService = null;
  private static MainActivity mainActivity = null;

  public class ServiceAction {
    public static final String startService = "SERVICE.START";
  }


  public static void setService(PlasmaService service) {
    plasmaService = service;
  };

  public static void setMainActivity(MainActivity activity) {
    mainActivity = activity;
  }

  public static PlasmaService getService() {
    return plasmaService;
  }

  public static MainActivity getMainActivity() {
    return mainActivity;
  }

}
