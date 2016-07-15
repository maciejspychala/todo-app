package io.maciek.todo.viewmodels;

import android.view.View;
import android.widget.CompoundButton;

import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.widgets.BaseViewModel;
import io.maciek.todo.viewmodels.widgets.CheckBoxViewModel;
import io.maciek.todo.viewmodels.widgets.TextViewModel;

/**
 * Created by maciej on 11.07.16.
 */
public class NoteItemViewModel implements CompoundButton.OnCheckedChangeListener {
    public TextViewModel title;
    public CheckBoxViewModel checkBox;
    public BaseViewModel sync;
    public Note note;

    public NoteItemViewModel(Note model) {
        this.note = model;
        sync = new BaseViewModel(note.isChanged() ? View.VISIBLE : View.INVISIBLE);
        title = new TextViewModel(note.getTitle());
        checkBox = new CheckBoxViewModel(model.isCompleted(), this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isPressed()) {
            note.setCompleted(b);
            note.setChanged(true);
            sync.setVisibility(note.isChanged() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public interface NoteClickListener {
        void onNoteClick(int id);
    }
}
