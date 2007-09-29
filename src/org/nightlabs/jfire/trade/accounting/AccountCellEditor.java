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

package org.nightlabs.jfire.trade.accounting;

import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AccountCellEditor 
extends DialogCellEditor 
{
	public static String[] DEFAULT_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Account.FETCH_GROUP_NAME
	};
	
	private Account currValue;
	private String anchorTypeID;
	
	/**
	 * @param anchorTypeID
	 */
	public AccountCellEditor(String anchorTypeID) {
		super();
		this.anchorTypeID = anchorTypeID;
	}

	/**
	 * @param anchorTypeID
	 * @param parent
	 */
	public AccountCellEditor(String anchorTypeID, Composite parent) {		
		super(parent);
		this.anchorTypeID = anchorTypeID;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public AccountCellEditor(String anchorTypeID, Composite parent, int style) {
		super(parent, style);
		this.anchorTypeID = anchorTypeID;
	}
		
	/**
	 * @see org.eclipse.jface.viewers.DialogCellEditor#doSetValue(java.lang.Object)
	 */
	protected void doSetValue(Object value) {
		if (value instanceof Account) {
			currValue = (Account)value;
			Account account = AccountDAO.sharedInstance().getAccount(
					(AnchorID) JDOHelper.getObjectId(currValue),
					DEFAULT_FETCH_GROUPS, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					new NullProgressMonitor()
				);
			getDefaultLabel().setText(account.getName().getText(Locale.getDefault().getLanguage()));
		}
		else {
			getDefaultLabel().setText(""); //$NON-NLS-1$
			currValue = null;
		}
	}

//	private XComposite wrapper;
//	private Label accountLabel;
//	private Button searchButton;
	
	/**
	 * @see org.eclipse.jface.viewers.DialogCellEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createControl(Composite parent) {
		Control superResult = super.createControl(parent);
		getDefaultLabel().setAlignment(SWT.RIGHT);
		return superResult;
//		wrapper = new XComposite(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
//		wrapper.getGridLayout().numColumns = 2;
//		accountLabel = new Label(wrapper, SWT.NONE | labelOrientation);
//		accountLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
//		searchButton = new Button(wrapper, SWT.FLAT);
//		searchButton.setText("");
//		GridData bData = new GridData();
////		bData.heightHint = parent.getBounds().height;
//		searchButton.setLayoutData(bData);
//		searchButton.addSelectionListener(new SelectionListener(){
//			public void widgetSelected(SelectionEvent e) {
//				Account account = AccountSearchDialog.searchAccount(anchorTypeID);
//				if (account != null) {
//					setValue(JDOHelper.getObjectId(account));
//					fireApplyEditorValue();
//				}
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//		});
//		return wrapper;
	}

	protected Object doGetValue() {
		return currValue;
	}

	protected void doSetFocus() {
	}

	protected Object openDialogBox(Control cellEditorWindow) {
		Account account = AccountSearchDialog.searchAccount(anchorTypeID);
		return account;
	}
}
