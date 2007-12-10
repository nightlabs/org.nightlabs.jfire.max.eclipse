package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collections;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;

import com.sun.corba.se.spi.ior.MakeImmutable;

public class IssueTypePrioritySection extends ToolBarSectionPart {

	private IssueTypeEditorPageController controller;
	private IssuePriorityTable issuePriorityTable;
	
	public IssueTypePrioritySection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Section Title");
		this.controller = controller;
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		issuePriorityTable = new IssuePriorityTable(client, SWT.NONE);

		getSection().setClient(client);
		
		IncreasePriorityAction iAction = new IncreasePriorityAction();
		DecreasePriorityAction dAction = new DecreasePriorityAction();
		getToolBarManager().add(dAction);
		getToolBarManager().add(iAction);
		
		updateToolBarManager();
	}

	public void setIssueType(IssueType issueType){
		issuePriorityTable.setInput(issueType.getIssuePriorities());
	}
	
	class IncreasePriorityAction 
	extends Action 
	{		
		public IncreasePriorityAction() {
			super();
			setId(IncreasePriorityAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypePrioritySection.class, 
					"Down"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
			int index = controller.getIssueType().getIssuePriorities().indexOf(issuePriorityTable.getFirstSelectedElement());
			if(index < controller.getIssueType().getIssuePriorities().size() - 1){
				Collections.swap(controller.getIssueType().getIssuePriorities(), index, index + 1);
				issuePriorityTable.setInput(controller.getIssueType().getIssuePriorities());
				markDirty();
			}//if
		}		
	}
	
	class DecreasePriorityAction
	extends Action
	{
		public DecreasePriorityAction() {
			super();
			setId(DecreasePriorityAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypePrioritySection.class, 
					"Up"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
			int index = controller.getIssueType().getIssuePriorities().indexOf(issuePriorityTable.getFirstSelectedElement());
			if(index > 0){
				Collections.swap(controller.getIssueType().getIssuePriorities(), index, index - 1);
				issuePriorityTable.setInput(controller.getIssueType().getIssuePriorities());
				markDirty();
			}//if
		}	
	}
}
