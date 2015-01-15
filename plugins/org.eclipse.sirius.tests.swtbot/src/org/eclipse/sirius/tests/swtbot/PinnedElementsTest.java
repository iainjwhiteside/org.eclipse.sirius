/*******************************************************************************
 * Copyright (c) 2010, 2014 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.tests.swtbot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramElementEditPart;
import org.eclipse.sirius.diagram.ui.tools.internal.preferences.SiriusDiagramUiInternalPreferencesKeys;
import org.eclipse.sirius.tests.swtbot.sequence.condition.CheckNoOpenedSessionInModelContentView;
import org.eclipse.sirius.tests.swtbot.support.api.business.UIResource;
import org.eclipse.sirius.tests.swtbot.support.api.view.DesignerViews;
import org.eclipse.sirius.tests.swtbot.support.utils.SWTBotUtils;

/**
 * Tests for the "pin elements" feature.
 * 
 * @author pcdavid
 */
public class PinnedElementsTest extends AbstractPinnedElementsTest {

    protected static final String VIEWPOINT_NAME = "Tests Cases for ticket #1825 (partial layout)";

    private static final String MODEL = "model/tc1825.ecore";

    private static final String SESSION_FILE = "model/tc1825.aird";

    private static final String VSM_FILE = "description/tc1825.odesign";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSetUpBeforeClosingWelcomePage() throws Exception {
        copyFileToTestProject(Activator.PLUGIN_ID, DATA_UNIT_DIR, MODEL, SESSION_FILE, VSM_FILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSetUpAfterOpeningDesignerPerspective() throws Exception {
        bot.viewById("org.eclipse.ui.views.ContentOutline").close();
        SWTBotUtils.waitAllUiEvents();
        sessionAirdResource = new UIResource(designerProject, FILE_DIR, "tc1825.aird");
        localSession = designerPerspective.openSessionFromFile(sessionAirdResource);
    }

    @Override
    protected void tearDown() throws Exception {
        editor.close();
        SWTBotUtils.waitAllUiEvents();
        // Reopen outline
        new DesignerViews(bot).openOutlineView();
        SWTBotUtils.waitAllUiEvents();
        super.tearDown();
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testMovingElementsSetsPinnedIfPreferenceEnabled() throws Exception {
        changeDiagramUIPreference(SiriusDiagramUiInternalPreferencesKeys.PREF_AUTO_PIN_ON_MOVE.name(), true);

        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_All_Unpinned");
        final IDiagramElementEditPart class1 = (IDiagramElementEditPart) editor.getEditPart("Class1").part();
        assertThat(class1, not(isPinnedMatcher()));
        editor.drag(250, 115, 260, 130);
        bot.waitUntil(waitForPinned(class1));
        assertThat("Moved element should pinned if AUTO_PIN_ON_MOVE is enabled.", class1, isPinnedMatcher());
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testMovingElementsDoesNotSetsPinnedIfPreferenceDisabled() throws Exception {
        changeDiagramUIPreference(SiriusDiagramUiInternalPreferencesKeys.PREF_AUTO_PIN_ON_MOVE.name(), false);

        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_All_Unpinned");
        final IDiagramElementEditPart class1 = (IDiagramElementEditPart) editor.getEditPart("Class1").part();
        assertThat(class1, not(isPinnedMatcher()));
        editor.drag(250, 115, 260, 130);
        bot.waitUntil(waitForNotPinned(class1));
        assertThat("Moved element should not be pinned if AUTO_PIN_ON_MOVE is disabled.", class1, not(isPinnedMatcher()));
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testPinElementContextualMenuAction() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_All_Unpinned");
        final IDiagramElementEditPart class1 = (IDiagramElementEditPart) editor.getEditPart("Class1").part();
        assertThat(class1, not(isPinnedMatcher()));
        editor.getSelectableEditPart("Class1").select();
        editor.clickContextMenu("Pin selected elements");
        bot.waitUntil(waitForPinned(class1));
        assertThat(class1, isPinnedMatcher());
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testPinnedAttributeIsPersistent() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_All_Unpinned");
        IDiagramElementEditPart class1 = (IDiagramElementEditPart) editor.getEditPart("Class1").part();
        assertThat(class1, not(isPinnedMatcher()));
        editor.getSelectableEditPart("Class1").select();
        editor.clickContextMenu("Pin selected elements");
        bot.waitUntil(waitForPinned(class1));
        assertThat(class1, isPinnedMatcher());
        localSession.close(true);

        bot.waitUntil(new CheckNoOpenedSessionInModelContentView(bot, SESSION_FILE));

        localSession = designerPerspective.openSessionFromFile(sessionAirdResource);
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_All_Unpinned");
        class1 = (IDiagramElementEditPart) editor.getEditPart("Class1").part();
        assertThat(class1, isPinnedMatcher());
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Flat_All_Unpinned() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_All_Unpinned");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Flat_All_Pinned() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_All_Pinned");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertNoBoundChanged(initialBounds, finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Flat_Some_Pinned_No_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_Some_Pinned_No_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Flat_Some_Pinned_Solvable_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_Some_Pinned_Solvable_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Flat_Some_Pinned_Unsolvable_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes, Containers and Lists (flat)", "Flat_Some_Pinned_Unsolvable_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_All_Unpinned() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_All_Unpinned");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_Simple_All_Unpinned() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_Simple_All_Unpinned");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_Simple_All_Pinned() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_Simple_All_Pinned");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertNoBoundChanged(initialBounds, finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_Simple_Some_Pinned_No_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_Simple_Some_Pinned_No_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_Simple_Some_Pinned_Solvable_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_Simple_Some_Pinned_Solvable_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_Simple_Some_Pinned_Unsolvable_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_Simple_Some_Pinned_Unsolvable_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_Some_Pinned_Solvable_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_Some_Pinned_Solvable_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
        assertNoOverlapsOnPinnedElements(finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_All_Pinned() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_All_Pinned");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertNoBoundChanged(initialBounds, finalBounds);
    }

    /**
     * @throws Exception
     *             if an error occurred.
     */
    public void testArrange_Recursive_Some_Pinned_Unsolvable_Overlaps() throws Exception {
        openDiagram(VIEWPOINT_NAME, "Nodes and Containers (recursive)", "Recursive_Some_Pinned_Unsolvable_Overlaps");
        final Map<IGraphicalEditPart, Rectangle> initialBounds = saveBounds();
        arrangeAll();
        final Map<IGraphicalEditPart, Rectangle> finalBounds = saveBounds();
        assertSomeBoundsChanged(initialBounds, finalBounds);
    }
}
