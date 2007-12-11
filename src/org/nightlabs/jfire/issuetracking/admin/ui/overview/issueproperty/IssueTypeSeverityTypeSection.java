package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

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

public class IssueTypeSeverityTypeSection extends ToolBarSectionPart {

	private IssueTypeEditorPageController controller;
	private IssueSeverityTypeTable issueSeverityTypeTable;
	
	public IssueTypeSeverityTypeSection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Section Title");
		this.controller = controller;
		getSection().setText("Section Title");
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		issueSeverityTypeTable = new IssueSeverityTypeTable(client, SWT.NONE);
		getSection().setClient(client);
		
		CreateSeverityTypeAction createAction = new CreateSeverityTypeAction();
		DeleteSeverityTypeAction deleteAction = new DeleteSeverityTypeAction();
		EditSeverityTypeAction editAction = new EditSeverityTypeAction();
		
		getToolBarManager().add(createAction);
		getToolBarManager().add(deleteAction);
		getToolBarManager().add(editAction);
		
		updateToolBarManager();
	}
	
	public void setIssueType(IssueType issueType){
		issueSeverityTypeTable.setInput(issueType.getIssueSeverityTypes());
	}
	
	class CreateSeverityTypeAction 
	extends Action 
	{		
		public CreateSeverityTypeAction() {
			super();
			setId(CreateSeverityTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeSeverityTypeSection.class, 
					"Create"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
		}		
	}
	
	class DeleteSeverityTypeAction 
	extends Action 
	{		
		public DeleteSeverityTypeAction() {
			super();
			setId(CreateSeverityTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeSeverityTypeSection.class, 
					"Delete"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
		}		
	}
	
	class EditSeverityTypeAction 
	extends Action 
	{		
		public EditSeverityTypeAction() {
			super();
			setId(CreateSeverityTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeSeverityTypeSection.class, 
					"Edit"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
		}		
	}
}
