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

package org.nightlabs.jfire.reporting.admin.ui.oda.jdojs.ui;

//import org.eclipse.birt.report.designer.data.ui.property.AbstractPropertyPage;
import org.eclipse.birt.report.designer.ui.dialogs.properties.AbstractPropertyPage;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class JavaScriptEditorPage extends AbstractPropertyPage {

	private XComposite wrapper;
	private Text jsText;

	public abstract String getJSPropertyName();
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.dialogs.properties.IPropertyPage#createPageControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageControl(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		jsText = new Text(wrapper, SWT.MULTI);
		jsText.setLayoutData(new GridData(GridData.FILL_BOTH));
		return wrapper;
	}

	private boolean propLoaded = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.dialogs.properties.IPropertyPage#pageActivated()
	 */
	public void pageActivated() {
		if (propLoaded)
			return;
		String script = (String)((OdaDataSetHandle) getContainer( ).getModel( ) ).getProperty( getJSPropertyName());
		if (script != null)
			jsText.setText(script);
		propLoaded = true;
	}

	@Override
	public boolean performOk() {
		try {
			if (propLoaded)
				( (OdaDataSetHandle) getContainer( ).getModel( ) ).setProperty( getJSPropertyName(), jsText.getText());
		} catch (SemanticException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	

}
