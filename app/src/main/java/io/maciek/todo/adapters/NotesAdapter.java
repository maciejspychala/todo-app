package io.maciek.todo.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import io.maciek.todo.R;
import io.maciek.todo.databinding.LoadingItemBinding;
import io.maciek.todo.databinding.NoteItemBinding;
import io.maciek.todo.models.LoadingItemModel;
import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.LoadingItemViewModel;
import io.maciek.todo.viewmodels.NoteItemViewModel;

/**
 * Created by maciej on 11.07.16.
 */
public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    public static final String ALL = "ALL";
    public static final String CHANGED = "CHANGED";

    private ArrayList<Note> notes;
    private LoadingItemModel loadingItemModel;
    private NoteItemViewModel.NoteClickListener itemClickListener;

    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ITEM = 1;
    private TodoFilter todoFilter;

    public NotesAdapter(ArrayList<Note> notes, LoadingItemModel loadingItemModel, NoteItemViewModel.NoteClickListener noteClickListener) {
        this.notes = notes;
        this.loadingItemModel = loadingItemModel;
        itemClickListener = noteClickListener;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
            NoteViewHolder holder = new NoteViewHolder(v);
            return holder;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoteViewHolder) {
            Note note = notes.get(position);
            ((NoteViewHolder) holder).getBinding().setViewModel(new NoteItemViewModel(note));
            ((NoteViewHolder) holder).getBinding().getRoot().setOnClickListener(view -> {
                if (itemClickListener != null) {
                    itemClickListener.onNoteClick(note.getId());
                }
            });
            ((NoteViewHolder) holder).getBinding().executePendingBindings();
        } else {
            ((LoadingViewHolder) holder).getBinding().setViewModel(new LoadingItemViewModel(loadingItemModel));
            loadingItemModel.setPosition(position);
        }
    }

    @Override
    public int getItemCount() {
        return notes != null ? notes.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= notes.size()) ? VIEW_TYPE_LOADING
                : VIEW_TYPE_ITEM;
    }

    @Override
    public Filter getFilter() {
        if (todoFilter == null) {
            todoFilter = new TodoFilter(this, notes);
        }
        return todoFilter;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private NoteItemBinding binding;

        public NoteViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public NoteItemBinding getBinding() {
            return binding;
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private LoadingItemBinding binding;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public LoadingItemBinding getBinding() {
            return binding;
        }
    }

    public class TodoFilter extends Filter {
        NotesAdapter adapter;
        ArrayList<Note> original;

        public TodoFilter(NotesAdapter notesAdapter, ArrayList<Note> notes) {
            original = notes;
            this.adapter = notesAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (charSequence.toString().equals(ALL)) {
                results.values = original;
                results.count = original.size();
            } else {
                ArrayList<Note> filtered = new ArrayList<>();
                for (Note note : original) {
                    if (note.isChanged()) {
                        filtered.add(note);
                    }
                }
                results.values = filtered;
                results.count = filtered.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.notes = ((ArrayList<Note>) filterResults.values);
            adapter.notifyDataSetChanged();
        }
    }
}
