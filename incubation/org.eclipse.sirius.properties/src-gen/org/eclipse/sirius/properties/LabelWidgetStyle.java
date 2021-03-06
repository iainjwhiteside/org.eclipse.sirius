/**
 * Copyright (c) 2016 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *
 */
package org.eclipse.sirius.properties;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sirius.viewpoint.FontFormat;
import org.eclipse.sirius.viewpoint.description.ColorDescription;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Label Widget Style</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>
 * {@link org.eclipse.sirius.properties.LabelWidgetStyle#getFontNameExpression
 * <em>Font Name Expression</em>}</li>
 * <li>{@link org.eclipse.sirius.properties.LabelWidgetStyle#getFontSize
 * <em>Font Size</em>}</li>
 * <li>{@link org.eclipse.sirius.properties.LabelWidgetStyle#getBackgroundColor
 * <em>Background Color</em>}</li>
 * <li>{@link org.eclipse.sirius.properties.LabelWidgetStyle#getForegroundColor
 * <em>Foreground Color</em>}</li>
 * <li>{@link org.eclipse.sirius.properties.LabelWidgetStyle#getFontFormat
 * <em>Font Format</em>}</li>
 * </ul>
 *
 * @see org.eclipse.sirius.properties.PropertiesPackage#getLabelWidgetStyle()
 * @model
 * @generated
 */
public interface LabelWidgetStyle extends WidgetStyle {
    /**
     * Returns the value of the '<em><b>Font Name Expression</b></em>'
     * attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Font Name Expression</em>' attribute isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Font Name Expression</em>' attribute.
     * @see #setFontNameExpression(String)
     * @see org.eclipse.sirius.properties.PropertiesPackage#getLabelWidgetStyle_FontNameExpression()
     * @model dataType=
     *        "org.eclipse.sirius.viewpoint.description.InterpretedExpression"
     * @generated
     */
    String getFontNameExpression();

    /**
     * Sets the value of the '
     * {@link org.eclipse.sirius.properties.LabelWidgetStyle#getFontNameExpression
     * <em>Font Name Expression</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @param value
     *            the new value of the '<em>Font Name Expression</em>'
     *            attribute.
     * @see #getFontNameExpression()
     * @generated
     */
    void setFontNameExpression(String value);

    /**
     * Returns the value of the '<em><b>Font Size</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Font Size</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Font Size</em>' attribute.
     * @see #setFontSize(int)
     * @see org.eclipse.sirius.properties.PropertiesPackage#getLabelWidgetStyle_FontSize()
     * @model
     * @generated
     */
    int getFontSize();

    /**
     * Sets the value of the '
     * {@link org.eclipse.sirius.properties.LabelWidgetStyle#getFontSize
     * <em>Font Size</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @param value
     *            the new value of the '<em>Font Size</em>' attribute.
     * @see #getFontSize()
     * @generated
     */
    void setFontSize(int value);

    /**
     * Returns the value of the '<em><b>Background Color</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Background Color</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Background Color</em>' reference.
     * @see #setBackgroundColor(ColorDescription)
     * @see org.eclipse.sirius.properties.PropertiesPackage#getLabelWidgetStyle_BackgroundColor()
     * @model
     * @generated
     */
    ColorDescription getBackgroundColor();

    /**
     * Sets the value of the '
     * {@link org.eclipse.sirius.properties.LabelWidgetStyle#getBackgroundColor
     * <em>Background Color</em>}' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @param value
     *            the new value of the '<em>Background Color</em>' reference.
     * @see #getBackgroundColor()
     * @generated
     */
    void setBackgroundColor(ColorDescription value);

    /**
     * Returns the value of the '<em><b>Foreground Color</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Foreground Color</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Foreground Color</em>' reference.
     * @see #setForegroundColor(ColorDescription)
     * @see org.eclipse.sirius.properties.PropertiesPackage#getLabelWidgetStyle_ForegroundColor()
     * @model
     * @generated
     */
    ColorDescription getForegroundColor();

    /**
     * Sets the value of the '
     * {@link org.eclipse.sirius.properties.LabelWidgetStyle#getForegroundColor
     * <em>Foreground Color</em>}' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @param value
     *            the new value of the '<em>Foreground Color</em>' reference.
     * @see #getForegroundColor()
     * @generated
     */
    void setForegroundColor(ColorDescription value);

    /**
     * Returns the value of the '<em><b>Font Format</b></em>' attribute list.
     * The list contents are of type
     * {@link org.eclipse.sirius.viewpoint.FontFormat}. The literals are from
     * the enumeration {@link org.eclipse.sirius.viewpoint.FontFormat}. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Font Format</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Font Format</em>' attribute list.
     * @see org.eclipse.sirius.viewpoint.FontFormat
     * @see org.eclipse.sirius.properties.PropertiesPackage#getLabelWidgetStyle_FontFormat()
     * @model upper="4"
     * @generated
     */
    EList<FontFormat> getFontFormat();

} // LabelWidgetStyle
