package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractProductTypeSearchDialog
extends ResizableTitleAreaDialog
{
	public static final int SEARCH_ID = IDialogConstants.CLIENT_ID + 1;
	private boolean earlySearchResult = false;
	private AbstractProductTypeSearchComposite abstractProductTypeSearchComposite;
	private String earlySearchText;
	private ProductType selectedProductType;
	
	/**
	 * @param parentShell
	 */
	public AbstractProductTypeSearchDialog(Shell parentShell) {
		super(parentShell, null);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog.text.search"), getProductTypeName())); //$NON-NLS-1$
		setTitle(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog.title.search"), getProductTypeName())); //$NON-NLS-1$
		setMessage(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog.message.search"), getProductTypeName())); //$NON-NLS-1$
	}

	protected String getProductTypeName() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog.productType"); //$NON-NLS-1$
	}
	
	@Override
	protected Point getPreferredSize() {
		return new Point(800, 600);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		abstractProductTypeSearchComposite = createProductTypeSearchComposite(parent);
		abstractProductTypeSearchComposite.getProductTypeTableComposite().addDoubleClickListener(new IDoubleClickListener(){
			@Override
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
			}
		});
		abstractProductTypeSearchComposite.getProductTypeTableComposite().addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getButton(Window.OK).setEnabled(!event.getSelection().isEmpty());
			}
		});
		if (earlySearchText != null) {
			abstractProductTypeSearchComposite.setSearchText(earlySearchText);
			if (!"".equals(earlySearchText.trim())) { //$NON-NLS-1$
				abstractProductTypeSearchComposite.searchPressed();
			}
			if (!abstractProductTypeSearchComposite.getProductTypeTableComposite().getSelection().isEmpty())
				if (getButton(SEARCH_ID) != null)
					getButton(SEARCH_ID).setEnabled(true);
				else
					earlySearchResult = true;
		}
		return abstractProductTypeSearchComposite;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		Button searchButton = createButton(parent, SEARCH_ID,
				Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog.searchButton.text"), //$NON-NLS-1$
				true);
		searchButton.addSelectionListener(searchListener);
		super.createButtonsForButtonBar(parent);
		
		getButton(Window.OK).setEnabled(earlySearchResult);
		if (abstractProductTypeSearchComposite != null) {
			abstractProductTypeSearchComposite.getProductTypeTableComposite().
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
			getButton(Window.OK).setEnabled(!selection.isEmpty());
			selectedProductType = abstractProductTypeSearchComposite.getSelectedProductType();
		}
	};
	
	public ProductType getProductType() {
		return selectedProductType;
	}
	
	public void setSearchText(String searchText) {
		if (abstractProductTypeSearchComposite != null && !abstractProductTypeSearchComposite.isDisposed()) {
			abstractProductTypeSearchComposite.setSearchText(searchText);
		} else {
			earlySearchText = searchText;
		}
	}
	
	/**
	 * Returns the Implementation of {@link AbstractProductTypeSearchComposite} which performs the search.
	 * @param parent the parent Composite
	 * @return the Implementation of {@link AbstractProductTypeSearchComposite} which performs the search 
	 */
	protected abstract AbstractProductTypeSearchComposite createProductTypeSearchComposite(Composite parent);
}