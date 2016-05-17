package io.bloco.cardcase.presentation.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import io.bloco.cardcase.BuildConfig;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.Preconditions;
import io.bloco.cardcase.presentation.common.FileHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class AvatarPicker {

  public class ReceivingError extends Exception {}
  public class ResizeError extends Exception {}

  public static final int AVATAR_REQUEST_CODE = 21;
  public static final int CROP_REQUEST_CODE = 31;
  public static final int IMAGE_QUALITY = 50;
  public static final int AVATAR_SIZE = 512;

  private final Context context;
  private final Resources resources;
  private final FileHelper fileHelper;
  private File tempFile;

  public static class BundleArgs {
    public static final String FILE_PATH = "file_path";
  }

  @Inject public AvatarPicker(Context context, Resources resources, FileHelper fileHelper) {
    this.context = context;
    this.resources = resources;
    this.fileHelper = fileHelper;
  }

  public void startPicker(Activity activity) {
    tempFile = createTempFile();

    Intent pickIntent = new Intent();
    pickIntent.setType("image/*");
    pickIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
    pickIntent.addCategory(Intent.CATEGORY_OPENABLE);

    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    takePhotoIntent.putExtra("android.intent.extras.CAMERA_FACING",
        Camera.CameraInfo.CAMERA_FACING_FRONT);
    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempFileUri());
    takePhotoIntent.addFlags(
        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

    String pickTitle = resources.getString(R.string.avatar_picker);
    Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });

    activity.startActivityForResult(chooserIntent, AVATAR_REQUEST_CODE);
  }

  public File processActivityResult(int requestCode, int resultCode, Intent data,
      Activity activity) throws ReceivingError, ResizeError {
    // Crop
    if (requestCode == CROP_REQUEST_CODE) {
      clearTempFile();
      if (resultCode == Activity.RESULT_OK) {
        String croppedAvatarPath = data.getStringExtra("file_path");
        Preconditions.checkNotNull(croppedAvatarPath, "Empty cropped avatar");
        return new File(croppedAvatarPath);
      } else if (resultCode == CropAvatarActivity.RESULT_ERROR) {
        throw new ResizeError();
      }
      return null;
    }

    if (requestCode != AVATAR_REQUEST_CODE) {
      return null;
    }

    // Picker (camera or gallery)

    if (resultCode != Activity.RESULT_OK) {
      // There was an error, let's cancel
      clearTempFile();
      return null;
    }

    // Check if we still have the tempFile
    if (tempFile == null) {
      throw new ReceivingError();
    }

    // Gallery
    if (data != null) {
      Uri imageUri = data.getData();
      if (imageUri != null) {
        tempFile = fileHelper.saveUriToFile(imageUri, tempFile);
        if (tempFile == null) {
          throw new ReceivingError();
        }
      }
    }

    // Camera: nothing to do, the photo is already on tempFile

    cropPhoto(activity);

    return null;
  }

  public File createNewAvatarFile() {
    return fileHelper.createFinalImageFile();
  }

  public void resizeAvatar(File avatarFile) throws ResizeError {
    Bitmap originalBitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
    if (originalBitmap == null) {
      throw new ResizeError();
    }

    Bitmap bitmap = Bitmap.createScaledBitmap(originalBitmap, AVATAR_SIZE, AVATAR_SIZE, false);
    FileOutputStream fos;
    try {
      fos = new FileOutputStream(avatarFile);
      bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, fos);
      fos.close();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  // Helpers

  private File createTempFile() {
    if (tempFile != null) {
      clearTempFile();
    }
    return fileHelper.createTemporaryFile();
  }

  private void clearTempFile() {
    if (tempFile != null && tempFile.exists()) {
      fileHelper.delete(tempFile);
      tempFile = null;
    }
  }

  private void cropPhoto(Activity activity) {
    String tempFilePath = tempFile.getAbsolutePath();
    Intent cropIntent = CropAvatarActivity.Factory.getIntent(activity, tempFilePath);
    activity.startActivityForResult(cropIntent, CROP_REQUEST_CODE);
  }

  private Uri getTempFileUri() {
    return FileProvider.getUriForFile(context, BuildConfig.FILES_AUTORITY, tempFile);
  }
}
