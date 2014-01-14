/*******************************************************************************
 * Copyright (c) 2011, 2012 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.api.dialect.description;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sirius.business.internal.dialect.description.DescriptionInterpretedExpressionTargetSwitch;
import org.eclipse.sirius.business.internal.dialect.description.DiagramToolInterpretedExpressionTargetSwitch;
import org.eclipse.sirius.business.internal.dialect.description.FilterInterpretedExpressionTargetSwitch;
import org.eclipse.sirius.business.internal.dialect.description.StyleInterpretedExpressionTargetSwitch;
import org.eclipse.sirius.business.internal.dialect.description.ToolInterpretedExpressionTargetSwitch;
import org.eclipse.sirius.business.internal.dialect.description.ValidationInterpretedExpressionTargetSwitch;
import org.eclipse.sirius.business.internal.dialect.description.ViewpointInterpretedExpressionTargetSwitch;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.ext.base.Options;

import com.google.common.collect.Sets;

/**
 * A switch that will return the Target Types associated to a given element and
 * feature corresponding to an Interpreted Expression. This default switch will
 * treat all description elements expected representation-specific elements.
 * 
 * @since 0.9.0
 * @author <a href="mailto:alex.lagarde@obeo.fr">Alex Lagarde</a>
 */
public class DefaultInterpretedExpressionTargetSwitch implements IInterpretedExpressionTargetSwitch {

    private DescriptionInterpretedExpressionTargetSwitch descriptionSwitch;

    private StyleInterpretedExpressionTargetSwitch styleSwitch;

    private ToolInterpretedExpressionTargetSwitch toolSwitch;

    private DiagramToolInterpretedExpressionTargetSwitch diagramToolSwitch;

    private FilterInterpretedExpressionTargetSwitch filterSwitch;

    private ValidationInterpretedExpressionTargetSwitch validationSwitch;

    private ViewpointInterpretedExpressionTargetSwitch viewpointSwitch;

    /**
     * Default constructor.
     * 
     * @param feature
     *            the feature containing the Interpreted expression to determine
     *            the target from
     * @param globalSwitch
     *            the global switch to use
     */
    public DefaultInterpretedExpressionTargetSwitch(EStructuralFeature feature, IInterpretedExpressionTargetSwitch globalSwitch) {
        IInterpretedExpressionTargetSwitch theGlobalSwitch = globalSwitch;
        if (theGlobalSwitch == null) {
            theGlobalSwitch = this;
        }
        this.descriptionSwitch = new DescriptionInterpretedExpressionTargetSwitch(feature, theGlobalSwitch);
        this.styleSwitch = new StyleInterpretedExpressionTargetSwitch(feature, theGlobalSwitch);
        this.toolSwitch = new ToolInterpretedExpressionTargetSwitch(feature, theGlobalSwitch);
        this.diagramToolSwitch = new DiagramToolInterpretedExpressionTargetSwitch(feature, theGlobalSwitch);
        this.filterSwitch = new FilterInterpretedExpressionTargetSwitch(feature, theGlobalSwitch);
        this.validationSwitch = new ValidationInterpretedExpressionTargetSwitch(feature, theGlobalSwitch);
        this.viewpointSwitch = new ViewpointInterpretedExpressionTargetSwitch(feature, theGlobalSwitch);
    }

    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.dialect.description.IInterpretedExpressionTargetSwitch#doSwitch(org.eclipse.emf.ecore.EObject,
     *      boolean)
     */
    public Option<Collection<String>> doSwitch(EObject theEObject, boolean considerFeature) {
        Collection<String> targetTypes = Sets.newLinkedHashSet();
        Option<Collection<String>> expressionTarget = Options.newSome(targetTypes);
        if (theEObject != null) {
            descriptionSwitch.setConsiderFeature(considerFeature);
            expressionTarget = descriptionSwitch.doSwitch(theEObject);

            if (expressionTarget.some() && expressionTarget.get().isEmpty()) {
                styleSwitch.setConsiderFeature(considerFeature);
                expressionTarget = styleSwitch.doSwitch(theEObject);
            }

            if (expressionTarget.some() && expressionTarget.get().isEmpty()) {
                filterSwitch.setConsiderFeature(considerFeature);
                expressionTarget = filterSwitch.doSwitch(theEObject);
            }

            if (expressionTarget.some() && expressionTarget.get().isEmpty()) {
                validationSwitch.setConsiderFeature(considerFeature);
                expressionTarget = validationSwitch.doSwitch(theEObject);
            }

            if (expressionTarget.some() && expressionTarget.get().isEmpty()) {
                viewpointSwitch.setConsiderFeature(considerFeature);
                expressionTarget = viewpointSwitch.doSwitch(theEObject);
            }

            if (expressionTarget.some() && expressionTarget.get().isEmpty()) {
                diagramToolSwitch.setConsiderFeature(considerFeature);
                expressionTarget = diagramToolSwitch.doSwitch(theEObject);
            }

            // Tool in last position -> tool will return a default EObject value
            // as domain class.
            if (expressionTarget.some() && expressionTarget.get().isEmpty()) {
                toolSwitch.setConsiderFeature(considerFeature);
                expressionTarget = toolSwitch.doSwitch(theEObject);
            }
        }
        return expressionTarget;
    }
}
