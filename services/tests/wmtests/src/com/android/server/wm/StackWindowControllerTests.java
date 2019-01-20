/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.server.wm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Rect;
import android.platform.test.annotations.Presubmit;

import androidx.test.filters.SmallTest;

import org.junit.Test;

/**
 * Test class for {@link StackWindowController}.
 *
 * Build/Install/Run:
 *  atest FrameworksServicesTests:StackWindowControllerTests
 */
@SmallTest
@Presubmit
public class StackWindowControllerTests extends WindowTestsBase {
    @Test
    public void testRemoveContainer() {
        final StackWindowController stackController =
                createStackControllerOnDisplay(mDisplayContent);
        final WindowTestUtils.TestTask task = WindowTestUtils.createTestTask(stackController);

        final TaskStack stack = stackController.mContainer;
        assertNotNull(stack);
        assertNotNull(task);
        stackController.removeContainer();
        // Assert that the container was removed.
        assertNull(stackController.mContainer);
        assertNull(stack.getDisplayContent());
        assertNull(task.getDisplayContent());
        assertNull(task.mStack);
    }

    @Test
    public void testRemoveContainer_deferRemoval() {
        final StackWindowController stackController =
                createStackControllerOnDisplay(mDisplayContent);
        final WindowTestUtils.TestTask task = WindowTestUtils.createTestTask(stackController);

        final TaskStack stack = stackController.mContainer;
        // Stack removal is deferred if one of its child is animating.
        task.setLocalIsAnimating(true);

        stackController.removeContainer();
        // For the case of deferred removal the stack controller will no longer be connected to the
        // container, but the task controller will still be connected to the its container until
        // the stack window container is removed.
        assertNull(stackController.mContainer);
        assertNull(stack.getController());
        assertNotNull(task);

        stack.removeImmediately();
        // After removing, the task will be isolated.
        assertNull(task.getParent());
        assertEquals(task.getChildCount(), 0);
        assertNull(task.getController());
    }

    @Test
    public void testReparent() {
        // Create first stack on primary display.
        final StackWindowController stack1Controller =
                createStackControllerOnDisplay(mDisplayContent);
        final TaskStack stack1 = stack1Controller.mContainer;
        final WindowTestUtils.TestTask task1 = WindowTestUtils.createTestTask(stack1Controller);
        task1.mOnDisplayChangedCalled = false;

        // Create second display and put second stack on it.
        final DisplayContent dc = createNewDisplay();
        final StackWindowController stack2Controller =
                createStackControllerOnDisplay(dc);
        final TaskStack stack2 = stack2Controller.mContainer;

        // Reparent
        stack1Controller.reparent(dc.getDisplayId(), new Rect(), true /* onTop */);
        assertEquals(dc, stack1.getDisplayContent());
        final int stack1PositionInParent = stack1.getParent().mChildren.indexOf(stack1);
        final int stack2PositionInParent = stack1.getParent().mChildren.indexOf(stack2);
        assertEquals(stack1PositionInParent, stack2PositionInParent + 1);
        assertTrue(task1.mOnDisplayChangedCalled);
    }
}
