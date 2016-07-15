package io.maciek.todo;

import android.app.Application;

import io.maciek.todo.dagger.components.DaggerNetComponent;
import io.maciek.todo.dagger.components.NetComponent;
import io.maciek.todo.dagger.modules.AppModule;
import io.maciek.todo.dagger.modules.NetModule;

/**
 * Created by maciej on 14.07.16.
 */
public class TodoApp extends Application {
    private NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initComponent();
    }

    private void initComponent() {

        netComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule("http://128.199.63.179:3000/"))
                .build();

    }

    public NetComponent getNetComponent() {
        return netComponent;
    }

}
