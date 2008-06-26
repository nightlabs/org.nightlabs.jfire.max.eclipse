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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.admin.ui.tariff;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.language.LanguageChangeEvent;
import org.nightlabs.base.ui.language.LanguageChangeListener;
import org.nightlabs.base.ui.language.LanguageChooser;
import org.nightlabs.base.ui.language.LanguageChooserList;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TariffEditView
extends LSDViewPart
{
	public static final String ID_VIEW = TariffEditView.class.getName();

	protected LanguageChooser languageChooser;
	protected TariffListComposite tariffListComposite;

	public void createPartContents(Composite parent)
	{
		Composite composite = parent;
		composite.setLayout(new GridLayout(1, false));
		
		SashForm sf = new SashForm(composite, SWT.BORDER);
		sf.setLayoutData(new GridData(GridData.FILL_BOTH));
		languageChooser = new LanguageChooserList(sf, false, true);
		tariffListComposite = new TariffListComposite(sf, SWT.NONE);

		sf.setWeights(new int[] {1, 2});

		languageChooser.addLanguageChangeListener(new LanguageChangeListener() {
			public void languageChanged(LanguageChangeEvent event)
			{
				String languageID = event.getNewLanguage().getLanguageID();
				tariffListComposite.setLanguageID(languageID);
			}
		});
	}
	
	public TariffListComposite getTariffListComposite() {
		return tariffListComposite;
	}
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	public void submit()
		throws ModuleException
	{
		tariffListComposite.submit();
	}
}
