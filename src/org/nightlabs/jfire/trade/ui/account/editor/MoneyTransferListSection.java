package org.nightlabs.jfire.trade.ui.account.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageControllerModifyListener;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.ManualMoneyTransfer;
import org.nightlabs.jfire.accounting.MoneyTransfer;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable;
import org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
public class MoneyTransferListSection extends ToolBarSectionPart{

	private MoneyTransferTable moneyTransferTable;
	private MoneyTransferPageController controller;

	private ManualMoneyTransferAction transferMoneyAction;

	public MoneyTransferListSection(FormPage page, Composite parent, MoneyTransferPageController controller) {
		super(page, parent,	ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferListSection.section.title")); //$NON-NLS-1$
		this.controller = controller;
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferListSection.sectionTitle")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		moneyTransferTable = new MoneyTransferTable(
				client, SWT.NONE);
		moneyTransferTable.getGridData().grabExcessHorizontalSpace = true;

		this.controller.addPropertyChangeListener(MoneyTransferPageController.PROPERTY_MONEY_TRANSFER_QUERY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (ignoreMoneyTransferQueryChanged)
					return;

				moneyTransferQueryChanged((QueryCollection<?>) evt.getNewValue());
			}
		});

		JDOLifecycleManager.sharedInstance().addLifecycleListener(moneyTransferLifecycleListener);

		getSection().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				JDOLifecycleManager.sharedInstance().removeLifecycleListener(moneyTransferLifecycleListener);
			}
		});

		this.controller.addModifyListener(new IEntityEditorPageControllerModifyListener() {
			public void controllerObjectModified(final EntityEditorPageControllerModifyEvent modifyEvent)
			{
				Display.getDefault().asyncExec(new Runnable()
				{
					@SuppressWarnings("unchecked") //$NON-NLS-1$
					public void run()
					{
						moneyTransferListChanged((List<MoneyTransfer>) modifyEvent.getNewObject());
					}
				});
			}
		});
		moneyTransferQueryChanged(this.controller.getQueryWrapper());

		List<MoneyTransfer> moneyTransferList = this.controller.getMoneyTransferList();
		if (moneyTransferList != null)
			moneyTransferListChanged(moneyTransferList);

		getSection().setClient(client);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		getSection().setLayoutData(gridData);

		transferMoneyAction = new ManualMoneyTransferAction();
		getToolBarManager().add(transferMoneyAction);
		updateToolBarManager();
	}

	private JDOLifecycleListener moneyTransferLifecycleListener = new JDOLifecycleAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferPageController.loadMoneyTransfersJob.name")) { //$NON-NLS-1$
		private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(
				ManualMoneyTransfer.class, false, JDOLifecycleState.NEW, JDOLifecycleState.DELETED
		);

		@Override
		public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter() { return filter; }

		@Override
		public void notify(JDOLifecycleEvent event) {
			ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferPageController.loadMoneyTransfersJob.name"), 100); //$NON-NLS-1$
			try {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						controller.fireMoneyTransferQueryChange();
					}
				});

			} finally {
				monitor.done();
			}
		}
	};

	private boolean ignoreMoneyTransferQueryChanged = false;
	/**
	 * must be called on UI thread!
	 */
	private void fireProductTransferQueryChanged()
	{
		ignoreMoneyTransferQueryChanged = true;
		try {
			controller.fireMoneyTransferQueryChange();
		} finally {
			ignoreMoneyTransferQueryChanged = false;
		}
	}

	/**
	 * This method is called on the UI thread whenever the productTransferQuery has changed.
	 * It is not called, if the change originated from here (i.e. {@link #fireProductTransferQueryChanged()} in
	 * this object).
	 */
	private void moneyTransferQueryChanged(QueryCollection<?> queryCollection)
	{
		moneyTransferTable.setLoadingStatus();
	}

	/**
	 * this method is called on the UI thread.
	 */
	private void moneyTransferListChanged(List<MoneyTransfer> moneyTransferList)
	{
		moneyTransferTable.setMoneyTransfers(controller.getCurrentAnchorID(), moneyTransferList);
	}

	public class ManualMoneyTransferAction
	extends Action
	{
		public ManualMoneyTransferAction()
		{
			super();
			setId(ManualMoneyTransferAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					TradePlugin.getDefault(),
					MoneyTransferListSection.class,
			"Transfer")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferListSection.action.toolitp")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferListSection.action.text")); //$NON-NLS-1$
		}

		@Override
		public void run()
		{
			AnchorID accountID = controller.getCurrentAnchorID();

			if (accountID == null)
				return;

			ManualMoneyTransferWizard wizard = new ManualMoneyTransferWizard(accountID);
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
			dialog.open();
		}
	}
}
