package io.maciek.todo.viewmodels.widgets;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import io.maciek.todo.BR;

/**
 * Created by maciej on 11.07.16.
 */
public class BaseViewModel extends BaseObservable {

    public BaseViewModel() {
    }

    public BaseViewModel(int visibility) {
        setVisibility(visibility);
    }

    public BaseViewModel(boolean visibility) {
        setVisibility(visibility);
    }

    @Bindable
    public int visibility;

    public void setVisibility(boolean visible) {
        visibility = visible ? View.VISIBLE : View.GONE;
        notifyPropertyChanged(BR.visibility);
    }

    public void setVisibility(int visiblity) {
        this.visibility = visiblity;
        notifyPropertyChanged(BR.visibility);
    }
}
