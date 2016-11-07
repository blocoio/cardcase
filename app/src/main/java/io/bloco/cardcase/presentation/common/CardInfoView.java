package io.bloco.cardcase.presentation.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class CardInfoView extends FrameLayout {

    @Inject
    ImageLoader imageLoader;

    @Bind(R.id.card_avatar)
    ImageView avatar;
    @Bind(R.id.card_avatar_edit_overlay)
    View avatarEditOverlay;
    @Bind(R.id.card_name)
    EditText name;
    @Bind(R.id.card_phone)
    EditText phone;
    @Bind(R.id.card_fields)
    ViewGroup fields;
    @Bind(R.id.card_time)
    TextView time;
    @Bind(R.id.card_company)
    EditText company;
    @Bind(R.id.card_address)
    EditText address;
    @Bind(R.id.card_website)
    EditText website;
    @Bind(R.id.card_position)
    EditText position;
    @Bind(R.id.card_email)
    EditText email;
    @Bind(R.id.linkedinLink)
    EditText linkedinProfile;
    @Bind(R.id.linkedin_icon)
    ImageView linkedinIcon;
    @Bind(R.id.card_vk)
    EditText vklink;
    @Bind(R.id.facebook_link)
    EditText facebooklink;


    @Bind(R.id.instagramLink)
    EditText instagramProfile;
    @Bind(R.id.instagramIcon)
    ImageView instagramIcon;


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
        company.addTextChangedListener(fieldTextWatcher);
        address.addTextChangedListener(fieldTextWatcher);
        website.addTextChangedListener(fieldTextWatcher);
        position.addTextChangedListener(fieldTextWatcher);
        email.addTextChangedListener(fieldTextWatcher);
        phone.addTextChangedListener(fieldTextWatcher);
        vklink.addTextChangedListener(fieldTextWatcher);
        facebooklink.addTextChangedListener(fieldTextWatcher);
        linkedinProfile.addTextChangedListener(fieldTextWatcher);
        instagramProfile.addTextChangedListener(fieldTextWatcher);

        disabledEditMode();
    }

    @OnClick(R.id.card_email)
    public void clickEmail() {
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

    @OnClick(R.id.card_vk)
    public void clickVkLink() {
        if (editMode) {
            return;
        }
        Uri webpage = Uri.parse("https://www.vk.com/" + card.getVklink());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        List<ResolveInfo> resInfo = getContext().getPackageManager().queryIntentActivities(intent, 0);

        if (resInfo.isEmpty()) return;

        for (ResolveInfo info : resInfo) {
            if (info.activityInfo == null) continue;
            if ("com.vkontakte.android".equals(info.activityInfo.packageName)) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        getContext().startActivity(intent);


    }
    @OnClick(R.id.facebook_link)
    public void clickFacebookLink() {
        if (editMode) {
            return;
        }
        Uri webpage = Uri.parse("https://www.facebook.com/" + card.getFacebookLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.vk_icon)
    public void clickVkIcon() {
        clickVkLink();
    }

    @OnClick(R.id.instagramIcon)
    public void clickInstagramIcon(){
        instagramClick();
    }

    @OnClick(R.id.instagramLink)
    public void instagramClick() {
        if (editMode) {
            return;
        }
        Uri webpage = Uri.parse("https://www.instagram.com/" + card.getInstagramURL());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.facebook_icon)
    public void clickFacebookIcon() {
        clickFacebookLink();
    }

    @OnClick(R.id.card_phone)
    public void clickPhone() {
        if (editMode) {
            return;
        }

        String uri = "tel:" + card.getPhone();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        getContext().startActivity(intent);
    }

    @OnClick(R.id.card_avatar)
    public void pickAvatar() {
        if (editMode && editListener != null) {
            editListener.onPickAvatar();
        }
    }

    @OnClick(R.id.linkedin_icon)
    public void clickLinkedinIcon() {
        clickLinkedInURL();
    }

    @OnClick(R.id.linkedinLink)
    public void clickLinkedInURL() {
        if (editMode) {
            return;
        }

        Uri webpage = Uri.parse("https://www.linkedin.com/in/" + card.getLinkedinURL());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        getContext().startActivity(intent);
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
        card.setCompany(company.getText().toString().trim());
        card.setAddress(address.getText().toString().trim());
        card.setWebsite(website.getText().toString().trim());
        card.setPosition(position.getText().toString().trim());
        card.setEmail(email.getText().toString().trim());
        card.setPhone(phone.getText().toString().trim());
        card.setVklink(vklink.getText().toString().trim());
        card.setFacebookLink(facebooklink.getText().toString().trim());

        List<String> urlParts = Arrays.asList(linkedinProfile.toString().trim().split("/"));
        card.setLinkedinURL(urlParts.get(urlParts.size() - 1));

        urlParts = Arrays.asList(instagramProfile.toString().trim().split("/"));
        card.setInstagramURL(urlParts.get(urlParts.size() - 1));

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
        company.setText(card.getCompany());
        address.setText(card.getAddress());
        website.setText(card.getWebsite());
        position.setText(card.getPosition());
        email.setText(card.getEmail());
        phone.setText(card.getPhone());
        vklink.setText(card.getVklink());
        facebooklink.setText(card.getFacebookLink());
        linkedinProfile.setText(card.getLinkedinURL());
        instagramProfile.setText(card.getInstagramURL());

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
        enableEditText(company);
        enableEditText(address);
        enableEditText(website);
        enableEditText(position);
        enableEditText(email);
        enableEditText(phone);
        enableEditText(linkedinProfile);
        enableEditText(vklink);
        enableEditText(facebooklink);

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
        disabledEditText(company);
        disabledEditText(address);
        disabledEditText(website);
        disabledEditText(position);
        disabledEditText(email);
        disabledEditText(phone);
        disabledEditText(vklink);
        disabledEditText(facebooklink);
        disabledEditText(linkedinProfile);
        disabledEditText(instagramProfile);

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
        @Override
        public void onTextChanged(String newText) {
            if (editMode && editListener != null) {
                editListener.onChange(getCard());

                if (allFieldsHaveContent()) {
                    addNewField("");
                }
            }
        }
    }
}
