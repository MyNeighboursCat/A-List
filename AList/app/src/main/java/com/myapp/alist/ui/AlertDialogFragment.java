/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.myapp.alist.R;

/**
 * Creates an <code>AlertDialogFragment</code> setting the title.
 * <p>
 * Only one type of dialog is used at the moment which is a delete warning.  When the OK button is
 * selected, {@link MainActivity#doDeleteOKPositiveClick()} is called.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
// Must be public.
public final class AlertDialogFragment extends DialogFragment {
    // This can't go in string.xml because newInstance is static (would have to use 'this' to access
    // the string).
    /**
     * Key for the string resource ID argument.
     */
    private static final String TITLE = "title";
    /**
     * Class tag used for <code>RuntimeException</code> thrown when the
     * <code>AlertDialogFragment</code> has an illegal argument.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * Creates a new <code>AlertDialogFragment</code>.  Causes {@link #onCreateDialog(Bundle)} to be
     * called.
     *
     * @return the <code>AlertDialogFragment</code> created with the string resource ID set as
     * an argument
     */
    static AlertDialogFragment newInstance() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        // Disable back button.  Must be set on DialogFragment and not AlertDialog.Builder.
        alertDialogFragment.setCancelable(false);
        final Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.TITLE, R.string.delete_warning);
        alertDialogFragment.setArguments(args);

        return alertDialogFragment;
    }

    /**
     * Uses <code>AlertDialog.Builder</code> to build a dialog and sets the title ID to the string
     * resource ID argument.
     *
     * @param savedInstanceState the bundle containing the string resource ID
     * @return the created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = AlertDialogFragment.this.getArguments();
        final MainActivity mainActivity = (MainActivity) this.getActivity();
        if (args == null || mainActivity == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        final int title = args.getInt(AlertDialogFragment.TITLE);
        String message = mainActivity.setCurrentSelectDetails();

        //noinspection Convert2Lambda
        return new AlertDialog.Builder(this.getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (args.getInt(AlertDialogFragment.TITLE) ==
                                        R.string.delete_warning) {
                                    mainActivity.doDeleteOKPositiveClick();
                                } else {
                                    Log.e(AlertDialogFragment.this.TAG,
                                            "onCreateDialog error 1");
                                    throw new RuntimeException();
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mainActivity.doDeleteCancelNegativeClick();
                            }
                        }
                )
                .create();
    }
}
