package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.jfire.issue.IssuePriority;

public class IssueTypePrioritySection extends RestorableSectionPart {

	private IssueTypeEditorPageController controller;
	
	public IssueTypePrioritySection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		this.controller = controller;
		getSection().setText("Section Title");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		ListComposite<IssuePriority> issuePriorityList = new ListComposite<IssuePriority>(client, SWT.NONE);
//		issuePriorityList.addElements(controller.getEntityEditor().get))
		
//		moneyTransferTable = new MoneyTransferTable(
//				client, SWT.NONE);
//		moneyTransferTable.getGridData().grabExcessHorizontalSpace = true;
//		
//		this.controller.addPropertyChangeListener(MoneyTransferPageController.PROPERTY_MONEY_TRANSFER_QUERY, new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent evt)
//			{
//				if (ignoreMoneyTransferQueryChanged)
//					return;
//
//				moneyTransferQueryChanged((AbstractMoneyTransferQuery<?>) evt.getNewValue());
//			}
//		});
//
//		this.controller.addModifyListener(new IEntityEditorPageControllerModifyListener() {
//			public void controllerObjectModified(final EntityEditorPageControllerModifyEvent modifyEvent)
//			{
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					@SuppressWarnings("unchecked") 
//					public void run()
//					{
//						moneyTransferListChanged((List<MoneyTransfer>) modifyEvent.getNewObject());
//					}
//				});
//			}
//		});
//		moneyTransferQueryChanged(this.controller.getMoneyTransferQuery());
//		
//		List<MoneyTransfer> moneyTransferList = this.controller.getMoneyTransferList();
//		if (moneyTransferList != null)
//			moneyTransferListChanged(moneyTransferList);
		
		getSection().setClient(client);
		
//		GridData gridData = new GridData(GridData.FILL_BOTH);
//		gridData.grabExcessVerticalSpace = true;
//		getSection().setLayoutData(gridData);
	}

}
