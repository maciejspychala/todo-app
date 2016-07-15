package io.maciek.todo;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import io.maciek.todo.rest.models.Note;
import io.maciek.todo.sql.TodoDataSource;

/**
 * Created by maciej on 15.07.16.
 */

@RunWith(AndroidJUnit4.class)
public class DataBaseTest {

    private TodoDataSource dataSource;
    private Note note1;
    private Note note2;


    @Before
    public void setup() {
        dataSource = new TodoDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        dataSource.deleteAll();
        note1 = new Note(1, 1, "title", true);
        note2 = new Note(2, 2, "title2", false);
        dataSource.add(note1);
        dataSource.add(note2);
    }

    @Test
    public void testSaving() {
        ArrayList<Note> notes = (ArrayList<Note>) dataSource.getAllNotes();
        Assert.assertEquals(notes.size(), 2);
        if (notes.get(0).getId() == 1) {
            Assert.assertEquals(notes.get(0), note1);
            Assert.assertEquals(notes.get(1), note2);
        } else {
            Assert.assertEquals(notes.get(1), note1);
            Assert.assertEquals(notes.get(0), note2);
        }
    }

    @Test
    public void testUpdating() {
        note1.setTitle("title Udpated");
        note1.setChanged(!note1.isChanged());
        note1.setChanged(!note1.isCompleted());
        note1.setUserId(3);
        dataSource.updateNote(note1);
        ArrayList<Note> notes = (ArrayList<Note>) dataSource.getAllNotes();
        Note result = new Note(5, 5, "wrongTitle", false);
        for (Note note : notes) {
            if (note.getId() == note1.getId()) {
                result = note;
            }
        }
        Assert.assertEquals(note1, result);
    }

    @Test
    public void testAdding() {
        Note note3 = new Note(3, 3, "title3", true);
        dataSource.add(note3);
        ArrayList<Note> notes = (ArrayList<Note>) dataSource.getAllNotes();
        Assert.assertEquals(notes.size(), 3);
        Note result = new Note(5, 5, "wrongTitle", false);
        for (Note note : notes) {
            if (note.getId() == note3.getId()) {
                result = note;
            }
        }
        Assert.assertEquals(result, note3);
    }

    @After
    public void clear(){
        dataSource.deleteAll();
    }
}
