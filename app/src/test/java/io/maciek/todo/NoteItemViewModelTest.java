package io.maciek.todo;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.NoteItemViewModel;

/**
 * Created by maciej on 15.07.16.
 */
public class NoteItemViewModelTest {

    private NoteItemViewModel noteViewModel;
    private Note note;

    @Before
    public void setup() {
        note = new Note(1, 2, "title", true);
        noteViewModel = new NoteItemViewModel(note);
    }

    @Test
    public void testViewsCorrectValues() throws Exception {
        Assert.assertSame(note.isCompleted(), noteViewModel.checkBox.checked);
        Assert.assertSame(note.getTitle(), noteViewModel.title.text);
        Assert.assertFalse(noteViewModel.sync.visibility == View.VISIBLE);
    }

    @Test
    public void testClickCheckBox() {
        CheckBox checkBox = Mockito.mock(CheckBox.class);
        Mockito.when(checkBox.isPressed()).thenReturn(true);

        noteViewModel.onCheckedChanged(checkBox, !note.isCompleted());
        Assert.assertNotEquals(note.isCompleted(), noteViewModel.checkBox.checked);
        Assert.assertSame(note.getTitle(), noteViewModel.title.text);
        Assert.assertTrue(noteViewModel.sync.visibility == View.VISIBLE);
    }
}
