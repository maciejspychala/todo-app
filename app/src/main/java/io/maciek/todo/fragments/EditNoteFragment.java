package io.maciek.todo.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import io.maciek.todo.databinding.FragmentEditNoteBinding;
import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.EditNoteFragmentViewModel;

/**
 * Created by maciej on 12.07.16.
 */
public class EditNoteFragment extends DialogFragment {
    public static final String TAG = EditNoteFragment.class.getSimpleName();
    public static final String NOTE_KEY = "NOTE_KEY";
    public static final String EDIT_TEXT_KEY = "EDIT_TEXT_KEY";
    private EditNoteFragmentViewModel viewModel;
    private View.OnClickListener cancelListener;
    private View.OnClickListener saveListener;


    public static EditNoteFragment newInstance(Note note) {
        Bundle args = new Bundle();
        args.putParcelable(NOTE_KEY, note);
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveListener = view -> {
            getTargetFragment().onActivityResult(1, 1, null);
            viewModel.setNewTitle();
            dismiss();
        };
        cancelListener = view -> dismiss();
        viewModel = new EditNoteFragmentViewModel(getArguments().getParcelable(NOTE_KEY), saveListener, cancelListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentEditNoteBinding binding = FragmentEditNoteBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        if (savedInstanceState != null && savedInstanceState.containsKey(EDIT_TEXT_KEY)) {
            viewModel.setText(savedInstanceState.getString(EDIT_TEXT_KEY));
        }
        setCancelable(false);
        return binding.getRoot();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EDIT_TEXT_KEY, viewModel.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
