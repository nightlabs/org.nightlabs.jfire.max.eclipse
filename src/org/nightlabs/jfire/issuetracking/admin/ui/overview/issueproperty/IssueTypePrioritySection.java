package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
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
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypePrioritySection 
extends ToolBarSectionPart 
{
	private IssueTypeEditorPageController controller;
	private IssuePriorityTable issuePriorityTable;
	
	private IncreasePriorityAction increaseAction;
	private DecreasePriorityAction decreaseAction;
	private CreatePriorityAction createAction;
	private DeletePriorityAction deleteAction;
	private EditPriorityAction editAction;
	
	public IssueTypePrioritySection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.section.text")); //$NON-NLS-1$
		this.controller = controller;
		
		XComposite client = new XComposite(getContainer(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		client.getGridLayout().numColumns = 1; 

		issuePriorityTable = new IssuePriorityTable(client, SWT.NONE);
		issuePriorityTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				IssuePriority issuePriority = (IssuePriority)s.getFirstElement();
				IssueTypePriorityEditWizard wizard = new IssueTypePriorityEditWizard(issuePriority, false, null);
				try {
					DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
					dialog.open();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
				issuePriorityTable.refresh(true);
			}
		});
		
//		getSection().setClient(client);
		
		increaseAction = new IncreasePriorityAction();
		decreaseAction = new DecreasePriorityAction();
		createAction = new CreatePriorityAction();
		deleteAction = new DeletePriorityAction();
		editAction = new EditPriorityAction();
		
		getToolBarManager().add(createAction);
		getToolBarManager().add(deleteAction);
		getToolBarManager().add(editAction);
		getToolBarManager().add(decreaseAction);
		getToolBarManager().add(increaseAction);
		
		hookContextMenu();
		
		updateToolBarManager();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IssueTypePrioritySection.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(issuePriorityTable);
		issuePriorityTable.setMenu(menu);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(decreaseAction);
		manager.add(increaseAction);
		manager.add(new Separator());
		manager.add(createAction);
		manager.add(editAction);
		manager.add(deleteAction);
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
					"Down")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.IncreasePriorityAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.IncreasePriorityAction.text")); //$NON-NLS-1$
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
					"Up")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.DecreasePriorityAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.DecreasePriorityAction.text")); //$NON-NLS-1$
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
					"Create")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.CreatePriorityAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.CreatePriorityAction.text")); //$NON-NLS-1$
		}
		
		@Override
		public void run() {
			IssueTypePrioritySelectCreateWizard wizard = new IssueTypePrioritySelectCreateWizard(controller.getIssueType());
			try {
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
				int result = dialog.open();
				if(result == Dialog.OK) {
					controller.getIssueType().getIssuePriorities().addAll(wizard.getSelectedIssuePriorities());
					issuePriorityTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
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
					"Delete")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.DeletePriorityAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.DeletePriorityAction.text")); //$NON-NLS-1$
		}
		
		@Override
		public void run() {
			boolean confirm = MessageDialog.openConfirm(getSection().getShell(), Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.DeletePriorityAction.confirmDialog.title"), Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.DeletePriorityAction.confirmDialog.text")); //$NON-NLS-1$ //$NON-NLS-2$
			if(confirm) {
				controller.getIssueType().getIssuePriorities().removeAll(issuePriorityTable.getSelectedElements());
				issuePriorityTable.refresh(true);
				markDirty();
			}
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
					"Edit")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.EditPriorityAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySection.EditPriorityAction.text")); //$NON-NLS-1$
		}
		
		@Override
		public void run() {
			IssuePriority issuePriority = issuePriorityTable.getFirstSelectedElement();
			IssueTypePriorityEditWizard wizard = new IssueTypePriorityEditWizard(issuePriority, false, null);
			try {
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
				if(dialog.open() == Dialog.OK) {
					issuePriorityTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}		
	}
}
