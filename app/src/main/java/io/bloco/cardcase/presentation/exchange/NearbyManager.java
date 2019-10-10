package io.bloco.cardcase.presentation.exchange;

import android.app.Activity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import javax.inject.Inject;

import io.bloco.cardcase.common.di.PerActivity;

@PerActivity
public class NearbyManager extends MessageListener {

  private Message message;
  private Listener listener;
  private final Activity activity;

  public interface Listener {

    void onMessageReceived(byte[] messageBytes);

    void onError(Status status);
  }

  @Inject
  public NearbyManager(Activity activity) {
    this.activity = activity;
  }

  @Override
  public void onFound(Message message) {
    listener.onMessageReceived(message.getContent());
  }
  //onLost not needed in our case

  public void start(byte[] messageBytes, Listener listener) {
    this.message = new Message(messageBytes);
    this.listener = listener;
    Nearby.getMessagesClient(activity).publish(message)
        .addOnFailureListener(e -> {
          ApiException apiException = (ApiException) e;
          listener.onError(new Status(apiException.getStatusCode()));
        });
    Nearby.getMessagesClient(activity).subscribe(this);
  }

  public void stop() {
    // Clean up when the user leaves the activity.
    Nearby.getMessagesClient(activity).unpublish(message);
    Nearby.getMessagesClient(activity).unsubscribe(this);
  }
}
