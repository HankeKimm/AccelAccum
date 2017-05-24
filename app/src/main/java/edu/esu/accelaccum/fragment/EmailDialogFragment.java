package edu.esu.accelaccum.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;


import edu.esu.accelaccum.R;

/**
 * Created by hanke.kimm on 5/22/17.
 */

public class EmailDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText mEmailAddress;

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EmailDialogListener activity = (EmailDialogListener) getActivity();
            activity.onFinishEnterEmail(mEmailAddress.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }

    public interface EmailDialogListener {
        void onFinishEnterEmail(String inputText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_dialog, container);
        mEmailAddress = (EditText) view.findViewById(R.id.emailAddress);
        mEmailAddress.setOnEditorActionListener(this);
        return view;
    }

}
