package io.bloco.cardcase.presentation.exchange;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import io.bloco.cardcase.common.di.PerActivity;
import javax.inject.Inject;
import timber.log.Timber;

@PerActivity public class NearbyManager extends MessageListener
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private GoogleApiClient mGoogleApiClient;
  private Message mMessage;
  private Listener listener;

  public interface Listener {
    void onPermissionNeeded(Status status);

    void onMessageReceived(byte[] messageBytes);

    void onConnectionFailed(ConnectionResult connectionResult);

    void onError(Status status);
  }

  @Inject public NearbyManager(Activity activity) {
    mGoogleApiClient = new GoogleApiClient.Builder(activity).addApi(Nearby.MESSAGES_API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
  }

  public void start(byte[] messageBytes, Listener listener) {
    this.mMessage = new Message(messageBytes);
    this.listener = listener;

    if (!mGoogleApiClient.isConnected()) {
      mGoogleApiClient.connect();
    }
  }

  public void stop() {
    if (mGoogleApiClient.isConnected()) {
      // Clean up when the user leaves the activity.
      Nearby.Messages.unpublish(mGoogleApiClient, mMessage)
          .setResultCallback(new ErrorCheckingCallback("unpublish()"));
      Nearby.Messages.unsubscribe(mGoogleApiClient, this)
          .setResultCallback(new ErrorCheckingCallback("unsubscribe()"));
    }
    mGoogleApiClient.disconnect();
  }

  public void retry() {
    publishAndSubscribe();
  }

  // GoogleApiClient connection callback.
  @Override public void onConnected(Bundle connectionHint) {
    Nearby.Messages.getPermissionStatus(mGoogleApiClient)
        .setResultCallback(new ErrorCheckingCallback("getPermissionStatus", new Runnable() {
          @Override public void run() {
            publishAndSubscribe();
          }
        }));
  }

  @Override public void onConnectionSuspended(int i) {
    Timber.d("onConnectionSuspended() called with: %d", i);
  }

  private void publishAndSubscribe() {
    // We automatically subscribe to messages from nearby devices once
    // GoogleApiClient is connected. If we arrive here more than once during
    // an activity's lifetime, we may end up with multiple calls to
    // subscribe(). Repeated subscriptions using the same MessageListener
    // are ignored.
    Nearby.Messages.publish(mGoogleApiClient, mMessage)
        .setResultCallback(new ErrorCheckingCallback("publish()"));
    Nearby.Messages.subscribe(mGoogleApiClient, this)
        .setResultCallback(new ErrorCheckingCallback("subscribe()"));
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    listener.onConnectionFailed(connectionResult);
  }

  @Override public void onFound(final Message message) {
    listener.onMessageReceived(message.getContent());
  }

  /**
   * A simple ResultCallback that logs when errors occur.
   * It also displays the Nearby opt-in dialog when necessary.
   */
  private class ErrorCheckingCallback implements ResultCallback<Status> {
    private final String method;
    private final Runnable runOnSuccess;

    private ErrorCheckingCallback(String method) {
      this(method, null);
    }

    private ErrorCheckingCallback(String method, @Nullable Runnable runOnSuccess) {
      this.method = method;
      this.runOnSuccess = runOnSuccess;
    }

    @Override public void onResult(@NonNull Status status) {
      if (status.isSuccess()) {
        Timber.i("%s succeeded.", method);
        if (runOnSuccess != null) {
          runOnSuccess.run();
        }
      } else {
        // Currently, the only resolvable error is that the device is not opted
        // in to Nearby. Starting the resolution displays an opt-in dialog.
        if (status.hasResolution()) {
          listener.onPermissionNeeded(status);
        } else {
          listener.onError(status);
        }
      }
    }
  }
}
