package org.nightlabs.jfire.trade.admin.ui.moneyflow;

import java.util.Iterator;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
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
import org.nightlabs.jfire.accounting.book.mappingbased.MappingBasedAccountantDelegate;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.AddMoneyFlowMappingWizard;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectCreateAccountantDelegateWizard;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class MoneyFlowConfigComposite
extends XComposite
{
	private boolean showButtons = true;
	public MoneyFlowConfigComposite(Composite parent, int style,
			IDirtyStateManager dirtyStateManager, boolean showButtons)
	{
		super(parent, style);
		this.dirtyStateManager = dirtyStateManager;
		this.showButtons = showButtons;
		createPartContents(this);
	}

	public MoneyFlowConfigComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			IDirtyStateManager dirtyStateManager, boolean showButtons)
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.dirtyStateManager = dirtyStateManager;
		this.showButtons = showButtons;
		createPartContents(this);
	}

	private IDirtyStateManager dirtyStateManager;
	private MoneyFlowMappingTree productTypeMappingTree;
	public MoneyFlowMappingTree getProductTypeMappingTree() {
		return productTypeMappingTree;
	}

	private ResolvedMappingTree resolvedMappingTree;
	private ProductTypeID currProductTypeID;

	private FadeableComposite fadeableComposite;

	private Composite stackWrapper;
	private StackLayout stackLayout;
	private XComposite treeWrapper;

	private XComposite noDelegateComp;

	private NotificationListener notificationListenerProductTypeSelected = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite.loadMoneyFlowConfigJob.name")) {		 //$NON-NLS-1$
		public void notify(NotificationEvent event) {
			boolean doRefresh = false;
			ProductTypeID productTypeID = null;
			if (event.getSubjects().isEmpty()) {
				doRefresh = currProductTypeID != null;
				currProductTypeID = null;
			}
			else {
				for (Iterator it = event.getSubjects().iterator(); it.hasNext(); ) {
					Object subject = it.next();
					if (subject instanceof ProductTypeID) {
						productTypeID = (ProductTypeID)subject;
						doRefresh = (productTypeID != null) && (!productTypeID.equals(currProductTypeID));
						currProductTypeID = productTypeID;
					}
					else {
						doRefresh = currProductTypeID != null;
						currProductTypeID = null;
					}
				}
			}
			if (doRefresh) {
				setProductTypeID(currProductTypeID);
			}
		}
	};

	public NotificationListener getNotificationListener() {
		return notificationListenerProductTypeSelected;
	}

	private ProductType productType;
	public ProductType getProductType() {
		return productType;
	}

	/**
	 * @deprecated use {@link #setProductType(ProductType)} instead
	 *
	 * sets the id of the {@link ProductType} to load
	 * @param productTypeID the id of the {@link ProductType} to set
	 */
	@Deprecated
	public void setProductTypeID(final ProductTypeID productTypeID)
	{
		currProductTypeID = productTypeID;
		Job updateJob = new FadeableCompositeJob(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite.updateMoneyFlowConfigJob.name"), fadeableComposite, this) { //$NON-NLS-1$

			@Override
			protected IStatus run(ProgressMonitor monitor, Object source) throws Exception {
				if (productTypeID != null)
					productType = ProductTypeDAO.sharedInstance().getProductType(
							productTypeID, MoneyFlowMappingTree.DEFAULT_PTYPE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor);
				if ((productType == null) || (productType.getProductTypeLocal().getLocalAccountantDelegate() == null)) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							stackLayout.topControl = noDelegateComp;
							stackWrapper.layout(true);
						}
					});
				}
				else {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							stackLayout.topControl = treeWrapper;
							stackWrapper.layout(true);
						}
					});
				}
				productTypeMappingTree.setProductTypeID(productTypeID, monitor);

				ProductType pType = ProductTypeDAO.sharedInstance().getProductType(
						productTypeID, new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor);
				if (pType != null && pType.isSaleable())
					resolvedMappingTree.setProductTypeID(productTypeID, monitor);
				else
					resolvedMappingTree.clear();

				return Status.OK_STATUS;
			}
		};
		updateJob.schedule();
	}

	/**
	 * sets the {@link ProductType} to display the MoneyFlowConfiguration for
	 * @param productType the proudctType to set
	 */
	public void setProductType(final ProductType productType)
	{
		this.productType = productType;
		currProductTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
		Job updateJob = new FadeableCompositeJob(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite.updateMoneyFlowConfigJob.name"), fadeableComposite, this) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor, Object source) throws Exception {
				if ((productType == null) || (productType.getProductTypeLocal().getLocalAccountantDelegate() == null)) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (stackWrapper.isDisposed())
								return;

							stackLayout.topControl = noDelegateComp;
							stackWrapper.layout(true);
						}
					});
				}
				else {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (stackWrapper.isDisposed())
								return;

							stackLayout.topControl = treeWrapper;
							stackWrapper.layout(true);
						}
					});
				}
				productTypeMappingTree.setProductType(productType);
				resolvedMappingTree.setProductType(productType);

				return Status.OK_STATUS;
			}
		};
		updateJob.schedule();
	}

	protected void updateDelegate(final LocalAccountantDelegate delegate) {
		if (currProductTypeID != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					stackLayout.topControl = treeWrapper;
					stackWrapper.layout(true);
					productTypeMappingTree.setDelegateID((LocalAccountantDelegateID)JDOHelper.getObjectId(delegate));
				}
			});
		}
	}

	/**
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartContents(Composite parent)
	{
		fadeableComposite = new FadeableComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		stackWrapper = new Composite(fadeableComposite, SWT.NONE);
		stackLayout = new StackLayout();
		stackWrapper.setLayout(stackLayout);
		stackWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));

		treeWrapper = new XComposite(stackWrapper, SWT.NONE);
		treeWrapper.getGridLayout().makeColumnsEqualWidth = true;
		treeWrapper.setLayout(new GridLayout(2, false));
//		XComposite comp = new XComposite(treeWrapper, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
		XComposite comp = new XComposite(treeWrapper, getBorderStyle(), LayoutMode.TIGHT_WRAPPER);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		productTypeMappingTree = new MoneyFlowMappingTree(comp, null);
		resolvedMappingTree = new ResolvedMappingTree(comp);

		if (showButtons) {
			Composite buttonParent = new XComposite(treeWrapper, SWT.NONE);
			buttonParent.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			Button addDelegateButton = new Button(buttonParent, SWT.NONE);
			addDelegateButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite.addDelegateButton.text")); //$NON-NLS-1$
			addDelegateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addDelegateButton.addSelectionListener(addDelegateListener);

			Button removeDelegateButton = new Button(buttonParent, SWT.NONE);
			removeDelegateButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite.removeDelegateButton.text")); //$NON-NLS-1$
			removeDelegateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeDelegateButton.addSelectionListener(removeDelegateListener);
		}

		noDelegateComp = new XComposite(stackWrapper, SWT.NONE);
		Label label = new Label(noDelegateComp, SWT.WRAP);
		label.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite.noDelegateLabel.text")); //$NON-NLS-1$
		Button button = new Button(noDelegateComp, SWT.FLAT);
		button.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite.assignDelegateButton.text")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				openSelectedAccountDelegateWizard();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		stackLayout.topControl = treeWrapper;
	}

	public void addDelegate()
	{
		if (getCurrProductTypeID() == null || getCurrDelegate() == null)
			return;

		AddMoneyFlowMappingWizard wiz = new AddMoneyFlowMappingWizard(
				getCurrDelegate(),
				getCurrProductTypeID()
			);
		DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(wiz) {
			@Override
			protected void configureShell(Shell newShell) {
				super.configureShell(newShell);
				newShell.setSize(500, 450);
			}
		};
		if (wizardDialog.open() == Window.OK) {
			setNextRefreshSelectMapping(wiz.getCreatedMapping());
			refresh(true);

			if (dirtyStateManager != null)
				dirtyStateManager.markDirty();
		}
	}

	private SelectionListener addDelegateListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			addDelegate();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	public void removeDelegate()
	{
		if (getCurrProductTypeID() == null)
			return;

		MoneyFlowMapping mapping = getSelectedMoneyFlowMapping();
		if (mapping == null)
			return;
		LocalAccountantDelegate _delegate = getSelectedLocalAccountantDelegate();
		if (_delegate == null)
			return;
		if (!(_delegate instanceof MappingBasedAccountantDelegate))
			return;
		MappingBasedAccountantDelegate delegate = (MappingBasedAccountantDelegate) _delegate;
		// TODO: Popup confirmation dialog
		delegate.removeMoneyFlowMapping(mapping);
		refresh(true);

		if (dirtyStateManager != null)
			dirtyStateManager.markDirty();
	}

	private SelectionListener removeDelegateListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			removeDelegate();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	public ProductTypeID getCurrProductTypeID() {
		return currProductTypeID;
	}

	public MappingBasedAccountantDelegate getCurrDelegate() {
		return productTypeMappingTree.getCurrentDelegate();
	}

	public void refresh(boolean refreshInput) {
		productTypeMappingTree.refresh(refreshInput);
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowMappingTree#setLastAddedMapping(org.nightlabs.jfire.accounting.book.MoneyFlowMapping)
	 */
	public void setNextRefreshSelectMapping(MoneyFlowMapping nextRefreshSelectMapping) {
		productTypeMappingTree.setLastAddedMapping(nextRefreshSelectMapping);
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowMappingTree#getSelectedLocalAccountantDelegate()
	 */
	public LocalAccountantDelegate getSelectedLocalAccountantDelegate() {
		return productTypeMappingTree.getSelectedLocalAccountantDelegate();
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowMappingTree#getSelectedMoneyFlowMapping()
	 */
	public MoneyFlowMapping getSelectedMoneyFlowMapping() {
		return productTypeMappingTree.getSelectedMoneyFlowMapping();
	}

//	public void openSelectedAccountDelegateWizard()
//	{
//		final SelectCreateAccountantDelegateWizard wiz = new SelectCreateAccountantDelegateWizard();
//		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
//		dlg.open();
//		if (wiz.getSelectedDelegate() != null) {
//			Job assignJob = new Job("Assigning delegate") {
//				@Override
//				protected IStatus run(ProgressMonitor monitor) {
//					if (currProductTypeID != null) {
//						try {
//							// selected delegate is assigned
//							getProductType().setLocalAccountantDelegate(wiz.getSelectedDelegate());
//							if (dirtyStateManager != null) {
//								Display.getDefault().syncExec(new Runnable(){
//									public void run() {
//										dirtyStateManager.markDirty();
//									}
//								});
//							}
//						} catch (Exception t) {
//							throw new RuntimeException(t);
//						}
//					}
//					updateDelegate(wiz.getSelectedDelegate());
//					resolvedMappingTree.setProductType(getProductType());
//					return Status.OK_STATUS;
//				}
//			};
//			assignJob.setUser(true);
//			assignJob.schedule();
//		}
//	}
	public void openSelectedAccountDelegateWizard()
	{
		final SelectCreateAccountantDelegateWizard wiz = new SelectCreateAccountantDelegateWizard();
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		dlg.open();
		if (wiz.getSelectedDelegate() != null) {
			if (currProductTypeID != null) {
				try {
					// selected delegate is assigned
					// TODO what about the inheritance? The meta-data is not set - I'll set it here, but I'm not sure whether it's set already somewhere else. IMHO here, it makes sense - otherwise our setting would be overwritten...
					getProductType().getProductTypeLocal().getFieldMetaData(ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited(wiz.isLocalAccountantDelegateInherited());
					getProductType().getProductTypeLocal().setLocalAccountantDelegate(wiz.getSelectedDelegate());
					if (dirtyStateManager != null)
						dirtyStateManager.markDirty();
				} catch (Exception t) {
					throw new RuntimeException(t);
				}
			}
			updateDelegate(wiz.getSelectedDelegate());
			resolvedMappingTree.setProductType(getProductType());
		}
	}

}
