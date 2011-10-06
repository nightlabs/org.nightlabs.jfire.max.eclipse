package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherLocalAccountantDelegateDAO;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

public class SelectLocalAccountantDelegatePage
extends WizardHopPage
{
	public static enum Mode {
		INHERIT,
		CREATE,
		SELECT
	}

	private ProductTypeID parentVoucherTypeID;

	private Button inheritAccountantDelegate;
	private Button createAccountantDelegate;
	private Button selectAccountantDelegate;
	private ListComposite<VoucherLocalAccountantDelegate> accountantDelegateList;

	private Mode mode;

	public SelectLocalAccountantDelegatePage(ProductTypeID parentVoucherTypeID)
	{
		super(SelectLocalAccountantDelegatePage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(VoucherAdminPlugin.getDefault(), SelectLocalAccountantDelegatePage.class));
		setDescription(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.description")); //$NON-NLS-1$
		this.parentVoucherTypeID = parentVoucherTypeID;
		new WizardHop(this);
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		inheritAccountantDelegate = new Button(page, SWT.RADIO);
		inheritAccountantDelegate.setSelection(true);
		inheritAccountantDelegate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mode = Mode.INHERIT;
		setInheritedLocalAccountantDelegateName(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.inheritedLocalAccountantDelegateName_loadingData")); //$NON-NLS-1$
		inheritAccountantDelegate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		});

		createAccountantDelegate = new Button(page, SWT.RADIO);
		createAccountantDelegate.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.createAccountantDelegateRadio.text")); //$NON-NLS-1$
		createAccountantDelegate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		});

		selectAccountantDelegate = new Button(page, SWT.RADIO);
		selectAccountantDelegate.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.selectAccountantDelegateRadio.text")); //$NON-NLS-1$
		selectAccountantDelegate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		});

		accountantDelegateList = new ListComposite<VoucherLocalAccountantDelegate>(page,
				AbstractListComposite.getDefaultWidgetStyle(page),
				(String) null, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((LocalAccountantDelegate)element).getName().getText();
			}
		});
		accountantDelegateList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				inheritAccountantDelegate.setSelection(false);
				createAccountantDelegate.setSelection(false);
				selectAccountantDelegate.setSelection(true); // because of a bug in swt, we must clear the selection above - it doesn't clear itself here
				selectedLocalAccountantDelegate = accountantDelegateList.getSelectedElement();
				updateUI();
			}
		});

		VoucherLocalAccountantDelegate dummy = new VoucherLocalAccountantDelegate("", ""); //$NON-NLS-1$ //$NON-NLS-2$
		dummy.getName().setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.accountantDelegateList.item_loadingData")); //$NON-NLS-1$
		accountantDelegateList.addElement(dummy);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.loadJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final VoucherType parentVoucherType = parentVoucherTypeID == null ? null : VoucherTypeDAO.sharedInstance().getVoucherType(
						parentVoucherTypeID,
						new String[] { FetchPlan.DEFAULT,  ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL, ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE, LocalAccountantDelegate.FETCH_GROUP_NAME},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				final List<VoucherLocalAccountantDelegate> delegates = VoucherLocalAccountantDelegateDAO.sharedInstance().getVoucherLocalAccountantDelegates(
						FETCH_GROUPS_LOCAL_ACCOUNTANT_DELEGATE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Collections.sort(delegates, new Comparator<VoucherLocalAccountantDelegate>() {
					public int compare(VoucherLocalAccountantDelegate d1, VoucherLocalAccountantDelegate d2)
					{
						return d1.getName().getText().compareTo(d2.getName().getText());
					}
				});

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						inheritedLocalAccountantDelegate = null;
						if (parentVoucherType != null)
							inheritedLocalAccountantDelegate = (VoucherLocalAccountantDelegate) parentVoucherType.getProductTypeLocal().getLocalAccountantDelegate();

						if (inheritedLocalAccountantDelegate == null)
							setInheritedLocalAccountantDelegateName(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.inheritedLocalAccountantDelegateName_noneAssigned")); //$NON-NLS-1$
						else
							setInheritedLocalAccountantDelegateName(inheritedLocalAccountantDelegate.getName().getText());

						accountantDelegateList.removeAll();
						accountantDelegateList.addElements(delegates);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();

		return page;
	}

	public static final String[] FETCH_GROUPS_LOCAL_ACCOUNTANT_DELEGATE = {
		FetchPlan.DEFAULT, LocalAccountantDelegate.FETCH_GROUP_NAME,
		VoucherLocalAccountantDelegate.FETCH_GROUP_VOUCHER_LOCAL_ACCOUNTS,
		VoucherLocalAccountantDelegate.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_CURRENCY
	};

	private void setInheritedLocalAccountantDelegateName(String name)
	{
		inheritAccountantDelegate.setText(String.format(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage.inheritAccountantDelegateRadio.text"), name)); //$NON-NLS-1$
	}

	private VoucherLocalAccountantDelegate inheritedLocalAccountantDelegate;
	private VoucherLocalAccountantDelegate selectedLocalAccountantDelegate;

	private CreateLocalAccountantDelegatePage createLocalAccountantDelegatePage = null;

	private void addCreateLocalAccountantDelegatePage()
	{
		if (createLocalAccountantDelegatePage == null)
			createLocalAccountantDelegatePage = new CreateLocalAccountantDelegatePage();

		if (!getWizardHop().getHopPages().contains(createLocalAccountantDelegatePage))
			getWizardHop().addHopPage(createLocalAccountantDelegatePage);
	}
	private void removeCreateLocalAccountantDelegatePage()
	{
		if (createLocalAccountantDelegatePage == null)
			return;

		getWizardHop().removeHopPage(createLocalAccountantDelegatePage);
	}

	private void updateUI()
	{
		if (inheritAccountantDelegate.getSelection())
			mode = Mode.INHERIT;
		else if (createAccountantDelegate.getSelection())
			mode = Mode.CREATE;
		else if (selectAccountantDelegate.getSelection())
			mode = Mode.SELECT;
		else
			throw new IllegalStateException("What's that?!"); //$NON-NLS-1$

		if (mode == Mode.CREATE)
			addCreateLocalAccountantDelegatePage();
		else
			removeCreateLocalAccountantDelegatePage();

		getContainer().updateButtons();
	}

	public Mode getMode()
	{
		return mode;
	}

	public VoucherLocalAccountantDelegate getSelectedLocalAccountantDelegate()
	{
		if (mode == Mode.SELECT)
			return selectedLocalAccountantDelegate;
		else
			return null;
	}

	public VoucherLocalAccountantDelegate createVoucherLocalAccountantDelegate()
	{
		if (mode != Mode.CREATE)
			throw new IllegalStateException("Cannot create in mode " + mode); //$NON-NLS-1$

		return createLocalAccountantDelegatePage.createVoucherLocalAccountantDelegate();
	}

	@Override
	public boolean isPageComplete()
	{
		if (mode != null) {
			switch (mode) {
			case INHERIT:
				return true;
			case CREATE:
				return true;
			case SELECT:
				return getSelectedLocalAccountantDelegate() != null;
			default:
				throw new IllegalStateException("What's this?!"); //$NON-NLS-1$
			}
		}
		else
			return false;
	}

	public VoucherLocalAccountantDelegate getInheritedLocalAccountantDelegate()
	{
		return inheritedLocalAccountantDelegate;
	}
}
