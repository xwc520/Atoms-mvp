/*
 * Copyright 2017 Vea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vea.atoms.mvp.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.vea.atoms.mvp.di.component.AppComponent;
import com.vea.atoms.mvp.utils.AtomsUtils;
import com.vea.atoms.mvp.utils.Preconditions;

import timber.log.Timber;

/**
 * ================================================
 * BaseApplication每个模块的Application都使用B
 * <p>
 * Created by Vea on 2018/8/20
 * ================================================
 */
public class BaseApplication extends Application implements IApp {

    private AppLifecycles mAppDelegate;

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks;

    private static BaseApplication mApplication;

    public static BaseApplication getInstance() {
        return mApplication;
    }

    public static void setApplication(BaseApplication mApplication) {
        BaseApplication.mApplication = mApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

        if (mAppDelegate == null) {
            this.mAppDelegate = new AppDelegate(base);
        }
        this.mAppDelegate.attachBaseContext(this, base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
        if (mAppDelegate != null) {
            this.mAppDelegate.onCreate(this);
        }

        Timber.d("BaseApplication#onCreate");

        mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                ActivityManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityManager.getAppManager().removeActivity(activity);
            }
        };

        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mAppDelegate != null)
            this.mAppDelegate.onTerminate(this);
        unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    /**
     * 将 {@link AppComponent} 返回出去, 供其它地方使用, {@link AppComponent} 接口中声明的方法所返回的实例, 在 {@link #getAppComponent()} 拿到对象后都可以直接使用
     *
     * @return AppComponent
     * @see AtomsUtils#obtainAppComponentFromContext(Context) 可直接获取 {@link AppComponent}
     */
    @NonNull
    @Override
    public AppComponent getAppComponent() {
        Preconditions.checkNotNull(mAppDelegate, "%s cannot be null", AppDelegate.class.getName());
        Preconditions.checkState(mAppDelegate instanceof IApp, "%s must be implements %s", AppDelegate.class.getName(), IApp.class.getName());
        return ((IApp) mAppDelegate).getAppComponent();
    }
}
