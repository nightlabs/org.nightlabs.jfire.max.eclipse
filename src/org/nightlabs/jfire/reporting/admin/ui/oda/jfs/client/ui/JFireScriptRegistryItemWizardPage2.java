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

package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

//import org.eclipse.birt.report.designer.data.ui.property.AbstractPropertyPage;
import org.eclipse.birt.report.designer.ui.dialogs.properties.AbstractPropertyPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class JFireScriptRegistryItemWizardPage2 extends AbstractPropertyPage {

	private XComposite wrapper; 
	
	private ScriptRegistryItemTree itemTree;
	
	/**
	 * @param arg0
	 */
	public JFireScriptRegistryItemWizardPage2() {
	}

	public Control createPageControl(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		itemTree = new ScriptRegistryItemTree(wrapper, null, false);
		itemTree.setInput(
				ScriptRegistryItemProvider.sharedInstance().getTopLevelNodes()
			);
		return wrapper;
	}

	public void pageActivated() {
	}

}
