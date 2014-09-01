/*******************************************************************************
 * Copyright (c) 2014 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.ui.internal.edit.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IBorderedShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.sirius.diagram.ui.business.internal.operation.MoveViewOperation;
import org.eclipse.sirius.diagram.ui.business.internal.operation.ShiftDirectBorderedNodesOperation;
import org.eclipse.sirius.diagram.ui.business.internal.query.RequestQuery;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.SiriusResizeTracker;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DNodeContainerViewNodeContainerCompartmentEditPart;
import org.eclipse.sirius.diagram.ui.tools.internal.edit.command.CommandFactory;
import org.eclipse.sirius.diagram.ui.tools.internal.util.EditPartQuery;
import org.eclipse.sirius.ext.gmf.runtime.editparts.GraphicalHelper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This command avoids time consumption as long as it does not executed. The
 * "real" command is created during the execution.
 * 
 * @author <a href="mailto:laurent.redor@obeo.fr">Laurent Redor</a>
 */
public class ChildrenAdjustmentCommand extends AbstractTransactionalCommand {
    CompositeTransactionalCommand wrappedCommand;

    GraphicalEditPart host;

    ChangeBoundsRequest request;

    /**
     * Default constructor.
     * 
     * @param host
     *            the <i>host</i> EditPart on which this policy is installed.
     * @param moveDelta
     *            The move delta
     * @param movedEditParts
     *            Selected edit parts that will be moved
     */
    public ChildrenAdjustmentCommand(GraphicalEditPart host, ChangeBoundsRequest request) {
        super(host.getEditingDomain(), "Adapt children location", null);
        this.host = host;
        this.request = request;
    }

    @Override
    protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) {
        CommandResult result = CommandResult.newOKCommandResult();
        wrappedCommand = new CompositeTransactionalCommand(host.getEditingDomain(), this.getLabel());

        RequestQuery rq = new RequestQuery(request);
        boolean keepSameAbsoluteLocation = false;
        if (rq.isResizeFromTop() || rq.isResizeFromLeft() || request.isCenteredResize()) {
            Object childrenMoveModeExtendedData = request.getExtendedData().get(SiriusResizeTracker.CHILDREN_MOVE_MODE_KEY);
            keepSameAbsoluteLocation = (childrenMoveModeExtendedData == null && SiriusResizeTracker.DEFAULT_CHILDREN_MOVE_MODE)
                    || (childrenMoveModeExtendedData != null && ((Boolean) childrenMoveModeExtendedData).booleanValue());
            if (keepSameAbsoluteLocation) {
                addChildrenAdjustmentCommands(host, wrappedCommand, request);
            }
        }

        if (host instanceof IBorderedShapeEditPart) {
            addBorderChildrenAdjustmentCommands(host, wrappedCommand, request, keepSameAbsoluteLocation);
        }

        if (wrappedCommand.size() > 0) {
            if (wrappedCommand.canExecute()) {
                try {
                    wrappedCommand.execute(new NullProgressMonitor(), null);
                } catch (ExecutionException e) {
                    result = CommandResult.newErrorCommandResult(e);
                }
            } else {
                // Not expected to be there
                result = CommandResult.newWarningCommandResult("The adaptation of children location to shape move can not be done.", null);
            }
        }
        return result;
    }

    @Override
    public boolean canUndo() {
        if (wrappedCommand.size() > 0 && wrappedCommand != null) {
            return wrappedCommand.canUndo();
        }
        return true;
    }

    @Override
    public boolean canRedo() {
        if (wrappedCommand.size() > 0 && wrappedCommand != null) {
            return wrappedCommand.canRedo();
        }
        return true;
    }

    @Override
    public void dispose() {
        host = null;
        request = null;
        wrappedCommand = null;
    }

    /**
     * Add the needed commands, to move the children nodes, to the original
     * command.
     * 
     * @param resizedPart
     *            The part that will be resized
     * @param cc
     *            The current command that resizes the parent part, command to
     *            complete with the moves of children
     * @param cbr
     *            The original request
     */
    private void addChildrenAdjustmentCommands(GraphicalEditPart resizedPart, CompositeTransactionalCommand cc, ChangeBoundsRequest cbr) {
        PrecisionPoint delta = new PrecisionPoint(cbr.getMoveDelta().getNegated());
        GraphicalHelper.applyInverseZoomOnPoint(resizedPart, delta);
        DNodeContainerViewNodeContainerCompartmentEditPart compartment = Iterables
                .getFirst(Iterables.filter(resizedPart.getChildren(), DNodeContainerViewNodeContainerCompartmentEditPart.class), null);
        if (compartment != null) {
            Iterable<EditPart> childrenExceptBorderItemPart = Iterables.filter(compartment.getChildren(), EditPart.class);
            for (EditPart editPart : childrenExceptBorderItemPart) {
                IAdaptable adapter = new EObjectAdapter((Node) editPart.getModel());
                // Shift this view by the delta
                cc.compose(CommandFactory.createICommand(cc.getEditingDomain(), new MoveViewOperation(DiagramUIMessages.SetLocationCommand_Label_Resize, adapter, delta)));
            }
        }
    }

    /**
     * Add the needed commands, to move the border nodes, to the original
     * command.
     * 
     * @param resizedPart
     *            The part that will be resized (parent of the border nodes to
     *            move)
     * @param cc
     *            The current command that resizes the parent part, command to
     *            complete with the moves of border nodes
     * @param cbr
     *            The resize request
     * @param keepSameAbsoluteLocation
     *            true if the children must stay at the same absolute location,
     *            false otherwise. The location can change in one axis of there
     *            border node is on the moved side.
     */
    private void addBorderChildrenAdjustmentCommands(GraphicalEditPart resizedPart, CompositeTransactionalCommand cc, final ChangeBoundsRequest cbr, boolean keepSameAbsoluteLocation) {
        RequestQuery rq = new RequestQuery(cbr);
        Rectangle logicalDelta = rq.getLogicalDelta();
        EditPartQuery resizedPartQuery = new EditPartQuery(resizedPart);
        if (rq.isResizeFromTop() || rq.isResizeFromBottom()) {
            int verticalSizeDelta = logicalDelta.height;

            // The border nodes of the bottom side must be shift to stay on
            // the bottom side.
            List<Node> childrenToMove = resizedPartQuery.getBorderedNodes(PositionConstants.SOUTH);
            if (!childrenToMove.isEmpty()) {
                cc.compose(CommandFactory.createICommand(cc.getEditingDomain(), new ShiftDirectBorderedNodesOperation(childrenToMove, verticalSizeDelta, PositionConstants.VERTICAL)));
            }
            // The border nodes of the east and west side must eventually be
            // shift to stay in the parent bounds.
            Map<Node, Integer> childrenToMoveWithDelta = resizedPartQuery.getBorderedNodesToMoveWithDelta(PositionConstants.EAST, verticalSizeDelta);
            childrenToMoveWithDelta.putAll(resizedPartQuery.getBorderedNodesToMoveWithDelta(PositionConstants.WEST, verticalSizeDelta));
            for (Entry<Node, Integer> entry : childrenToMoveWithDelta.entrySet()) {
                cc.compose(CommandFactory.createICommand(cc.getEditingDomain(), new ShiftDirectBorderedNodesOperation(Lists.newArrayList(entry.getKey()), entry.getValue().intValue(),
                        PositionConstants.VERTICAL)));
            }

            if (keepSameAbsoluteLocation && (rq.isResizeFromTop() || cbr.isCenteredResize())) {
                if (cbr.isCenteredResize()) {
                    verticalSizeDelta = verticalSizeDelta + logicalDelta.y;
                }
                // The border nodes of the west and east sides must be shift to
                // stay at the same absolute location (except if they have
                // already moved to stay in the parent bounds).
                List<Node> borderNodes = resizedPartQuery.getBorderedNodes(PositionConstants.WEST);
                borderNodes.addAll(resizedPartQuery.getBorderedNodes(PositionConstants.EAST));
                borderNodes.removeAll(childrenToMoveWithDelta.keySet());
                cc.compose(CommandFactory.createICommand(cc.getEditingDomain(), new ShiftDirectBorderedNodesOperation(borderNodes, verticalSizeDelta, PositionConstants.VERTICAL)));
            }

        }
        if (rq.isResizeFromRight() || rq.isResizeFromLeft()) {
            int horizontalSizeDelta = logicalDelta.width;
            // The border node of the east size must be shift to stay on the
            // east side.
            List<Node> childrenToMove = resizedPartQuery.getBorderedNodes(PositionConstants.EAST);
            if (!childrenToMove.isEmpty()) {
                cc.compose(CommandFactory.createICommand(cc.getEditingDomain(), new ShiftDirectBorderedNodesOperation(childrenToMove, horizontalSizeDelta, PositionConstants.HORIZONTAL)));
            }
            // The border nodes of the north or south size must eventually be
            // shift to stay in the parent bounds.
            Map<Node, Integer> childrenToMoveWithDelta = resizedPartQuery.getBorderedNodesToMoveWithDelta(PositionConstants.NORTH, horizontalSizeDelta);
            childrenToMoveWithDelta.putAll(resizedPartQuery.getBorderedNodesToMoveWithDelta(PositionConstants.SOUTH, horizontalSizeDelta));
            for (Entry<Node, Integer> entry : childrenToMoveWithDelta.entrySet()) {
                cc.compose(CommandFactory.createICommand(cc.getEditingDomain(), new ShiftDirectBorderedNodesOperation(Lists.newArrayList(entry.getKey()), entry.getValue().intValue(),
                        PositionConstants.HORIZONTAL)));
            }

            if (keepSameAbsoluteLocation && (rq.isResizeFromLeft() || cbr.isCenteredResize())) {
                if (cbr.isCenteredResize()) {
                    horizontalSizeDelta = horizontalSizeDelta + logicalDelta.x;
                }
                // The border nodes of the north and south sides must be shift
                // to stay at the same absolute location (except if they have
                // already moved to stay in the parent bounds).
                List<Node> borderNodes = resizedPartQuery.getBorderedNodes(PositionConstants.NORTH);
                borderNodes.addAll(resizedPartQuery.getBorderedNodes(PositionConstants.SOUTH));
                borderNodes.removeAll(childrenToMoveWithDelta.keySet());
                cc.compose(CommandFactory.createICommand(cc.getEditingDomain(), new ShiftDirectBorderedNodesOperation(borderNodes, horizontalSizeDelta, PositionConstants.HORIZONTAL)));
            }
        }
    }
}
