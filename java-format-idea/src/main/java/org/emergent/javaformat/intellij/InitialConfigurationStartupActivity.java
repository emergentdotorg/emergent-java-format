/*
 * (c) Copyright 2021 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.emergent.javaformat.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class InitialConfigurationStartupActivity implements StartupActivity.DumbAware, ProjectActivity {

    @Override
    public @Nullable Object execute(@NonNull Project project, @NonNull Continuation<? super Unit> continuation) {
        runActivity(project);
        return null;
    }

    @Override
    public void runActivity(@NotNull Project project) {
        PalantirJavaFormatSettings settings = PalantirJavaFormatSettings.getInstance(project);
        if (settings.isUninitialized()) {
            settings.setEnabled(false);
        }
    }
}
