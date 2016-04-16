package io.bloco.cardcase.presentation.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.home.SimpleTextWatcher;
import java.util.ArrayList;
import javax.inject.Inject;

public class CardInfoView extends FrameLayout {

  @Inject ImageLoader imageLoader;

  @Bind(R.id.card_avatar) ImageView avatar;
  @Bind(R.id.card_avatar_edit_overlay) View avatarEditOverlay;
  @Bind(R.id.card_name) EditText name;
  @Bind(R.id.card_email) EditText email;
  @Bind(R.id.card_phone) EditText phone;
  @Bind(R.id.card_fields) ViewGroup fields;
  @Bind(R.id.card_time) TextView time;

  private Card card;
  private CardEditListener editListener;
  private FieldTextWatcher fieldTextWatcher;
  private boolean editMode;

  public CardInfoView(Context context, AttributeSet attrs) {
    super(context, attrs);
    ((AndroidApplication) context.getApplicationContext()).getApplicationComponent().inject(this);

    inflate(context, R.layout.view_card_info, this);
    ButterKnife.bind(this);

    fieldTextWatcher = new FieldTextWatcher();
    name.addTextChangedListener(fieldTextWatcher);
    email.addTextChangedListener(fieldTextWatcher);
    phone.addTextChangedListener(fieldTextWatcher);

    disabledEditMode();
  }

  @OnClick(R.id.card_email) public void clickEmail() {
    if (editMode) {
      return;
    }

    String uri = "mailto:" + card.getEmail();
    Intent intent = new Intent(Intent.ACTION_SENDTO);
    intent.setType("text/plain");
    intent.setData(Uri.parse(uri));

    Intent chooser = Intent.createChooser(intent, getResources().getString(R.string.send_email));
    getContext().startActivity(chooser);
  }

  @OnClick(R.id.card_phone) public void clickPhone() {
    if (editMode) {
      return;
    }

    String uri = "tel:" + card.getPhone();
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse(uri));
    getContext().startActivity(intent);
  }

  @OnClick(R.id.card_avatar) public void pickAvatar() {
    if (editMode && editListener != null) {
      editListener.onPickAvatar();
    }
  }

  public void setAvatar(String avatarPath) {
    card.setAvatarPath(avatarPath);
    if (card.hasAvatar()) {
      imageLoader.loadAvatar(avatar, card.getAvatarPath());
    }
  }

  public void showTime() {
    time.setVisibility(VISIBLE);
  }

  public Card getCard() {
    card.setName(name.getText().toString().trim());
    card.setEmail(email.getText().toString().trim());
    card.setPhone(phone.getText().toString().trim());

    int fieldsCount = fields.getChildCount();
    ArrayList<String> fieldValues = new ArrayList<>(fieldsCount);
    for (int i = 0; i < fieldsCount; i++) {
      EditText field = (EditText) fields.getChildAt(i);
      String fieldValue = getEditTextValue(field);
      if (!fieldValue.isEmpty()) {
        fieldValues.add(fieldValue);
      }
    }
    card.setFields(fieldValues);

    return card;
  }

  public void setCard(Card card) {
    this.card = card.copy();

    name.setText(card.getName());
    email.setText(card.getEmail());
    phone.setText(card.getPhone());

    setAvatar(card.getAvatarPath());

    fields.removeAllViews();
    for (String fieldValue : card.getFields()) {
      addNewField(fieldValue);
    }

    if (card.getUpdatedAt() != null) {
      long timestamp = card.getUpdatedAt().getTime();
      String timeCaption = DateUtils.getRelativeTimeSpanString(timestamp).toString();
      String timePhrase = getResources().getString(R.string.card_time, timeCaption);
      time.setText(timePhrase);
    }

    if (editMode) {
      enableEditMode();
    } else {
      disabledEditMode();
    }
  }

  public void setEditListener(CardEditListener editListener) {
    this.editListener = editListener;
  }

  public void enableEditMode() {
    editMode = true;

    enableEditText(name);
    enableEditText(email);
    enableEditText(phone);

    if (card == null || !card.hasAvatar()) {
      avatar.setImageResource(R.drawable.avatar_edit);
    }

    for (int i = 0, count = fields.getChildCount(); i < count; i++) {
      EditText field = (EditText) fields.getChildAt(i);
      enableEditText(field);
    }
    addNewField("");

    avatarEditOverlay.setVisibility(View.VISIBLE);
  }

  public void disabledEditMode() {
    editMode = false;

    disabledEditText(name);
    disabledEditText(email);
    disabledEditText(phone);

    if (card == null || !card.hasAvatar()) {
      avatar.setImageResource(R.drawable.ic_avatar);
    }

    for (int i = 0; i < fields.getChildCount(); i++) {
      EditText field = (EditText) fields.getChildAt(i);
      if (getEditTextValue(field).isEmpty()) {
        fields.removeView(field);
        i--;
      } else {
        disabledEditText(field);
      }
    }

    avatarEditOverlay.setVisibility(View.GONE);

    // Close keyboard
    InputMethodManager imm = (InputMethodManager)
        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getWindowToken(), 0);
  }

  private void addNewField(String value) {
    inflate(getContext(), R.layout.item_card_field, fields);
    EditText field = (EditText) fields.getChildAt(fields.getChildCount() - 1);
    field.setText(value);
    field.addTextChangedListener(fieldTextWatcher);
    if (!editMode) {
      disabledEditText(field);
    }
  }

  // Private

  private String getEditTextValue(EditText editText) {
    return editText.getText().toString().trim();
  }

  private void disabledEditText(EditText editText) {
    // Hide if empty
    if (editText.getText().length() == 0) {
      editText.setVisibility(View.GONE);
    } else {
      editText.setVisibility(View.VISIBLE);
    }

    editText.setCursorVisible(false);
    editText.setLongClickable(false);
    editText.setFocusable(false);
    editText.setFocusableInTouchMode(false);
    editText.setSelected(false);

    // Hide background
    editText.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
  }

  private void enableEditText(EditText editText) {
    editText.setVisibility(View.VISIBLE);

    editText.setCursorVisible(true);
    editText.setLongClickable(true);
    editText.setFocusable(true);
    editText.setFocusableInTouchMode(true);
    editText.setSelected(true);

    // Show background
    editText.setBackgroundTintList(getResources().getColorStateList(R.color.secondary));
  }

  private boolean allFieldsHaveContent() {
    for (int i = 0, count = fields.getChildCount(); i < count; i++) {
      EditText field = (EditText) fields.getChildAt(i);
      if (getEditTextValue(field).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public interface CardEditListener {
    void onPickAvatar();

    void onChange(Card updatedCard);
  }

  private class FieldTextWatcher extends SimpleTextWatcher {
    @Override public void onTextChanged(String newText) {
      if (editMode && editListener != null) {
        editListener.onChange(getCard());

        if (allFieldsHaveContent()) {
          addNewField("");
        }
      }
    }
  }
}
