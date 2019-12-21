/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.systemui.util.concurrency;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger Module for classes found within the concurrent package.
 */
@Module
public abstract class ConcurrencyModule {
    /** Background Looper */
    @Provides
    @Singleton
    @Background
    public static Looper provideBgLooper() {
        HandlerThread thread = new HandlerThread("SysUiBg",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        return thread.getLooper();
    }

    /** Main Looper */
    @Provides
    @Main
    public static  Looper provideMainLooper() {
        return Looper.getMainLooper();
    }

    /**
     * Background Handler.
     *
     * Prefer the Background Executor when possible.
     */
    @Provides
    @Background
    public static Handler provideBgHandler(@Background Looper bgLooper) {
        return new Handler(bgLooper);
    }

    /**
     * Main Handler.
     *
     * Prefer the Main Executor when possible.
     */
    @Provides
    @Main
    public static Handler provideMainHandler(@Main Looper mainLooper) {
        return new Handler(mainLooper);
    }

    /**
     * Provide a Background-Thread Executor by default.
     */
    @Provides
    public static Executor provideExecutor(@Background Looper looper) {
        return new ExecutorImpl(new Handler(looper));
    }

    /**
     * Provide a Background-Thread Executor.
     */
    @Provides
    @Background
    public static Executor provideBackgroundExecutor(@Background Looper looper) {
        return new ExecutorImpl(new Handler(looper));
    }

    /**
     * Provide a Main-Thread Executor.
     */
    @Provides
    @Main
    public static Executor provideMainExecutor(Context context) {
        return context.getMainExecutor();
    }

    /**
     * Provide a Background-Thread Executor by default.
     */
    @Provides
    public static DelayableExecutor provideDelayableExecutor(@Background Looper looper) {
        return new ExecutorImpl(new Handler(looper));
    }

    /**
     * Provide a Background-Thread Executor.
     */
    @Provides
    @Background
    public static DelayableExecutor provideBackgroundDelayableExecutor(@Background Looper looper) {
        return new ExecutorImpl(new Handler(looper));
    }

    /**
     * Provide a Main-Thread Executor.
     */
    @Provides
    @Main
    public static DelayableExecutor provideMainDelayableExecutor(@Main Looper looper) {
        return new ExecutorImpl(new Handler(looper));
    }
}
