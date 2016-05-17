package io.bloco.cardcase.presentation.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lyft.android.scissors.CropView;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.Preconditions;
import io.bloco.cardcase.common.di.ActivityComponent;
import io.bloco.cardcase.common.di.DaggerActivityComponent;
import io.bloco.cardcase.presentation.BaseActivity;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;

public class CropAvatarActivity extends BaseActivity {

  public static final int RESULT_ERROR = 999;

  @Inject AvatarPicker avatarPicker;

  @Bind(R.id.crop_avatar_view) CropView cropView;

  private ProgressDialog waitDialog;
  private CropView.Extensions cropExtensions;

  public static class Factory {
    public static Intent getIntent(Context context, String filePath) {
      Intent intent = new Intent(context, CropAvatarActivity.class);
      intent.putExtra(AvatarPicker.BundleArgs.FILE_PATH, filePath);
      return intent;
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crop);

    initializeInjectors();

    bindToolbar();
    toolbar.setTitle(R.string.crop_avatar);
    toolbar.setStartButton(R.drawable.ic_close, R.string.close, new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    Intent intent = getIntent();
    String filePath = intent.getStringExtra(AvatarPicker.BundleArgs.FILE_PATH);
    Preconditions.checkNotNull(filePath, "File path not provided");

    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
    cropView.setImageBitmap(bitmap);
    cropExtensions = cropView.extensions();
  }

  private void initializeInjectors() {
    ActivityComponent component = DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(getActivityModule())
        .build();
    component.inject(this);

    ButterKnife.bind(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    dismissDialog();
  }

  @OnClick(R.id.crop_avatar_fab) public void crop() {
    showWaitDialog();

    new AsyncTask<Void, Void, File>() {
      private Exception exception;

      @Override protected File doInBackground(Void... params) {
        File avatarFile = avatarPicker.createNewAvatarFile();
        executeCrop(avatarFile);
        try {
          avatarPicker.resizeAvatar(avatarFile);
        } catch (AvatarPicker.ResizeError resizeError) {
          exception = resizeError;
        }
        return avatarFile;
      }

      @Override protected void onPostExecute(File avatarFile) {
        if (exception != null) {
          finishWithError();
        } else {
          finishWithResult(avatarFile);
        }
        dismissDialog();
      }
    }.execute();
  }

  private void showWaitDialog() {
    waitDialog =
        ProgressDialog.show(this, null, getString(R.string.crop_avatar_dialog), true, false);
  }

  private void dismissDialog() {
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
      waitDialog = null;
    }
  }

  private void finishWithResult(File avatarFile) {
    Intent intent = new Intent();
    intent.putExtra(AvatarPicker.BundleArgs.FILE_PATH, avatarFile.getAbsolutePath());
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  private void finishWithError() {
    Intent intent = new Intent();
    setResult(RESULT_ERROR, intent);
    finish();
  }

  private void executeCrop(File avatarFile) {
    try {
      cropExtensions.crop()
          .quality(AvatarPicker.IMAGE_QUALITY)
          .format(Bitmap.CompressFormat.JPEG)
          .into(avatarFile)
          .get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
