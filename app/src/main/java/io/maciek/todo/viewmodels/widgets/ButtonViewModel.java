package io.maciek.todo.viewmodels.widgets;

import android.databinding.Bindable;
import android.view.View;

import io.maciek.todo.BR;

/**
 * Created by maciej on 11.07.16.
 */
public class ButtonViewModel extends TextViewModel {

    @Bindable
    public View.OnClickListener onClickListener;

    public ButtonViewModel(String text, View.OnClickListener onClickListener) {
        super(text);
        this.onClickListener = onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        notifyPropertyChanged(BR.onClickListener);
    }
}
