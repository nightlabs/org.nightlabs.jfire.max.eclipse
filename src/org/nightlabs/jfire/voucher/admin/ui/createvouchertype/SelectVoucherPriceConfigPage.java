package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

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
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.priceconfig.IPackagePriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.accounting.VoucherPriceConfig;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherPriceConfigDAO;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

public class SelectVoucherPriceConfigPage
extends WizardHopPage
{
	private ProductTypeID parentVoucherTypeID;

	public static enum Mode {
		INHERIT,
		CREATE,
		SELECT,
		NONE
	}

	private Mode mode;

	private Button inheritPriceConfig;
	private Button createPriceConfig;
	private Button selectPriceConfig;
	private Button assignNoneConfig;
	private ListComposite<VoucherPriceConfig> priceConfigList;

	private CreateVoucherPriceConfigPage createVoucherPriceConfigPage = null;

	public SelectVoucherPriceConfigPage(ProductTypeID parentVoucherTypeID)
	{
		super(SelectVoucherPriceConfigPage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(VoucherAdminPlugin.getDefault(), SelectVoucherPriceConfigPage.class));
		this.parentVoucherTypeID = parentVoucherTypeID;
		new WizardHop(this);
	}

	private VoucherPriceConfig selectedPriceConfig = null;

	private void addCreateVoucherPriceConfigPage()
	{
		if (createVoucherPriceConfigPage == null)
			createVoucherPriceConfigPage = new CreateVoucherPriceConfigPage();

		if (!getWizardHop().getHopPages().contains(createVoucherPriceConfigPage))
			getWizardHop().addHopPage(createVoucherPriceConfigPage);
	}

	private void removeCreateVoucherPriceConfigPage()
	{
		if (createVoucherPriceConfigPage == null)
			return;

		getWizardHop().removeHopPage(createVoucherPriceConfigPage);
	}

	private void updateUI()
	{



		if (inheritPriceConfig.getSelection())
			mode = Mode.INHERIT;
		else if (createPriceConfig.getSelection())
			mode = Mode.CREATE;
		else if (selectPriceConfig.getSelection())
			mode = Mode.SELECT;
		else if (assignNoneConfig.getSelection())
			mode = Mode.NONE;
		else
			throw new IllegalStateException("What's that?!"); //$NON-NLS-1$


		if (mode == Mode.NONE)
			priceConfigList.setEnabled(false);
		else
			priceConfigList.setEnabled(true);

		if (mode == Mode.CREATE)
			addCreateVoucherPriceConfigPage();
		else
			removeCreateVoucherPriceConfigPage();

		getContainer().updateButtons();
	}

	private void setInheritedPriceConfigName(String name)
	{
		inheritPriceConfig.setText(String.format(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.inheritPriceConfigRadio.text"), name)); //$NON-NLS-1$
	}

	@Override
	@Implement
	public Control createPageContents(Composite parent)
	{
		final XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		inheritPriceConfig = new Button(page, SWT.RADIO);
		inheritPriceConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inheritPriceConfig.setSelection(true);
		mode = Mode.INHERIT;
		inheritPriceConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		});

		setInheritedPriceConfigName(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.inheritedPriceConfigName_loadingData")); //$NON-NLS-1$

		createPriceConfig = new Button(page, SWT.RADIO);
		createPriceConfig.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.createPriceConfigRadio.text")); //$NON-NLS-1$
		createPriceConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createPriceConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		});

		selectPriceConfig = new Button(page, SWT.RADIO);
		selectPriceConfig.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.selectPriceConfigRadio.text")); //$NON-NLS-1$
		selectPriceConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectPriceConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		}

		);

		assignNoneConfig = new Button(page, SWT.RADIO);
		assignNoneConfig.setText("Assign None"); //$NON-NLS-1$
		assignNoneConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		assignNoneConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		}

		);

		priceConfigList = new ListComposite<VoucherPriceConfig>(page,
				AbstractListComposite.getDefaultWidgetStyle(page),(String) null,
				new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				VoucherPriceConfig vpc = (VoucherPriceConfig) element;
				return vpc.getName().getText();
			}
		});
		VoucherPriceConfig dummy = new VoucherPriceConfig("dummy", "dummy"); //$NON-NLS-1$  //$NON-NLS-2$
		dummy.getName().setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.priceConfigList.item_loadingData")); //$NON-NLS-1$
		priceConfigList.addElement(dummy);

		priceConfigList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				inheritPriceConfig.setSelection(false);
				createPriceConfig.setSelection(false);
				assignNoneConfig.setSelection(false);
				selectPriceConfig.setSelection(true); // because of a bug in swt, we must clear the selection above - it doesn't clear itself here
				selectedPriceConfig = priceConfigList.getSelectedElement();
				updateUI();
			}
		});

		page.setEnabled(false);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.loadJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final VoucherType parentVoucherType = VoucherTypeDAO.sharedInstance().getVoucherType(
						parentVoucherTypeID,
						new String[] { FetchPlan.DEFAULT,  ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG, PriceConfig.FETCH_GROUP_NAME},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				final List<VoucherPriceConfig> voucherPriceConfigs = VoucherPriceConfigDAO.sharedInstance().getVoucherPriceConfigs(
						new String[] { FetchPlan.DEFAULT, PriceConfig.FETCH_GROUP_NAME }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						inheritedPriceConfig = parentVoucherType.getPackagePriceConfig();
						if (inheritedPriceConfig == null)
							setInheritedPriceConfigName(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage.inheritedPriceConfigName_noneAssigned")); //$NON-NLS-1$
						else
							setInheritedPriceConfigName(inheritedPriceConfig.getName().getText());

						priceConfigList.removeAll();
						priceConfigList.addElements(voucherPriceConfigs);

						page.setEnabled(true);

						updateUI();
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();

		return page;
	}

	private IPackagePriceConfig inheritedPriceConfig;
	public IPackagePriceConfig getInheritedPriceConfig()
	{
		return inheritedPriceConfig;
	}

	public VoucherPriceConfig createPriceConfig()
	{
		if (mode != Mode.CREATE)
			throw new IllegalStateException("mode != Mode.CREATE"); //$NON-NLS-1$

		return createVoucherPriceConfigPage.createPriceConfig();
	}

	public Mode getMode()
	{
		return mode;
	}

	public VoucherPriceConfig getSelectedPriceConfig()
	{
		if (mode != Mode.SELECT)
			return null;

		return selectedPriceConfig;
	}

	@Override
	public boolean isPageComplete()
	{
		switch (mode) {
		case INHERIT:
			return true;
		case CREATE:
			return true;
		case SELECT:
			return getSelectedPriceConfig() != null;
		case NONE:
			return true;
		default:
			throw new IllegalStateException("What's this?!"); //$NON-NLS-1$
		}
	}
}
