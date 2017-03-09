package com.example.alexa.lib;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;

import android.util.Log;
import android.widget.Toast;

public class TokenListener implements Listener<AuthorizeResult, AuthError> {
  private static final String TAG = "ALEXA";

  /* getToken completed successfully. */
  @Override
  public void onSuccess(AuthorizeResult authorizeResult) {
    String accessToken = authorizeResult.getAccessToken();
    Log.i(TAG, "plasma018 accessToken: " + accessToken);
  }

  /* There was an error during the attempt to get the token. */
  @Override
  public void onError(AuthError authError) {
    Log.i(TAG, "plasma018 error: " + authError.getLocalizedMessage());
  }
}


