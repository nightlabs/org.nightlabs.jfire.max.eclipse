package org.nightlabs.jfire.trade.store.search;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.book.id.LocalAccountantDelegateID;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.store.deliver.id.DeliveryConfigurationID;
import org.nightlabs.jfire.store.id.ProductTypeGroupID;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeSearchCriteriaComposite 
extends XComposite 
{
	/**
	 * @param parent
	 * @param style
	 */
	public ProductTypeSearchCriteriaComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ProductTypeSearchCriteriaComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	protected void createComposite(Composite parent) 
	{
//		parent.setLayout(new RowLayout());
		parent.setLayout(new GridLayout(3, true));
		
		createSaleAccessComp(parent);

		createTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.deliveryConfigurationGroup.text"), deliveryConfigurationListener); //$NON-NLS-1$
		createTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.priceConfigurationGroup.text"), innerPriceConfigListener); //$NON-NLS-1$
		createTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.accountConfigurationGroup.text"), localAccountDelegateListener); //$NON-NLS-1$
		createTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.ownerGroup.text"), ownerListener); //$NON-NLS-1$
		createTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.productTypeGroupGroup.text"), productTypeGroupListener);		 //$NON-NLS-1$
	}

	private SelectionListener deliveryConfigurationListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select delivery configuration and set selectedDeliveryConfigurationID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}				
	};

	private SelectionListener innerPriceConfigListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select price configuration and set selectedPriceConfigID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}				
	};
	
	private SelectionListener localAccountDelegateListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select account configuration and set selectedLocalAccountDelegateID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}				
	};

	private SelectionListener ownerListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select owner and set selectedOwnerID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}				
	};

	private SelectionListener productTypeGroupListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select productType and set selectedLocalAccountDelegateID	
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}				
	};
	
	private Button activeButtonState = null;
	private XComboComposite<SaleAccessState> stateCombo = null;
	protected void createSaleAccessComp(Composite parent) 
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.saleAccessGroup.text")); //$NON-NLS-1$
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
		activeButtonState = new Button(group, SWT.CHECK);
		activeButtonState.setText(Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.activeStateButton.text")); //$NON-NLS-1$
		activeButtonState.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				stateCombo.setEnabled(((Button)e.getSource()).getSelection());				
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});

		stateCombo = new XComboComposite<SaleAccessState>(group, SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				SaleAccessState state = (SaleAccessState) element;
				String prefix = "org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.saleAccessState_"; //$NON-NLS-1$
				return Messages.getString(prefix + state.name());
			}
		});
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		stateCombo.setLayoutData(data);				
		stateCombo.addElements(CollectionUtil.array2ArrayList(SaleAccessState.values()));
		stateCombo.selectElement(selectedSaleAccessState);		
		stateCombo.setEnabled(false);		
		stateCombo.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				selectedSaleAccessState = stateCombo.getSelectedElement();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
	}

	public enum SaleAccessState {
		PUBLISHED, CONFIRMED, SALEABLE, CLOSED
	}

	protected void createTextComposite(Composite parent, String groupTitle, SelectionListener selectionListener) 
	{
		final Group group = new Group(parent, SWT.NONE);
		group.setText(groupTitle);
		group.setLayout(new GridLayout(2, false));	
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button activeButton = new Button(group, SWT.CHECK);
		GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
		buttonData.horizontalSpan = 2;		
		activeButton.setLayoutData(buttonData);
		activeButton.setText(Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.activeButton.text"));		  //$NON-NLS-1$
		Text textField = new Text(group, SWT.BORDER);		
		final Text text = textField;
		text.setEnabled(false);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		text.addSelectionListener(selectionListener);
		final Button browseButton = new Button(group, SWT.NONE);
		browseButton.setText(Messages.getString("org.nightlabs.jfire.trade.store.search.ProductTypeSearchCriteriaComposite.browseButton.text")); //$NON-NLS-1$
		browseButton.addSelectionListener(selectionListener);
		browseButton.setEnabled(false);
		
		activeButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				text.setEnabled(((Button)e.getSource()).getSelection());
				browseButton.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});		
	}

	private DeliveryConfigurationID selectedDeliveryConfigurationID = null;
	public DeliveryConfigurationID getSelectedDeliveryConfigurationID() {
		return selectedDeliveryConfigurationID;
	}
	
	private PriceConfigID selectedPriceConfigID = null;
	public PriceConfigID getSelectedPriceConfigID() {
		return selectedPriceConfigID;
	}
	
	private AnchorID selectedOwnerID = null;
	public AnchorID getSelectedOwnerID() {
		return selectedOwnerID;
	}

	private ProductTypeGroupID selectedProductTypeGroupID = null;
	public ProductTypeGroupID getSelectedProductTypeGroupID() {
		return selectedProductTypeGroupID;
	}
	
	private LocalAccountantDelegateID selectedLocalAccountantDelegateID = null;
	public LocalAccountantDelegateID getSelectedLocalAccountantDelegateID() {
		return selectedLocalAccountantDelegateID;
	}

	private SaleAccessState selectedSaleAccessState = SaleAccessState.PUBLISHED;
	public SaleAccessState getSelectedSaleAccessState() {
		return selectedSaleAccessState;
	}
	
}
