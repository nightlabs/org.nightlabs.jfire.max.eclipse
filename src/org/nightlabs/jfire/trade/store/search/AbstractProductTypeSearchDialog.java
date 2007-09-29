package org.nightlabs.jfire.trade.store.search;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractProductTypeSearchDialog 
extends CenteredDialog 
{
	/**
	 * @param parentShell
	 */
	public AbstractProductTypeSearchDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(Messages.getString("org.nightlabs.jfire.trade.store.search.AbstractProductTypeSearchDialog.title")); //$NON-NLS-1$
		getShell().setSize(800, 600);				
		setToCenteredLocation();
	}

	private boolean earlySearchResult = false;
	private AbstractProductTypeSearchComposite abstractProductTypeSearchComposite;
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		abstractProductTypeSearchComposite = createProductTypeSearchComposite(parent);
		if (earlySearchText != null) {
			abstractProductTypeSearchComposite.setSearchText(earlySearchText);
			if (!"".equals(earlySearchText.trim())) { //$NON-NLS-1$
				abstractProductTypeSearchComposite.searchPressed();				
			}
			if (!abstractProductTypeSearchComposite.getProductTypeTableComposite().getTableViewer().getSelection().isEmpty())
				if (getButton(SEARCH_ID) != null)
					getButton(SEARCH_ID).setEnabled(true);
				else
					earlySearchResult = true;
		}
		return abstractProductTypeSearchComposite;
	}
	
	public static final int SEARCH_ID = IDialogConstants.CLIENT_ID + 1;
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) 
	{
		Button searchButton = createButton(parent, SEARCH_ID, 
				Messages.getString("org.nightlabs.jfire.trade.store.search.AbstractProductTypeSearchDialog.searchButton.text"), //$NON-NLS-1$
				true);
		searchButton.addSelectionListener(searchListener);
		super.createButtonsForButtonBar(parent);
		
		getButton(Dialog.OK).setEnabled(earlySearchResult);	
		if (abstractProductTypeSearchComposite != null) {
			abstractProductTypeSearchComposite.getProductTypeTableComposite().getTableViewer().
			addSelectionChangedListener(okListener);			
		}
	}
		
	private SelectionListener searchListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			abstractProductTypeSearchComposite.searchPressed();
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};
	
	private ISelectionChangedListener okListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			getButton(Dialog.OK).setEnabled(!selection.isEmpty());
			selectedProductType = abstractProductTypeSearchComposite.getSelectedProductType(); 
		}
	};
	
	private ProductType selectedProductType;
	public ProductType getProductType() {
		return selectedProductType; 
	}
	
	private String earlySearchText;
	public void setSearchText(String searchText) {
		if (abstractProductTypeSearchComposite != null && !abstractProductTypeSearchComposite.isDisposed()) {
			abstractProductTypeSearchComposite.setSearchText(searchText);
		} else {
			earlySearchText = searchText;
		}
	}
	
	protected abstract AbstractProductTypeSearchComposite createProductTypeSearchComposite(Composite parent);
}