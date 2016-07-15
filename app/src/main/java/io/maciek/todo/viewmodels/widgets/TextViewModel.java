package io.maciek.todo.viewmodels.widgets;

import android.databinding.Bindable;

import io.maciek.todo.BR;

/**
 * Created by maciej on 11.07.16.
 */
public class TextViewModel extends BaseViewModel {
    @Bindable
    public String text;

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    public String getText() {
        return text;
    }

    public TextViewModel(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }
}
