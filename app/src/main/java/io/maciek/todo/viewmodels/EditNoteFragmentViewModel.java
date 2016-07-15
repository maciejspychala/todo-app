package io.maciek.todo.viewmodels;

import android.view.View;

import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.widgets.EditTextViewModel;

/**
 * Created by maciej on 12.07.16.
 */
public class EditNoteFragmentViewModel {
    public EditTextViewModel titleEditText;
    public View.OnClickListener onSaveClickListener;
    public View.OnClickListener onCancelClickListener;
    private Note note;

    public EditNoteFragmentViewModel(Note note, View.OnClickListener save, View.OnClickListener cancel) {
        this.note = note;
        titleEditText = new EditTextViewModel(note.getTitle());
        onSaveClickListener = save;
        onCancelClickListener = cancel;
    }

    public void setText(String text) {
        titleEditText.setText(text);
    }

    public String getText() {
        return titleEditText.text;
    }

    public void setNewTitle() {
        if (!getText().equals(note.getTitle())) {
            note.setChanged(true);
            note.setTitle(getText());
        }
    }
}
