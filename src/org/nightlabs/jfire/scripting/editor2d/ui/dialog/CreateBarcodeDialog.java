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
package org.nightlabs.jfire.scripting.editor2d.ui.dialog;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.editor2d.unit.DotUnit;
import org.nightlabs.editor2d.util.UnitUtil;
import org.nightlabs.i18n.unit.MMUnit;
import org.nightlabs.i18n.unit.resolution.ResolutionImpl;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.Orientation;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.Type;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.WidthScale;
import org.nightlabs.jfire.scripting.editor2d.ui.request.BarcodeCreateRequest;
import org.nightlabs.jfire.scripting.editor2d.ui.resource.Messages;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class CreateBarcodeDialog
extends ResizableTitleAreaDialog
{
	public static final Logger LOGGER = Logger.getLogger(CreateBarcodeDialog.class);
	public static final double DEFAULT_BARCODE_HEIGHT = 15; // mm

	public CreateBarcodeDialog(Shell parentShell, BarcodeCreateRequest request)
	{
		super(parentShell, Messages.RESOURCE_BUNDLE);
		this.request = request;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private BarcodeCreateRequest request = null;

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.title")); //$NON-NLS-1$
		newShell.setMinimumSize(400, 200);
	}

	protected XComboComposite<ScriptRegistryItemID> scriptCombo = null;
	private XComboComposite<Type> barcodeTypeCombo = null;
	private XComboComposite<WidthScale> widthScaleCombo = null;
	private XComboComposite<Orientation> orientationCombo = null;
	private Spinner xSpinner = null;
	private Spinner ySpinner = null;
	private Spinner heightSpinner = null;
	private Button humanReadableButton = null;

	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitle(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.title")); //$NON-NLS-1$
		setMessage(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.message")); //$NON-NLS-1$

		XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = new GridLayout(4, true);
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Script
		Label scriptLabel = new Label(comp, SWT.NONE);
		scriptLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.script")); //$NON-NLS-1$
		createScriptRegistryItemIDCombo(comp);
//		scriptCombo.selectElement(ScriptingConstants.OID.SCRIPT_REGISTRY_ITEM_ID_SCRIPT_TICKET_KEY);

		// Barcode Type
		Label barcodeTypeLabel = new Label(comp, SWT.NONE);
		barcodeTypeLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.barcodeType")); //$NON-NLS-1$
		createBarcodeTypeCombo(comp);
		barcodeTypeCombo.selectElement(Type.TYPE_128);

		// Width scale
		Label widthScaleLabel = new Label(comp, SWT.NONE);
		widthScaleLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.widthScale")); //$NON-NLS-1$
		createWidthScaleCombo(comp);
		widthScaleCombo.selectElement(WidthScale.SCALE_3);

		// X
		Label xLabel = new Label(comp, SWT.NONE);
		xLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.x")); //$NON-NLS-1$
		xSpinner = new Spinner(comp, SWT.BORDER);
		xSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		xSpinner.setMinimum(Integer.MIN_VALUE);
		xSpinner.setMaximum(Integer.MAX_VALUE);
		xSpinner.setSelection(request.getLocation().x);

		// Y
		Label yLabel = new Label(comp, SWT.NONE);
		yLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.y")); //$NON-NLS-1$
		ySpinner = new Spinner(comp, SWT.BORDER);
		ySpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ySpinner.setMinimum(Integer.MIN_VALUE);
		ySpinner.setMaximum(Integer.MAX_VALUE);
		ySpinner.setSelection(request.getLocation().y);

		// Height
		Label heightLabel = new Label(comp, SWT.NONE);
		heightLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.height")); //$NON-NLS-1$
		heightSpinner = new Spinner(comp, SWT.BORDER);
		heightSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		heightSpinner.setMinimum(1);
		heightSpinner.setMaximum(Integer.MAX_VALUE);
		// TODO: get dotunit from AbstractEditor.getUnitManager()
		int heightInDots = UnitUtil.getModelValue(DEFAULT_BARCODE_HEIGHT, new DotUnit(new ResolutionImpl()), new MMUnit());
		heightSpinner.setSelection(heightInDots);

		// Orientation
		Label orientationLabel = new Label(comp, SWT.NONE);
		orientationLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.orientation")); //$NON-NLS-1$
		createOrientationCombo(comp);
		orientationCombo.selectElement(Orientation.VERTICAL);

		// Human Readable
		Label humanReadableLabel = new Label(comp, SWT.NONE);
		humanReadableLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.label.humanReadable")); //$NON-NLS-1$
		humanReadableButton = new Button(comp, SWT.CHECK);
		humanReadableButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		humanReadableButton.setSelection(true);

		humanReadableLabel.setFocus();
		return comp;
	}

	private void createBarcodeTypeCombo(Composite parent)
	{
//		List<Type> types = CollectionUtil.enum2List(Type.valueOf(Type.class, "TYPE_128"));
		List<Type> types = CollectionUtil.enum2List(Type.TYPE_128);
		barcodeTypeCombo = new XComboComposite<Type>(parent, AbstractListComposite.getDefaultWidgetStyle(parent),
				(String) null, barcodeTypeLabelProvider);
		barcodeTypeCombo.setInput(types);
	}

	public static final ILabelProvider barcodeTypeLabelProvider = new LabelProvider()
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof Type)
			{
				Type type = (Type) element;
				switch (type)
				{
					case TYPE_128:
						return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.type128");							 //$NON-NLS-1$
				}
				return type.name();
			}
			return null;
		}
		@Override
		public Image getImage(Object element) {
			return null;
		}
	};

	private void createWidthScaleCombo(Composite parent)
	{
//		List<WidthScale> types = CollectionUtil.enum2List(Enum.valueOf(WidthScale.class, "SCALE_1"));
		List<WidthScale> types = CollectionUtil.enum2List(WidthScale.SCALE_1);
		widthScaleCombo = new XComboComposite<WidthScale>(parent,
				AbstractListComposite.getDefaultWidgetStyle(parent), (String) null, widthScaleLabelProvider);
		widthScaleCombo.setInput(types);
	}

	public static final ILabelProvider widthScaleLabelProvider = new LabelProvider()
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof WidthScale) {
				WidthScale widthScale = (WidthScale) element;
				switch (widthScale)
				{
					case SCALE_1:
						return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.widthScale1");								 //$NON-NLS-1$
					case SCALE_2:
						return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.widthScale2"); //$NON-NLS-1$
					case SCALE_3:
						return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.widthScale3"); //$NON-NLS-1$
					case SCALE_4:
						return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.widthScale4"); //$NON-NLS-1$
				}
			}
			return null;
		}
		@Override
		public Image getImage(Object element) {
			return null;
		}
	};

	private void createOrientationCombo(Composite parent)
	{
//		List<Orientation> types = CollectionUtil.enum2List(Enum.valueOf(Orientation.class, "HORIZONTAL"));
		List<Orientation> types = CollectionUtil.enum2List(Orientation.HORIZONTAL);
		orientationCombo = new XComboComposite<Orientation>(parent,
				AbstractListComposite.getDefaultWidgetStyle(parent), (String) null, orientationLabelProvider);
		orientationCombo.setInput(types);
	}

	public static final ILabelProvider orientationLabelProvider = new LabelProvider()
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof Orientation) {
				Orientation orientation = (Orientation) element;
				if (orientation == Orientation.HORIZONTAL)
					return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.horizontal"); //$NON-NLS-1$
				if (orientation == Orientation.VERTICAL)
					return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog.vertical");					 //$NON-NLS-1$
			}
			return null;
		}
		@Override
		public Image getImage(Object element) {
			return null;
		}
	};

	public static final ILabelProvider scriptRegistryLabelProvider = new LabelProvider()
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof ScriptRegistryItemID) {
				ScriptRegistryItemID scriptRegistryItemID = (ScriptRegistryItemID) element;
				return scriptRegistryItemID.scriptRegistryItemID;
			}
			return null;
		}
		@Override
		public Image getImage(Object element) {
			return null;
		}
	};

	@Override
	protected void okPressed()
	{
		request.setLocation(new Point(xSpinner.getSelection(), ySpinner.getSelection()));
		request.setHumanReadable(humanReadableButton.getSelection());
		request.setOrientation(orientationCombo.getSelectedElement());
		request.setWidthScale(widthScaleCombo.getSelectedElement());
		request.setHeight(heightSpinner.getSelection());
		request.setScriptRegistryItemID(scriptCombo.getSelectedElement());
		request.setBarcodeType(barcodeTypeCombo.getSelectedElement());
		request.setValue(getValue(getSelectedScriptRegistryItemID()));
		request.setLocation(new Point(xSpinner.getSelection(), ySpinner.getSelection()));
		super.okPressed();
	}

	protected abstract ScriptRegistryItemID getSelectedScriptRegistryItemID();

	protected abstract String getValue(ScriptRegistryItemID scriptRegistryItemID);

	protected abstract void createScriptRegistryItemIDCombo(Composite parent);
}
