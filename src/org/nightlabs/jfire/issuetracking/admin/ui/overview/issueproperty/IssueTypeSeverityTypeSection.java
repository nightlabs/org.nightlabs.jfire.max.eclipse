package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;

public class IssueTypeSeverityTypeSection extends ToolBarSectionPart {

	private IssueTypeEditorPageController controller;
	private IssueSeverityTypeTable issueSeverityTypeTable;
	
	private CreateSeverityTypeAction createAction;
	private DeleteSeverityTypeAction deleteAction;
	private EditSeverityTypeAction editAction;
	
	public IssueTypeSeverityTypeSection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Section Title");
		this.controller = controller;
		getSection().setText("Section Title");
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		issueSeverityTypeTable = new IssueSeverityTypeTable(client, SWT.NONE);
		issueSeverityTypeTable.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				IssueSeverityType issueSeverityType = (IssueSeverityType)s.getFirstElement();
				IssueTypeSeverityTypeEditWizard wizard = new IssueTypeSeverityTypeEditWizard(issueSeverityType, false, null);
				try {
					DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
					dialog.open();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
				issueSeverityTypeTable.refresh(true);
			}
		});
		
		getSection().setClient(client);
		
		createAction = new CreateSeverityTypeAction();
		deleteAction = new DeleteSeverityTypeAction();
		editAction = new EditSeverityTypeAction();
		
		getToolBarManager().add(createAction);
		getToolBarManager().add(deleteAction);
		getToolBarManager().add(editAction);
		
		hookContextMenu();
		
		updateToolBarManager();
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IssueTypeSeverityTypeSection.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(issueSeverityTypeTable);
		issueSeverityTypeTable.setMenu(menu);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(createAction);
		manager.add(editAction);
		manager.add(deleteAction);
	}
	
	public void setIssueType(IssueType issueType){
		issueSeverityTypeTable.setInput(issueType.getIssueSeverityTypes());
	}
	
	class CreateSeverityTypeAction 
	extends Action {		
		public CreateSeverityTypeAction() {
			super();
			setId(CreateSeverityTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeSeverityTypeSection.class, 
					"Create"));
			setToolTipText("Create/Choose a new severity type");
			setText("Create");
		}
		
		@Override
		public void run() {
			IssueTypeSeverityTypeSelectCreateWizard wizard = new IssueTypeSeverityTypeSelectCreateWizard(controller.getIssueType());
			try {
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
				int result = dialog.open();
				if(result == Dialog.OK) {
					controller.getIssueType().getIssueSeverityTypes().addAll(wizard.getSelectedIssueSeverities());
					issueSeverityTypeTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}		
	}
	
	class DeleteSeverityTypeAction 
	extends Action {		
		public DeleteSeverityTypeAction() {
			super();
			setId(CreateSeverityTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeSeverityTypeSection.class, 
					"Delete"));
			setToolTipText("Delete the selected severity type");
			setText("Delte");
		}
		
		@Override
		public void run() {
			boolean confirm = MessageDialog.openConfirm(getSection().getShell(), "Confirm Delete", "Delete this item(s)?");
			if(confirm) {
				controller.getIssueType().getIssueSeverityTypes().removeAll(issueSeverityTypeTable.getSelectedElements());
				issueSeverityTypeTable.refresh(true);
				markDirty();
			}
		}		
	}
	
	class EditSeverityTypeAction 
	extends Action {		
		public EditSeverityTypeAction() {
			super();
			setId(CreateSeverityTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeSeverityTypeSection.class, 
					"Edit"));
			setToolTipText("Edit the selected severity type");
			setText("Edit");
		}
		
		@Override
		public void run() {
			IssueSeverityType issueSeverityType = issueSeverityTypeTable.getFirstSelectedElement();
			IssueTypeSeverityTypeEditWizard wizard = new IssueTypeSeverityTypeEditWizard(issueSeverityType, false, null);
			try {
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
				if(dialog.open() == Dialog.OK) {
					issueSeverityTypeTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}		
	}
}
