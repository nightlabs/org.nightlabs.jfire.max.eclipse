package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.store.reverse.ReverseProductException;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ReverseProductPage 
extends DynamicPathWizardPage
{
	private static final Logger logger = Logger.getLogger(ReverseProductPage.class);
	
	private Text organisationIDText;
	private Text productIDText;	
	private Button reverseAllButton;
	private Button reverseArticleButton;
	
	public ReverseProductPage() {
		super(ReverseProductPage.class.getName(), "Reverse Product");
		setDescription("Enter the productID to search for the corresponding order to reverse");
	}

	@Override
	public Control createPageContents(Composite parent) {
		Composite wrapper = new XComposite(parent, SWT.NONE);
		
		Composite searchWrapper = new XComposite(wrapper, SWT.NONE);
		searchWrapper.setLayout(new GridLayout(2, false));
		searchWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label organisationIDLabel = new Label(searchWrapper, SWT.NONE);
		organisationIDLabel.setText("Organisation ID");
		organisationIDText = new Text(searchWrapper, SWT.BORDER);
		organisationIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		organisationIDText.setText(IDGenerator.getOrganisationID());
		organisationIDText.addModifyListener(organisationIDModifyListener);
		
		Label productIDLabel = new Label(searchWrapper, SWT.NONE);
		productIDLabel.setText("Product ID");
		productIDText = new Text(searchWrapper, SWT.BORDER);
		productIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		productIDText.addModifyListener(productIDModifyListener);
		productIDText.addSelectionListener(selectionListener);
		
		Composite chooseComposite = new XComposite(wrapper, SWT.NONE);				
		reverseAllButton = new Button(chooseComposite, SWT.RADIO);
		reverseAllButton.setText("Reverse Complete Offer");
		reverseAllButton.setSelection(true);
		reverseArticleButton = new Button(chooseComposite, SWT.RADIO);
		reverseArticleButton.setText("Reverse only Article");
				
		return wrapper;
	}
	
	private SelectionListener selectionListener = new SelectionAdapter() {		
		@Override
		public void widgetSelected(SelectionEvent e) {
			onFinish();
		}
	};
	
	private ModifyListener productIDModifyListener = new ModifyListener(){
		@Override
		public void modifyText(ModifyEvent e) {			
			try {
				// TODO: check for Base36 encoded string  
				Long id = Long.valueOf(productIDText.getText());
				setErrorMessage(null);
			} catch (NumberFormatException ex) {
				setErrorMessage("The entered text must be a number");
			}
		}
	};

	private ModifyListener organisationIDModifyListener = new ModifyListener(){
		@Override
		public void modifyText(ModifyEvent e) {
			// TODO: check if organisationID is existing
		}
	};
		
	protected void createReversingOffer(final ProductID productID) 
	{
		Job searchJob = new Job("Searching Product"){
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				try {
					Offer reversingOffer = tm.createReverseOfferForProduct(productID);
				} catch (ModuleException e) {
					if (e instanceof ReverseProductException) {
						ReverseProductException exception = (ReverseProductException) e;
						setErrorMessage(exception.getDescription());
					}
				}
				return Status.OK_STATUS;
			}
		};
		searchJob.schedule();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#onFinish()
	 */
	@Override
	public void onFinish() {
		super.onFinish();
		Long id = Long.valueOf(productIDText.getText());
		String organisationID = organisationIDText.getText();
		ProductID productID = ProductID.create(organisationID, id);
		createReversingOffer(productID);
	}	
}
