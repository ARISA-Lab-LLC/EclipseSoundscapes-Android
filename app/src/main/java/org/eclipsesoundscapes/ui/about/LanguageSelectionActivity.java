package org.eclipsesoundscapes.ui.about;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.ui.base.BaseActivity;
import org.eclipsesoundscapes.util.LocaleUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LanguageSelectionActivity extends BaseActivity {

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.language));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dataManager = ((EclipseSoundscapesApp)getApplication()).getDataManager();

        final RecyclerView recyclerView = findViewById(R.id.language_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LanguageAdapter(getSupportedLanguages()));
    }

    private ArrayList<Locale> getSupportedLanguages() {
        final ArrayList<Locale> languages = new ArrayList<>();
        final String[] locales = getResources().getStringArray(R.array.supported_languages_locale);

        for (String locale : locales) {
            languages.add(new Locale(locale));
        }

        return languages;
    }

    private Locale getCurrentLanguage() {
        final Locale selectedLocale = getSelectedLanguage();
        if (selectedLocale == null) {
            return Locale.getDefault();
        }

        return selectedLocale;
    }

    private void setLanguage(final Locale locale) {
        dataManager.setLanguage(locale.getLanguage());
        LocaleUtils.updateLocale(this);
        reload();
    }

    private void reload() {
        final Intent intent = new Intent(this, getClass());
        // clear current stack so when we navigate back previous activities are re-created
        // and resources are reloaded
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Nullable
    private Locale getSelectedLanguage() {
        if (!dataManager.getLanguage().isEmpty()) {
            return new Locale(dataManager.getLanguage());
        }

        return null;
    }

    public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

        private ArrayList<Locale> languages = new ArrayList<>();

        private Locale selectedLanguage;

        LanguageAdapter(final ArrayList<Locale> languages) {
            this.languages.addAll(languages);
            this.selectedLanguage = getSelectedLanguage();
        }

        @NonNull
        @Override
        public LanguageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_language, viewGroup, false);
            return new LanguageAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final LanguageAdapter.ViewHolder viewHolder, int i) {
            final Locale language = languages.get(i);
            viewHolder.language.setText(language.getDisplayName(getCurrentLanguage()));
            viewHolder.language.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedLanguage == null || !selectedLanguage.getLanguage().equals(language.getLanguage())) {
                        selectedLanguage = language;
                        setLanguage(selectedLanguage);
                    }
                }
            });

            if (selectedLanguage != null && language.equals(selectedLanguage)) {
                viewHolder.language.setChecked(true);
                viewHolder.language.setCheckMarkDrawable(R.drawable.ic_check_white);
            } else {
                viewHolder.language.setChecked(false);
                viewHolder.language.setCheckMarkDrawable(null);
            }
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.language) CheckedTextView language;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed();
            finish();
        } else {
            // locale has been updated and task cleared
            final Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_SETTINGS_MODE, SettingsActivity.MODE_SETTINGS);

            finish();
            startActivity(intent);
        }

        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }
}
