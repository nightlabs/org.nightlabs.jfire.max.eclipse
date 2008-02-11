package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

import java.util.Iterator;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.book.id.LocalAccountantDelegateID;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMappingAccountantDelegate;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.AddMoneyFlowMappingWizard;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectCreateAccountantDelegateWizard;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Fitas [at] NightLabs [dot] de
 *
 */



public class OwnerVendorConfigComposite 
extends XComposite
{	
	private boolean showButtons = true;
	
	private Text ownerText;
    private Text vendorText;

	private Label ownerLabel;
	private Label vendorLabel;
	
	LegalEntity vendorLegalEntity;
	LegalEntity ownerLegalEntity;
			
	
	
	
	public OwnerVendorConfigComposite(Composite parent, int style, 
			IDirtyStateManager dirtyStateManager, boolean showButtons) 
	{
		super(parent, style);
		this.dirtyStateManager = dirtyStateManager;
		this.showButtons = showButtons;
		
		
		this.getGridLayout().numColumns = 6;
		this.getGridData().grabExcessVerticalSpace = false;
		
		
		ownerLabel = new Label(this, XComposite.getBorderStyle(this) | SWT.SINGLE | SWT.READ_ONLY);
		ownerLabel.setText("Owner");
		
		ownerText = new Text(this, XComposite.getBorderStyle(this) | SWT.SINGLE | SWT.READ_ONLY);

		
		Button ownerButton = new Button(this, SWT.NONE);
		ownerButton.setText("...");
		
		ownerButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				ownerPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
		
	
		
		vendorLabel = new Label(this, XComposite.getBorderStyle(this) | SWT.SINGLE | SWT.READ_ONLY);
		vendorLabel.setText("Vendor");
		
		
		vendorText = new Text(this, XComposite.getBorderStyle(this) | SWT.SINGLE | SWT.READ_ONLY);
		
		Button vendorButton = new Button(this, SWT.NONE);
		vendorButton.setText("...");
		
		vendorButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				vendorPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
		

		

	}


	private IDirtyStateManager dirtyStateManager;

	
	private ProductTypeID currProductTypeID;
	
	private FadeableComposite fadeableComposite;
	
	private Composite stackWrapper;
	private StackLayout stackLayout;
	private XComposite treeWrapper;
	
	private XComposite noDelegateComp;
	
	
	
	
	protected void ownerPressed() 
	{
		LegalEntitySearchCreateWizard wiz = new LegalEntitySearchCreateWizard("",true);
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		if (dlg.open() == Window.OK) {
		
			 ownerLegalEntity = wiz.getLegalEntity();
			 fireLegalEntityValueChangedEvent();
			 updateUI();
			 
		}
		
		
	}
	
	
	protected void vendorPressed() 
	{
		LegalEntitySearchCreateWizard wiz = new LegalEntitySearchCreateWizard("",true);
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		if (dlg.open() == Window.OK) {
			//productType.setVendor(wiz.getLegalEntity());
		
			 vendorLegalEntity = wiz.getLegalEntity(); 
			 
			 fireLegalEntityValueChangedEvent();
			 updateUI();
			 
		}
		

	}
	
	
	protected void updateUI() {
		
		vendorText.setText(getVendorLegalEntity().getPerson().getDisplayName());
		
		ownerText.setText(getOwnerLegalEntity().getPerson().getDisplayName());
		
	}
	
	
	
	private ProductType productType;
	
	
	public LegalEntity getVendorLegalEntity() {
		return ownerLegalEntity;
	}
	public LegalEntity getOwnerLegalEntity() {
		return vendorLegalEntity;
	}
	
	
	
	
	
	
	public ProductType getProductType() {
		return productType;
	}
	
	/**
	 * sets the {@link ProductType} to display the MoneyFlowConfiguration for
	 * @param productType the proudctType to set
	 */
	public void setProductType(final ProductType productType) 
	{
		this.productType = productType;
		
		ownerLegalEntity = productType.getOwner();
		
		vendorLegalEntity = productType.getVendor();
		
		updateUI();
		
	}	
	
	protected void updateDelegate(final LocalAccountantDelegate delegate) {
	
	}
	

private ListenerList legalEntityValueChangedListeners = new ListenerList();
	
	
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
