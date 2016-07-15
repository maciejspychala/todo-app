package io.maciek.todo.viewmodels;

import io.maciek.todo.models.LoadingItemModel;
import io.maciek.todo.viewmodels.widgets.BaseViewModel;
import io.maciek.todo.viewmodels.widgets.TextViewModel;

/**
 * Created by maciej on 13.07.16.
 */
public class LoadingItemViewModel {
    public TextViewModel title;
    public BaseViewModel progress;

    public LoadingItemViewModel(LoadingItemModel model) {
        title = new TextViewModel(model.getText());
        progress = new BaseViewModel(model.isProgressBarVisibility());
    }
}
