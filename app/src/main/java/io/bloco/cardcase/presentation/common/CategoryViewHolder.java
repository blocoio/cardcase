package io.bloco.cardcase.presentation.common;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Bind(R.id.card_avatar)
    ImageView avatar;
    @Bind(R.id.card_name)
    TextView name;
    @Bind(R.id.card_time)
    TextView time;

    private Category category;

    public CategoryViewHolder(View view) {
        super(view);
        view.setOnClickListener(this);

        ((AndroidApplication) view.getContext().getApplicationContext()).getApplicationComponent()
                .inject(this);

        ButterKnife.bind(this, view);
    }

    public void bind(Category category) {
        this.category = category;
        name.setText(category.getName());
    }

    @Override
    public void onClick(View view) {
        Log.d("TEST", "CATEGORY CLICKED");
    }
}
