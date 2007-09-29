package org.nightlabs.jfire.trade.account.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageControllerModifyListener;
import org.nightlabs.jfire.accounting.MoneyTransfer;
import org.nightlabs.jfire.accounting.query.AbstractMoneyTransferQuery;
import org.nightlabs.jfire.trade.account.transfer.MoneyTransferTable;
import org.nightlabs.jfire.trade.resource.Messages;

/* 
* @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
*/
public class MoneyTransferListSection extends RestorableSectionPart{

	private MoneyTransferTable moneyTransferTable;
	private MoneyTransferPageController controller;
	
	public MoneyTransferListSection(FormPage page, Composite parent, MoneyTransferPageController controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		this.controller = controller;
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.editor.account.ManualMoneyTransferPage.sectionTitle"));
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

				moneyTransferQueryChanged((AbstractMoneyTransferQuery<?>) evt.getNewValue());
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
		moneyTransferQueryChanged(this.controller.getMoneyTransferQuery());
		
		List<MoneyTransfer> moneyTransferList = this.controller.getMoneyTransferList();
		if (moneyTransferList != null)
			moneyTransferListChanged(moneyTransferList);
		
		getSection().setClient(client);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		getSection().setLayoutData(gridData);
	}

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
	private void moneyTransferQueryChanged(AbstractMoneyTransferQuery<?> moneyTransferQuery)
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
}
