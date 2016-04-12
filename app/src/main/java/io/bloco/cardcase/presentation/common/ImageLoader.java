package io.bloco.cardcase.presentation.common;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class ImageLoader {

  private final Context context;

  @Inject public ImageLoader(Context context) {
    this.context = context;
  }

  public void loadAvatar(ImageView imageView, String avatarPath) {
    if (BitmapFactory.decodeFile(avatarPath) == null) {
      Timber.w("Invalid avatar file");
    }
    Picasso.with(context)
        .load(new File(avatarPath))
        .fit()
        .centerCrop()
        .transform(new CircleTransform())
        .into(imageView);
  }
}
