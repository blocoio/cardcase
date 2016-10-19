package io.bloco.cardcase.presentation.common;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.cardcase.presentation.home.HomeContract;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Bind(R.id.category_name)
    TextView name;

    private HomeContract.View homeContract;
    private Database database;

    private Category category;

    @Inject
    public CategoryViewHolder(View view, HomeContract.View homeContract, Database database) {
        super(view);
        view.setOnClickListener(this);

        ((AndroidApplication) view.getContext().getApplicationContext()).getApplicationComponent()
                .inject(this);

        this.homeContract = homeContract;
        this.database = database;
        ButterKnife.bind(this, view);
    }

    public void bind(Category category) {
        this.category = category;
        name.setText(category.getName());
    }

    @Override
    public void onClick(View view) {
        Log.d("TEST", "CLICKED " + category.getName() + " " + category.getId());
        List<Card> cards = database.getReceivedCards();
        List<Card> cardsByCategory = new ArrayList<>();
        for (Card card : cards) {
            Log.d("TEST", "check " + card.getCategoryId().toString() + "::" + category.getId().toString());
            if (card.getCategoryId().equals(category.getId())) {
                Log.d("TEST", "Add " + card.getCategoryId().toString());
                cardsByCategory.add(card);
            }
        }
        homeContract.hideCategories();
        homeContract.showCards(cardsByCategory);
    }
}
