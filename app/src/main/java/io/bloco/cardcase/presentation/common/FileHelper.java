package io.bloco.cardcase.presentation.common;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import io.bloco.cardcase.common.Preconditions;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class FileHelper {

  private static final String IMAGE_PREFIX = "card_avatar";
  private static final String TEMP_PREFIX = "temp";
  private static final String IMAGE_SEPARATOR = "_";
  private static final String IMAGE_SUFFIX = ".jpg";

  private final Context context;
  private final ContentResolver contentResolver;

  @Inject public FileHelper(Context context) {
    this.context = context;
    this.contentResolver = context.getContentResolver();
  }

  public File createTemporaryFile() {
    try {
      return File.createTempFile(TEMP_PREFIX, IMAGE_SUFFIX, getCacheFolder());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public File createFinalImageFile() {
    String timeStamp =
        new SimpleDateFormat("yyyyMMdd" + IMAGE_SEPARATOR + "HHmmssSSSS", Locale.US).format(new Date());
    String imageFileName = IMAGE_PREFIX + IMAGE_SEPARATOR + timeStamp;

    File file = new File(getStorageFolder(), imageFileName + IMAGE_SUFFIX);
    try {
      Preconditions.checkState(file.createNewFile(), "Could not create new file");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return file;
  }

  public File saveUriToFile(Uri uri, File file) {
    ParcelFileDescriptor parcelFileDes;

    try {
      parcelFileDes = contentResolver.openFileDescriptor(uri, "r");
    } catch (FileNotFoundException e) {
      Timber.e(e, "saveUriToFile");
      return null;
    }

    Preconditions.checkNotNull(parcelFileDes, "Could not open file URI");
    FileDescriptor fileDes = parcelFileDes.getFileDescriptor();

    FileInputStream inStream = new FileInputStream(fileDes);
    copyInputStreamToFile(inStream, file);
    return file;
  }

  public void saveBytesToFile(String avatarData, File file) {
    byte[] byteData = Base64.decode(avatarData, Base64.DEFAULT);
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      bos.write(byteData);
      bos.flush();
      bos.close();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  public void delete(File file) {
    if (file.exists() && !file.delete()) {
      Timber.w("Could not delete file: %s", file);
    }
  }

  public String getBytesFromFile(File file) {
    try {
      try (RandomAccessFile f = new RandomAccessFile(file, "r")) {
        // Read file and return data
        byte[] data = new byte[(int) f.length()];
        f.readFully(data);
        return Base64.encodeToString(data, Base64.DEFAULT);
      }
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  // PRIVATE

  private File getStorageFolder() {
    return context.getFilesDir();
  }

  private File getCacheFolder() {
    return context.getCacheDir();
  }

  private void copyInputStreamToFile(FileInputStream inStream, File dst) {
    try {
      FileOutputStream outStream = new FileOutputStream(dst);
      FileChannel inChannel = inStream.getChannel();
      FileChannel outChannel = outStream.getChannel();
      inChannel.transferTo(0, inChannel.size(), outChannel);
      inStream.close();
      outStream.close();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }
}
