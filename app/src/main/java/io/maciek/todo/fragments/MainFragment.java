package io.maciek.todo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import io.maciek.todo.R;
import io.maciek.todo.databinding.FragmentMainBinding;
import io.maciek.todo.interfaces.ErrorToastListener;
import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.MainFragmentViewModel;
import io.maciek.todo.viewmodels.NoteItemViewModel;

/**
 * Created by maciej on 11.07.16.
 */

public class MainFragment extends Fragment implements NoteItemViewModel.NoteClickListener, ErrorToastListener {


    public static final String TAG = MainFragment.class.getSimpleName();
    public static final String EDITED_NOTE = "EDITED_NOTE";
    public static final String FILTERED = "FILTERED";
    public static final String ALL_ITEMS = "ALL_ITEMS";
    public static final String SCROLL_POSITION = "SCROLL_POSITION";

    private MainFragmentViewModel viewModel;
    private ArrayList<Note> notes = new ArrayList<>();
    private NetworkChangeReceiver receiver;
    private Note whichItemWasEdited;
    private Toast toastWrong;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInternetConnectivityChangeListener();
        toastWrong = Toast.makeText(getContext(), getText(R.string.something_wrong), Toast.LENGTH_SHORT);
        setViewModel();
        resolveSavedInstance(savedInstanceState);
        viewModel.prepare();
    }

    private void resolveSavedInstance(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            whichItemWasEdited = savedInstanceState.getParcelable(EDITED_NOTE);
            notes.clear();
            notes.addAll(savedInstanceState.getParcelableArrayList(ALL_ITEMS));
            viewModel.list.scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
            if (savedInstanceState.getBoolean(FILTERED, false)) {
                viewModel.filterClickListener.onClick(null);
            }
        }
    }

    private void setViewModel() {
        viewModel = new MainFragmentViewModel(notes, getContext(), this, this);
    }


    private void setInternetConnectivityChangeListener() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainBinding binding = FragmentMainBinding.inflate(getLayoutInflater(savedInstanceState), container, false);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EDITED_NOTE, whichItemWasEdited);
        outState.putBoolean(FILTERED, viewModel.isFiltered());
        outState.putParcelableArrayList(ALL_ITEMS, notes);
        outState.putInt(SCROLL_POSITION, viewModel.list.scrollPosition);
    }

    @Override
    public void onNoteClick(int id) {
        EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(notes.get(id - 1));
        editNoteFragment.setTargetFragment(this, 1);
        editNoteFragment.show(getFragmentManager(), EditNoteFragment.TAG);
        whichItemWasEdited = notes.get(id - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && viewModel != null) {
            viewModel.updateItem(whichItemWasEdited);
        }
    }


    private void onNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
                Handler handler = new Handler();
                handler.postDelayed(() -> viewModel.internetIsBack(), 1000);
            }
        }
    }

    @Override
    public void showToast() {
        toastWrong.show();
    }


    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            onNetworkAvailable(context);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.saveNotesToDatabase();
    }
}
