package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;

/**
 * @author Fitas [at] NightLabs [dot] de
 * @author Daniel [at] NightLabs [dot] de
 */
public class LegalEntityEditComposite 
extends XComposite
{	
	private Text legalText;
	private LegalEntity legalEntity;
	private ListenerList legalEntityValueChangedListeners = new ListenerList();
	
	public LegalEntityEditComposite(Composite parent, int style) 
	{
		super(parent, style);
		this.getGridLayout().numColumns = 2;
		this.getGridData().grabExcessVerticalSpace = false;
		legalText = new Text(this, XComposite.getBorderStyle(this) | SWT.SINGLE | SWT.READ_ONLY);
		legalText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button legalButton = new Button(this, SWT.NONE);
		legalButton.setText("...");
		legalButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				legalPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
	}

	protected void legalPressed() 
	{
		LegalEntitySearchCreateWizard wiz = new LegalEntitySearchCreateWizard("",true);
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		if (dlg.open() == Window.OK) {
			legalEntity = wiz.getLegalEntity();
			fireLegalEntityValueChangedEvent();
			updateUI();
		}
	}

	protected void updateUI() {
		legalText.setText(legalEntity == null ? "" : legalEntity.getPerson().getDisplayName());
	}

	public LegalEntity getLegalEntity() {
		return legalEntity;
	}

	/**
	 * sets the {@link ProductType} to display the MoneyFlowConfiguration for
	 * @param productType the proudctType to set
	 */
	public void setLegalEntity(final LegalEntity legalEntity) 
	{
		this.legalEntity = legalEntity;
		updateUI();
	}	

	protected void updateDelegate(final LocalAccountantDelegate delegate) {

	}

	protected void fireLegalEntityValueChangedEvent()
	{
		Object[] listeners = legalEntityValueChangedListeners.getListeners();
		if (listeners.length < 1)
			return;

		for (Object l : listeners) {
			ILegalEntityValueChangedListener listener = (ILegalEntityValueChangedListener) l;
			listener.legalEntityValueChanged();
		}
	}

	public void addLegalEntityValueChangedListener(ILegalEntityValueChangedListener listener)
	{
		legalEntityValueChangedListeners.add(listener);
	}

	public void removeLegalEntityValueChangedListener(ILegalEntityValueChangedListener listener)
	{
		legalEntityValueChangedListeners.remove(listener);
	}

}
