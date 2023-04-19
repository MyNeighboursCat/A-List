/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.ui;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.myapp.alist.R;
import com.myapp.alist.databinding.TextDisplayBinding;

/**
 * Shows the About text.
 * <p>
 * These details are shown when the About option is selected in the {@link MainActivity} Action Bar.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
public final class AboutActivity extends AppCompatActivity {
    /**
     * Sets up the display including the Action Bar with Up enabled and the About information
     * comprised of the app name, version number and copyright message.
     *
     * @param savedInstanceState the bundle to be restored.  May be <code>null</code>.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure only the music stream volume is adjusted.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final TextDisplayBinding binding = TextDisplayBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());
        this.setSupportActionBar(binding.toolbar);

        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final String string1 = this.getString(R.string.app_name)
                + "\n\n" + this.getString(R.string.version) + ": "
                + this.getString(R.string.application_version_name) + "\n\n"
                + this.getString(R.string.copyright) + " "
                + this.getString(R.string.creation_year) + " "
                + this.getString(R.string.my_name) + ".  "
                + this.getString(R.string.all_rights_reserved);
        binding.textDisplayTextView.setText(string1);
    }

    /**
     * Handles the Up button press in the Action Bar.
     *
     * @param item the menu item selected
     * @return true if Up pressed or the boolean returned from the activity
     * <code>onOptionsItemSelected(MenuItem)</code>
     * @see Activity#onOptionsItemSelected(MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button.
        if (item.getItemId() == android.R.id.home) {
            // Return to existing instance of the calling activity rather than create a new one.
            // This keeps the existing state of the calling activity.
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
