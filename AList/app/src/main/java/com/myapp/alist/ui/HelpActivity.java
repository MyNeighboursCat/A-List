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
 * Shows the Help text.
 * <p>
 * These details are shown when the Help option is selected in the {@link MainActivity} Action Bar.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
public final class HelpActivity extends AppCompatActivity {
    /**
     * Setup the display including the Action Bar with Up enabled and the Help text.
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

        binding.textDisplayTextView.setText(R.string.help_text);
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
