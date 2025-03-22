package mm.pndaza.tipitakamyanmar.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;

import mm.pndaza.tipitakamyanmar.R;

/**
 * Helper class to manage iOS-style progress indicator
 */
public class IOSProgressDialog {

    private Dialog dialog;
    private TextView messageTextView;
    private ProgressBar progressBar;

    /**
     * Shows an iOS-style progress indicator with the given message
     *
     * @param context The context to use
     * @param message The message to display
     * @return The created dialog instance
     */
    public Dialog showProgressDialog(Context context, String message) {
        // Create dialog without a title
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set transparent background for the dialog window
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Inflate and set the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.ios_progress_dialog, null);
        dialog.setContentView(dialogView);

        // Find views
        messageTextView = dialogView.findViewById(R.id.progressMessage);
        progressBar = dialogView.findViewById(R.id.progressBar);

        // Set initial message
        if (message != null && !message.isEmpty()) {
            messageTextView.setText(message);
        }

        // Make the dialog non-cancelable
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Show the dialog
        dialog.show();
        return dialog;
    }

    /**
     * Dismisses the dialog if it's showing
     */
    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (IllegalArgumentException e) {
                // Handle potential window leaked exception
            }
            dialog = null;
            messageTextView = null;
            progressBar = null;
        }
    }

    /**
     * Updates the dialog message
     *
     * @param message The new message to display
     */
    public void setMessage(String message) {
        if (dialog != null && dialog.isShowing() && messageTextView != null) {
            messageTextView.setText(message);
        }
    }

    /**
     * Checks if the dialog is currently showing
     *
     * @return true if the dialog is showing, false otherwise
     */
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}
