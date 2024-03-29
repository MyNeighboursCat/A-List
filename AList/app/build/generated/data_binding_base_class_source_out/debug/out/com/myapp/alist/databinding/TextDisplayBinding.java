// Generated by view binder compiler. Do not edit!
package com.myapp.alist.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.myapp.alist.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class TextDisplayBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final CoordinatorLayout coordinatorLayout;

  @NonNull
  public final ScrollView scrollView;

  @NonNull
  public final TextView textDisplayTextView;

  @NonNull
  public final Toolbar toolbar;

  private TextDisplayBinding(@NonNull CoordinatorLayout rootView,
      @NonNull CoordinatorLayout coordinatorLayout, @NonNull ScrollView scrollView,
      @NonNull TextView textDisplayTextView, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.coordinatorLayout = coordinatorLayout;
    this.scrollView = scrollView;
    this.textDisplayTextView = textDisplayTextView;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static TextDisplayBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static TextDisplayBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.text_display, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static TextDisplayBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;

      id = R.id.scrollView;
      ScrollView scrollView = ViewBindings.findChildViewById(rootView, id);
      if (scrollView == null) {
        break missingId;
      }

      id = R.id.textDisplayTextView;
      TextView textDisplayTextView = ViewBindings.findChildViewById(rootView, id);
      if (textDisplayTextView == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new TextDisplayBinding((CoordinatorLayout) rootView, coordinatorLayout, scrollView,
          textDisplayTextView, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
