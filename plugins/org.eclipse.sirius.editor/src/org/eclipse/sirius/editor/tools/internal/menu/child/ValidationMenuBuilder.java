/*******************************************************************************
 * Copyright (c) 2009 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.editor.tools.internal.menu.child;

import org.eclipse.sirius.diagram.description.validation.ValidationPackage;
import org.eclipse.sirius.editor.tools.api.menu.AbstractTypeRestrictingMenuBuilder;

/**
 * The validation menu.
 * 
 * @author cbrun
 * 
 */
public class ValidationMenuBuilder extends AbstractTypeRestrictingMenuBuilder {
    /**
     * build the menu.
     */
    public ValidationMenuBuilder() {
        super();
        addValidType(ValidationPackage.eINSTANCE.getValidationSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLabel() {
        return "New Validation...";
    }

}
