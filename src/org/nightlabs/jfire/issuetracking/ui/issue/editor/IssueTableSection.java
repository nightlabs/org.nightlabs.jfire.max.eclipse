package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferPageController;

/* 
* @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
*/
public class IssueTableSection extends RestorableSectionPart{

	private IssueTable issueTable;
	private IssuePageController controller;
	
	public IssueTableSection(FormPage page, Composite parent, IssuePageController controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		this.controller = controller;
		getSection().setText("Issue List");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		issueTable = new IssueTable(
				client, SWT.NONE);
		issueTable.getGridData().grabExcessHorizontalSpace = true;
		
//		this.controller.addPropertyChangeListener(MoneyTransferPageController.PROPERTY_MONEY_TRANSFER_QUERY, new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent evt)
//			{
//				if (ignoreMoneyTransferQueryChanged)
//					return;
//
//				moneyTransferQueryChanged((AbstractMoneyTransferQuery<?>) evt.getNewValue());
//			}
//		});

//		this.controller.addModifyListener(new IEntityEditorPageControllerModifyListener() {
//			public void controllerObjectModified(final EntityEditorPageControllerModifyEvent modifyEvent)
//			{
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					@SuppressWarnings("unchecked") //$NON-NLS-1$
//					public void run()
//					{
//						moneyTransferListChanged((List<MoneyTransfer>) modifyEvent.getNewObject());
//					}
//				});
//			}
//		});
//		moneyTransferQueryChanged(this.controller.getMoneyTransferQuery());
		
//		List<Issue> issueList = this.controller.getIssueList();
//		if (issueList != null)
//			issueTable.setLoadingStatus();
//			moneyTransferListChanged(moneyTransferList);
		
		getSection().setClient(client);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		getSection().setLayoutData(gridData);
	}

//	private boolean ignoreMoneyTransferQueryChanged = false;
//	/**
//	 * must be called on UI thread!
//	 */
//	private void fireProductTransferQueryChanged()
//	{
//		ignoreMoneyTransferQueryChanged = true;
//		try {
//			controller.fireMoneyTransferQueryChange();
//		} finally {
//			ignoreMoneyTransferQueryChanged = false;
//		}
//	}
	
//	/**
//	 * This method is called on the UI thread whenever the productTransferQuery has changed.
//	 * It is not called, if the change originated from here (i.e. {@link #fireProductTransferQueryChanged()} in
//	 * this object).
//	 */
//	private void moneyTransferQueryChanged(AbstractMoneyTransferQuery<?> moneyTransferQuery)
//	{
//		issueTable.setLoadingStatus();
//	}
//
//	/**
//	 * this method is called on the UI thread.
//	 */
//	private void moneyTransferListChanged(List<MoneyTransfer> moneyTransferList)
//	{
//		issueTable.setMoneyTransfers(controller.getCurrentAnchorID(), moneyTransferList);
//	}
}
