/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.jfire.scripting.editor2d.ui.model;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.CheckboxPropertyDescriptor;
import org.nightlabs.base.ui.property.DoublePropertyDescriptor;
import org.nightlabs.base.ui.property.GenericComboBoxPropertyDescriptor;
import org.nightlabs.base.ui.property.XTextPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ui.model.DrawComponentPropertySource;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.Orientation;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.WidthScale;
import org.nightlabs.jfire.scripting.editor2d.ui.resource.Messages;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class BarcodePropertySource
extends DrawComponentPropertySource
{
	public static final String CATEGORY_BARCODE = Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.category.barcode"); //$NON-NLS-1$

	public BarcodePropertySource(BarcodeDrawComponent barcode) {
		super(barcode);
	}

	public BarcodeDrawComponent getBarcode() {
		return (BarcodeDrawComponent) drawComponent;
	}

	@Override
	protected List<IPropertyDescriptor> createPropertyDescriptors()
	{
		List<IPropertyDescriptor> descriptors = getDescriptors();

		// Name
		descriptors.add(createNamePD());
		// X
		descriptors.add(createXPD());
		// Y
		descriptors.add(createYPD());
		// Width
		descriptors.add(createWidthPD());
		// Height
		descriptors.add(createHeightPD());
		// Value
		descriptors.add(createValuePD());
		// Human Readable
		descriptors.add(createHumanReadablePD());
		// WidthScale
		descriptors.add(createWidthScalePD());
		// Orientation
		descriptors.add(createOrientationPD());
		// Visible
		descriptors.add(createVisiblePD());
		// Visible Script
		// comes from extension point
//		descriptors.add(createVisibleConditionScriptPD());
//		descriptors.addAll(getExtensionPointProperties());

		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id)
	{
		if (id.equals(DrawComponent.PROP_X)) {
			return new Double(getValue(drawComponent.getX(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			return new Double(getValue(drawComponent.getY(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_WIDTH))
		{
			if (getBarcode().getOrientation() == Orientation.HORIZONTAL)
				return new Double(getValue(drawComponent.getWidth(), getUnit()));
			else if (getBarcode().getOrientation() == Orientation.VERTICAL)
				return new Double(getValue(drawComponent.getHeight(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			if (getBarcode().getOrientation() == Orientation.HORIZONTAL)
				return new Double(getValue(drawComponent.getHeight(), getUnit()));
			else if (getBarcode().getOrientation() == Orientation.VERTICAL)
				return new Double(getValue(drawComponent.getWidth(), getUnit()));
		}
		else if (id.equals(BarcodeDrawComponent.PROP_VALUE))
			return getBarcode().getText();
		else if (id.equals(BarcodeDrawComponent.PROP_HUMAN_READABLE))
			return getBarcode().isHumanReadable();
		else if (id.equals(BarcodeDrawComponent.PROP_WIDTH_SCALE))
			return getBarcode().getWidthScale();
		else if (id.equals(BarcodeDrawComponent.PROP_ORIENTATION))
			return getBarcode().getOrientation();
		else if (id.equals(BarcodeDrawComponent.PROP_BARCODE_HEIGHT))
			return new Double(getValue(getBarcode().getBarcodeHeight(), getUnit()));

		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value)
	{
		if (id.equals(DrawComponent.PROP_X)) {
			double x = ((Double)value).doubleValue();
			drawComponent.setX(getSetValue(x, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			double y = ((Double)value).doubleValue();
			drawComponent.setY(getSetValue(y, getUnit()));
			return;
		}
		else if (id.equals(BarcodeDrawComponent.PROP_HUMAN_READABLE)) {
			getBarcode().setHumanReadable((Boolean)value);
			return;
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			double height = ((Double)value).doubleValue();
			drawComponent.setHeight(getSetValue(height, getUnit()));
			return;
		}
		else if (id.equals(BarcodeDrawComponent.PROP_WIDTH_SCALE)) {
			getBarcode().setWidthScale((WidthScale)value);
		}
		else if (id.equals(BarcodeDrawComponent.PROP_ORIENTATION)) {
			getBarcode().setOrientation((Orientation)value);
		}
		else if (id.equals(BarcodeDrawComponent.PROP_BARCODE_HEIGHT)) {
			double barcodeHeight = ((Double)value).doubleValue();
			getBarcode().setBarcodeHeight(getSetValue(barcodeHeight, getUnit()));
		}
		super.setPropertyValue(id, value);
	}

	// TODO: use WidthScale Combo
	@Override
	protected PropertyDescriptor createWidthPD()
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_WIDTH,
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.width"), true); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}

	@Override
	protected PropertyDescriptor createHeightPD()
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(BarcodeDrawComponent.PROP_BARCODE_HEIGHT,
				org.nightlabs.editor2d.ui.resource.Messages.getString("org.nightlabs.editor2d.ui.model.DrawComponentPropertySource.height"), false); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}

	protected PropertyDescriptor createValuePD()
	{
		PropertyDescriptor desc = new XTextPropertyDescriptor(BarcodeDrawComponent.PROP_VALUE,
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.value"), true); //$NON-NLS-1$
		desc.setCategory(CATEGORY_BARCODE);
		return desc;
	}

	protected PropertyDescriptor createHumanReadablePD()
	{
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(BarcodeDrawComponent.PROP_HUMAN_READABLE,
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.humanReadable"), false); //$NON-NLS-1$
		desc.setCategory(CATEGORY_BARCODE);
		return desc;
	}

	protected PropertyDescriptor createWidthScalePD()
	{
		PropertyDescriptor desc = new GenericComboBoxPropertyDescriptor<WidthScale>(
				BarcodeDrawComponent.PROP_WIDTH_SCALE,
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.widthScale"), //$NON-NLS-1$
				CollectionUtil.enum2List(WidthScale.SCALE_1),
				new WidthScaleLabelProvider());
		desc.setCategory(CATEGORY_BARCODE);
		return desc;
	}

	protected PropertyDescriptor createOrientationPD() {
		PropertyDescriptor desc = new GenericComboBoxPropertyDescriptor<Orientation>(
				BarcodeDrawComponent.PROP_ORIENTATION,
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.orientation"), //$NON-NLS-1$
				CollectionUtil.enum2List(Orientation.HORIZONTAL),
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						if (element == Orientation.HORIZONTAL)
							return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.horizontal"); //$NON-NLS-1$
						if (element == Orientation.VERTICAL)
							return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource.vertical"); //$NON-NLS-1$

						return super.getText(element);
					}
				});
		desc.setCategory(CATEGORY_BARCODE);
		return desc;
	}

}
