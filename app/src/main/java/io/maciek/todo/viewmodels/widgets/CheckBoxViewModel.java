package io.maciek.todo.viewmodels.widgets;

import android.databinding.Bindable;
import android.widget.CompoundButton;

import io.maciek.todo.BR;

/**
 * Created by maciej on 12.07.16.
 */
public class CheckBoxViewModel extends BaseViewModel {

    @Bindable
    public CompoundButton.OnCheckedChangeListener checkListener;


    @Bindable
    public boolean checked;

    public void setChecked(boolean checked) {
        this.checked = checked;
        notifyPropertyChanged(BR.checked);
    }

    public CheckBoxViewModel(boolean checked, CompoundButton.OnCheckedChangeListener listener) {
        this.checked = checked;
        notifyPropertyChanged(BR.checked);
        this.checkListener = listener;
        notifyPropertyChanged(BR.checkListener);
    }
}
