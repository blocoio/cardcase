package io.bloco.cardcase.presentation.common;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Category;
import io.bloco.cardcase.presentation.home.HomeContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PerActivity
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static class ViewType {
        private static final int NORMAL = 0;
        private static final int FOOTER = 1;
    }

    private final HomeContract.View homeContract;
    private final Database database;
    private List<Category> categories;
    private boolean showLoader;

    @Inject
    public CategoryAdapter(Activity activity, Database database) {
        this.categories = new ArrayList<>();
        this.showLoader = false;
        this.homeContract = (HomeContract.View) activity;
        this.database = database;
    }

    public void showLoader() {
        this.showLoader = true;
    }

    @Override
    public int getItemCount() {
        return categories.size() + (showLoader ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < categories.size()) {
            return ViewType.NORMAL;
        } else {
            return ViewType.FOOTER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ViewType.FOOTER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.categories_list_loader, parent, false);
                return new FooterViewHolder(view);

            case ViewType.NORMAL:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
                return new CategoryViewHolder(view, homeContract, database);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            Category category = categories.get(position);
            ((CategoryViewHolder) holder).bind(category);
        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).start();
        }
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        private final View view;

        public FooterViewHolder(View view) {
            super(view);
            this.view = view;
        }

        public void start() {
        }
    }
}
