package com.example.plasma.alexa;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.ScopeFactory;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.example.alexa.lib.TokenListener;


public class MainActivity extends Activity {
  private static final String TAG = "MainActivity";
  private static final Scope ALEXA_ALL_SCOPE = ScopeFactory.scopeNamed("alexa:all");
  private RequestContext mRequestContext;
  private static final String PRODUCT_ID = "my_device";
  private static final String PRODUCT_DSN = "123456";
  private Button mLoginButton;
  private Button mVoiceButton;
  private Context mContext;
  private PlasmaService plasmaService;
  private TextView serviceStatus;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mContext = this;
    mRequestContext = RequestContext.create(this);
    mRequestContext.registerListener(new AuthorizeListenerImpl());
    // Find the button with the login_with_amazon ID
    // and set up a click handler
    mLoginButton = (Button) findViewById(R.id.login_with_amazon);
    mLoginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final JSONObject scopeData = new JSONObject();
        final JSONObject productInstanceAttributes = new JSONObject();
        try {
          productInstanceAttributes.put("deviceSerialNumber", PRODUCT_DSN);
          scopeData.put("productInstanceAttributes", productInstanceAttributes);
          scopeData.put("productID", PRODUCT_ID);

          AuthorizationManager.authorize(new AuthorizeRequest.Builder(mRequestContext)
              .addScope(ScopeFactory.scopeNamed("alexa:all", scopeData))
              .forGrantType(AuthorizeRequest.GrantType.ACCESS_TOKEN).shouldReturnUserData(false)
              .build());
        } catch (JSONException e) {
          Log.i(TAG, "plasma018 errors:" + e);
        }
      }

    });
    serviceStatus = (TextView) findViewById(R.id.status);
    mVoiceButton = (Button) findViewById(R.id.microphone_button);
    mVoiceButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_UP:
            Log.i(TAG, "MotionEvent.ACTION_UP");
            serviceStatus.setText(R.string.thinking);
            plasmaService.StopRecordVoice();
            break;
          case MotionEvent.ACTION_DOWN:
            Log.i(TAG, "MotionEvent.ACTION_DOWN");
            serviceStatus.setVisibility(View.VISIBLE);
            plasmaService.RecordVoice();
            break;
        }
        return false;
      }
    });
    mVoiceButton.setEnabled(false);
    App.setMainActivity(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mRequestContext.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();
    AuthorizationManager.getToken(this, new Scope[] {ALEXA_ALL_SCOPE}, new TokenListener());
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Intent intent = new Intent(MainActivity.this, PlasmaService.class);
    stopService(intent);
  }


  public void setService(PlasmaService service) {
    plasmaService = service;
  }

  public void setStatusGone() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        serviceStatus.setVisibility(View.INVISIBLE);
      }
    });
  }

  private class AuthorizeListenerImpl extends AuthorizeListener {
    /* Authorization was completed successfully. */
    @Override
    public void onSuccess(final AuthorizeResult authorizeResult) {
      AuthorizationManager.getToken(mContext, new Scope[] {ScopeFactory.scopeNamed("alexa:all")},
          new TokenListener());
      Log.i(TAG, "plasma018 onSuccess: " + authorizeResult.getAccessToken());
      if (authorizeResult.getAccessToken() != null) {
        Intent intent = new Intent(MainActivity.this, PlasmaService.class);
        intent.putExtra("token", authorizeResult.getAccessToken());
        intent.setAction();
        startService(intent);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            mVoiceButton.setEnabled(true);
          }
        });
      }
    }

    /* There was an error during the attempt to authorize the application. */
    @Override
    public void onError(final AuthError authError) {
      Log.i(TAG, "plasma018 onError: " + authError.getLocalizedMessage());
    }

    /* Authorization was cancelled before it could be completed. */
    @Override
    public void onCancel(final AuthCancellation authCancellation) {
      Log.i(TAG, "plasma018 onCancel: " + authCancellation.getDescription());
    }
  }



}
