/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.ui;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Handles the selection of normal text and hyperlinks.
 * <p>
 * Distinguishes between the selection of normal text and a hyperlink.  Allows the clicking of a
 * hyperlink.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
final class HyperlinkMovementMethod extends ArrowKeyMovementMethod {
    /**
     * The unique instance of the <code>HyperlinkMovementMethod</code>.
     */
    private static HyperlinkMovementMethod sInstance;

    /**
     * Creates and returns the unique instance of the <code>HyperlinkMovementMethod</code>.
     */
    static MovementMethod getHyperlinkMovementMethodInstance() {
        if (HyperlinkMovementMethod.sInstance == null) {
            HyperlinkMovementMethod.sInstance = new HyperlinkMovementMethod();
        }

        return HyperlinkMovementMethod.sInstance;
    }

    /**
     * Distinguishes between the selection of normal text and a hyperlink.  Allows the clicking of a
     * hyperlink.
     *
     * @param widget the <code>TextView</code> touched by the user
     * @param buffer the <code>Spannable</code>
     * @param event  the <code>MotionEvent</code>
     * @return the <code>boolean</code> to indicate whether the event has been handled
     */
    @Override
    public boolean onTouchEvent(final TextView widget, final Spannable buffer, final MotionEvent
            event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            // Need to allow for the scrolled position of the text which is in the EditText.
            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            // This is finding the start position of the hyperlink in the string of the text which
            // is in the EditText.
            int off = layout.getOffsetForHorizontal(line, x);

            // Start and end are set to the start position of the hyperlink so there should only be
            // one element in the returned array.
            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            // If there is a hyperlink then...
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    // Up occurs shortly after down (before select all, copy etc can appear) so acts
                    // as a click.
                    link[0].onClick(widget);
                } else {
                    // Select the text as normal if just down.
                    // Up never happens if select all, copy etc appears first i.e. user press
                    // down
                    // for a long time.
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    /**
     * Releases resources when leaving this app.
     */
    void releaseResources() {
        HyperlinkMovementMethod.sInstance = null;
    }
}
