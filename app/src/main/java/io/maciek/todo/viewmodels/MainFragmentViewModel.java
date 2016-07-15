package io.maciek.todo.viewmodels;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import io.maciek.todo.R;
import io.maciek.todo.TodoApp;
import io.maciek.todo.adapters.NotesAdapter;
import io.maciek.todo.interfaces.ErrorToastListener;
import io.maciek.todo.models.LoadingItemModel;
import io.maciek.todo.rest.ApiService;
import io.maciek.todo.rest.models.Note;
import io.maciek.todo.sql.TodoDataSource;
import io.maciek.todo.viewmodels.widgets.BaseViewModel;
import io.maciek.todo.viewmodels.widgets.ButtonViewModel;
import io.maciek.todo.viewmodels.widgets.RecyclerViewModel;
import io.maciek.todo.viewmodels.widgets.TextViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by maciej on 11.07.16.
 */
public class MainFragmentViewModel {

    @Inject
    ApiService apiService;

    public static final int ITEMS_PER_PAGE = 32;
    public ArrayList<Note> notes;
    public LoadingItemModel loadingItemModel = new LoadingItemModel("Loading more items...", true);

    public TextViewModel hello;
    public ButtonViewModel filterButton;
    public ButtonViewModel syncButton;
    public BaseViewModel syncingIcon;
    public RecyclerViewModel list;

    public NotesAdapter adapter;
    public View.OnClickListener filterClickListener;
    public View.OnClickListener sync;

    private AtomicInteger howManyToUpdate = new AtomicInteger(0);
    private int lastItemPos;
    private boolean internetFailed;
    private boolean fetching;

    private boolean filtered = false;
    private TodoDataSource database;
    private ErrorToastListener errorToastListener;

    public void saveNotesToDatabase() {
        for (Note note : notes) {
            if (note.isChanged()) {
                database.updateNote(note);
            }
        }
    }

    public void updateItem(Note whichItemWasEdited) {
        adapter.notifyItemChanged(adapter.getNotes().indexOf(whichItemWasEdited));
        database.updateNote(whichItemWasEdited);
    }

    public void prepare() {
        if (notes.size() == 0) {
            notes.addAll(database.getAllNotes());
        }
        lastItemPos = notes.size();
        adapter.notifyDataSetChanged();
        if (notes.size() == 0) {
            fetchTodos();
        }
    }

    public void internetIsBack() {
        if (isInternetFailed()) {
            fetchTodos();
            setInternetFailed(false);
        }
    }


    public enum State {ERROR, LOADING, ALL_FETCHED}

    private State state = State.LOADING;
    private State oldState = State.LOADING;

    public MainFragmentViewModel(ArrayList<Note> notes, Context context, NoteItemViewModel.NoteClickListener noteClickListener, ErrorToastListener errorToastListener) {
        this.notes = notes;
        this.errorToastListener = errorToastListener;
        setFilterButton();
        syncingIcon = new BaseViewModel(false);
        adapter = new NotesAdapter(notes, loadingItemModel, noteClickListener);

        if (context != null) {
            setDatabase(context);
            setList(context);
            setSyncButton(context);
            hello = new TextViewModel(context.getString(R.string.title));
            ((TodoApp) context.getApplicationContext()).getNetComponent().inject(this);
        }
    }

    private void setSyncButton(Context context) {
        sync = view -> {
            if (howManyToUpdate.get() == 0) {
                ArrayList<Note> toUpdate = new ArrayList<>();
                for (Note note : notes) {
                    if (note.isChanged()) {
                        toUpdate.add(note);
                    }
                }
                howManyToUpdate.set(toUpdate.size());
                if (howManyToUpdate.get() > 0) {
                    syncingIcon.setVisibility(true);
                }
                updateNotes(toUpdate);
            }
        };
        syncButton = new ButtonViewModel(context.getString(R.string.sync), sync);
    }

    private void setDatabase(Context context) {
        database = new TodoDataSource(context);
        database.open();
    }

    private void setList(Context context) {
        list = new RecyclerViewModel();
        list.setAdapter(adapter);
        if (context != null) {
            list.setLayoutManager(new LinearLayoutManager(context));
        }
        list.setFixedSize(true);
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                list.scrollPosition = (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
                if ((((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()) > lastItemPos - 2 && !internetFailed) {
                    fetchTodos();
                }
            }
        };
        list.setScrollListener(scrollListener);
    }

    private void setFilterButton() {
        filterClickListener = view -> {
            if (notes.size() > 0) {
                if (filtered) {
                    adapter.getFilter().filter(NotesAdapter.ALL);
                    switch (oldState) {
                        case LOADING:
                            loading();
                            break;
                        case ALL_FETCHED:
                            allFetched();
                            break;
                        case ERROR:
                            onError();
                            break;
                    }
                    filterButton.setText(NotesAdapter.CHANGED);
                } else {
                    oldState = state;
                    adapter.getFilter().filter(NotesAdapter.CHANGED);
                    allFetched();
                    filterButton.setText(NotesAdapter.ALL);
                }
                filtered = !filtered;
            }
        };
        filterButton = new ButtonViewModel(NotesAdapter.CHANGED, filterClickListener);
    }

    public void allFetched() {
        loadingItemModel.allFetched();
        adapter.notifyItemChanged(loadingItemModel.getPosition());
        state = State.ALL_FETCHED;
    }

    public void onError() {
        loadingItemModel.onError();
        adapter.notifyItemChanged(loadingItemModel.getPosition());
        state = State.ERROR;
    }

    public void loading() {
        loadingItemModel.loading();
        adapter.notifyItemChanged(loadingItemModel.getPosition());
        state = State.LOADING;
    }

    public boolean isFiltered() {
        return filtered;
    }

    private void updateNotes(ArrayList<Note> toUpdate) {
        for (Note noteToUpdate : toUpdate) {
            Observable<Note> postTodo = apiService.postTodo(Integer.toString(noteToUpdate.getId()), noteToUpdate);
            postTodo.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(throwable -> {
                        if (throwable.getMessage().toLowerCase().contains("internal")) {
                            noteToUpdate.setChanged(false);
                            adapter.notifyItemChanged(adapter.getNotes().indexOf(noteToUpdate));
                            if (database != null) {
                                database.updateNote(noteToUpdate);
                            }
                            if (howManyToUpdate.decrementAndGet() <= 0) {
                                syncingIcon.setVisibility(false);
                            }
                        } else {
                            syncingIcon.setVisibility(false);
                            howManyToUpdate.set(0);
                            errorToastListener.showToast();
                        }
                    })
                    .onErrorResumeNext(throwable1 -> Observable.empty())
                    .subscribe(note -> {
                        noteToUpdate.setChanged(false);
                        adapter.notifyItemChanged(adapter.getNotes().indexOf(noteToUpdate));
                        if (database != null) {
                            database.updateNote(noteToUpdate);
                        }
                        if (howManyToUpdate.decrementAndGet() <= 0) {
                            syncingIcon.setVisibility(false);
                        }
                    });
        }
    }


    public void fetchTodos() {
        if (!fetching) {
            fetching = true;
            loading();
            Observable<List<Note>> getTodos = apiService.getTodos(lastItemPos, lastItemPos + ITEMS_PER_PAGE);
            getTodos.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(throwable -> {
                        fetching = false;
                        onError();
                        internetFailed = true;
                    })
                    .onErrorResumeNext(throwable1 -> Observable.empty())
                    .subscribe(notesReceived -> {
                        if (notesReceived != null && notesReceived.size() > 0) {
                            notes.addAll(notesReceived);
                            database.add(notesReceived);
                            adapter.notifyItemRangeChanged(lastItemPos, ITEMS_PER_PAGE + 1);
                            lastItemPos = notes.size();
                            fetching = false;
                        } else {
                            allFetched();
                        }
                    });

        }
    }

    public boolean isInternetFailed() {
        return internetFailed;
    }

    public void setInternetFailed(boolean internetFailed) {
        this.internetFailed = internetFailed;
    }
}
