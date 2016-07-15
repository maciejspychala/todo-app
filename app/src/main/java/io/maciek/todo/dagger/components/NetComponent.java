package io.maciek.todo.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import io.maciek.todo.dagger.modules.AppModule;
import io.maciek.todo.dagger.modules.NetModule;
import io.maciek.todo.viewmodels.MainFragmentViewModel;

/**
 * Created by maciej on 15.07.16.
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(MainFragmentViewModel viewModel);
}
