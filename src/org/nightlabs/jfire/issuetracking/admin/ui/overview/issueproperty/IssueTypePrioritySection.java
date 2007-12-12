package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collections;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;

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
		issuePriorityTable.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				IssuePriority issuePriority = (IssuePriority)s.getFirstElement();
				IssueTypePriorityCreateWizard wizard = new IssueTypePriorityCreateWizard(issuePriority, false, null);
				try {
					DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
					dialog.open();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
				issuePriorityTable.refresh(true);
			}
		});
		
		getSection().setClient(client);
		
		IncreasePriorityAction increaseAction = new IncreasePriorityAction();
		DecreasePriorityAction decreaseAction = new DecreasePriorityAction();
		CreatePriorityAction createAction = new CreatePriorityAction();
		DeletePriorityAction deleteAction = new DeletePriorityAction();
		EditPriorityAction editAction = new EditPriorityAction();
		
		getToolBarManager().add(createAction);
		getToolBarManager().add(deleteAction);
		getToolBarManager().add(editAction);
		getToolBarManager().add(decreaseAction);
		getToolBarManager().add(increaseAction);
		
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
			if(issuePriorityTable.getFirstSelectedElement() != null){
				int index = controller.getIssueType().getIssuePriorities().indexOf(issuePriorityTable.getFirstSelectedElement());
				if(index < controller.getIssueType().getIssuePriorities().size() - 1){
					Collections.swap(controller.getIssueType().getIssuePriorities(), index, index + 1);
					issuePriorityTable.setInput(controller.getIssueType().getIssuePriorities());
					markDirty();
				}//if
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
			if(issuePriorityTable.getFirstSelectedElement() != null){
				int index = controller.getIssueType().getIssuePriorities().indexOf(issuePriorityTable.getFirstSelectedElement());
				if(index > 0){
					Collections.swap(controller.getIssueType().getIssuePriorities(), index, index - 1);
					issuePriorityTable.setInput(controller.getIssueType().getIssuePriorities());
					markDirty();
				}//if
			}//if
		}	
	}
	
	class CreatePriorityAction 
	extends Action 
	{		
		public CreatePriorityAction() {
			super();
			setId(IncreasePriorityAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypePrioritySection.class, 
					"Create"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
		}		
	}
	
	class DeletePriorityAction 
	extends Action 
	{		
		public DeletePriorityAction() {
			super();
			setId(IncreasePriorityAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypePrioritySection.class, 
					"Delete"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
		}		
	}
	
	class EditPriorityAction 
	extends Action 
	{		
		public EditPriorityAction() {
			super();
			setId(IncreasePriorityAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypePrioritySection.class, 
					"Edit"));
			setToolTipText("Tool Tip Text");
			setText("Text");
		}
		
		@Override
		public void run() {
		}		
	}
}
