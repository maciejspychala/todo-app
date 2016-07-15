package io.maciek.todo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import io.maciek.todo.rest.ApiService;
import io.maciek.todo.rest.models.Note;
import io.maciek.todo.viewmodels.MainFragmentViewModel;

/**
 * Created by maciej on 15.07.16.
 */
public class MainFragmentViewModelTest {

    private MainFragmentViewModel mainFragmentViewModel;
    private ArrayList<Note> notes;
    private Note note1;
    private Note note2;

    @Before
    public void setup() {
        notes = new ArrayList<>();
        note1 = new Note(1, 1, "title", true);
        note2 = new Note(2, 2, "title2", false);
        notes.add(note1);
        notes.add(note2);
        mainFragmentViewModel = new MainFragmentViewModel(notes, null, null, null);
    }

    @Test
    public void testAdapter() {
        Assert.assertEquals(mainFragmentViewModel.adapter.getNotes().size(), 2);
    }

}
