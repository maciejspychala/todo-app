package io.maciek.todo;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.EditNoteFragmentViewModel;

/**
 * Created by maciej on 15.07.16.
 */
public class EditNoteFragmentViewModelTest {


    private EditNoteFragmentViewModel editNoteViewModel;
    private Note note;
    private View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editNoteViewModel.setNewTitle();
        }
    };


    @Before
    public void setup() {
        note = new Note(1, 2, "title", true);
        editNoteViewModel = new EditNoteFragmentViewModel(note, save, null);
    }

    @Test
    public void testViewsCorrectValues() {
        Assert.assertSame(note.getTitle(), editNoteViewModel.getText());
    }

    @Test
    public void testSaveWithoutChange() {
        editNoteViewModel.onSaveClickListener.onClick(null);
        Assert.assertSame(note.getTitle(), editNoteViewModel.getText());
        Assert.assertFalse(note.isChanged());
    }

    @Test
    public void testSaveWithChange() {
        editNoteViewModel.setText("title2");
        editNoteViewModel.onSaveClickListener.onClick(null);
        Assert.assertSame(note.getTitle(), "title2");
        Assert.assertTrue(note.isChanged());
    }
}
