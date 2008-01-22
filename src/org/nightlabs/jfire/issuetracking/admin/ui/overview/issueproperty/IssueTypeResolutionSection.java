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
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;

public class IssueTypeResolutionSection extends ToolBarSectionPart {

	private IssueTypeEditorPageController controller;
	private IssueResolutionTable issueResolutionTable;
	
	private CreateResolutionAction createAction;
	private DeleteResolutionAction deleteAction;
	private EditResolutionAction editAction;
	private IncreaseResolutionAction increaseResolutionAction;
	private DecreaseResolutionAction decreaseResolutionAction;
	
	public IssueTypeResolutionSection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Section Title");
		this.controller = controller;
		getSection().setText("Resolutions");
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		issueResolutionTable = new IssueResolutionTable(client, SWT.NONE);
		issueResolutionTable.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				IssueResolution issueResolution = (IssueResolution)s.getFirstElement();
				IssueTypeResolutionEditWizard wizard = new IssueTypeResolutionEditWizard(issueResolution, false, null);
				try {
					DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
					dialog.open();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
				issueResolutionTable.refresh(true);
			}
		});
		
		getSection().setClient(client);
		
		createAction = new CreateResolutionAction();
		deleteAction = new DeleteResolutionAction();
		editAction = new EditResolutionAction();
		increaseResolutionAction = new IncreaseResolutionAction();
		decreaseResolutionAction = new DecreaseResolutionAction();
		
		
		getToolBarManager().add(createAction);
		getToolBarManager().add(deleteAction);
		getToolBarManager().add(editAction);
		getToolBarManager().add(decreaseResolutionAction);
		getToolBarManager().add(increaseResolutionAction);
		
		hookContextMenu();
		
		updateToolBarManager();
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IssueTypeResolutionSection.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(issueResolutionTable);
		issueResolutionTable.setMenu(menu);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(createAction);
		manager.add(editAction);
		manager.add(deleteAction);
	}
	
	public void setIssueType(IssueType issueType){
		issueResolutionTable.setInput(issueType.getIssueResolutions());
	}
	
	class IncreaseResolutionAction 
	extends Action 
	{		
		public IncreaseResolutionAction() {
			super();
			setId(IncreaseResolutionAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeResolutionSection.class, 
					"Down"));
			setToolTipText("Increase");
			setText("Increase Resolution");
		}
		
		@Override
		public void run() {
			if(issueResolutionTable.getFirstSelectedElement() != null){
				int index = controller.getIssueType().getIssueResolutions().indexOf(issueResolutionTable.getFirstSelectedElement());
				if(index < controller.getIssueType().getIssueResolutions().size() - 1){
					Collections.swap(controller.getIssueType().getIssueResolutions(), index, index + 1);
					issueResolutionTable.setInput(controller.getIssueType().getIssueResolutions());
					markDirty();
				}//if
			}//if
		}		
	}
	
	class DecreaseResolutionAction
	extends Action
	{
		public DecreaseResolutionAction() {
			super();
			setId(DecreaseResolutionAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeResolutionSection.class, 
					"Up"));
			setToolTipText("Decrease");
			setText("Decrease Resolution");
		}
		
		@Override
		public void run() {
			if(issueResolutionTable.getFirstSelectedElement() != null){
				int index = controller.getIssueType().getIssueResolutions().indexOf(issueResolutionTable.getFirstSelectedElement());
				if(index > 0){
					Collections.swap(controller.getIssueType().getIssueResolutions(), index, index - 1);
					issueResolutionTable.setInput(controller.getIssueType().getIssueResolutions());
					markDirty();
				}//if
			}//if
		}	
	}
	
	class CreateResolutionAction 
	extends Action {		
		public CreateResolutionAction() {
			super();
			setId(CreateResolutionAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeResolutionSection.class, 
					"Create"));
			setToolTipText("Create/Choose a new resolution");
			setText("Create");
		}
		
		@Override
		public void run() {
			IssueTypeResolutionSelectCreateWizard wizard = new IssueTypeResolutionSelectCreateWizard(controller.getIssueType());
			try {
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
				int result = dialog.open();
				if(result == Dialog.OK) {
					controller.getIssueType().getIssueResolutions().addAll(wizard.getSelectedIssueResolutions());
					issueResolutionTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}		
	}
	
	class DeleteResolutionAction 
	extends Action {		
		public DeleteResolutionAction() {
			super();
			setId(CreateResolutionAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeResolutionSection.class, 
					"Delete"));
			setToolTipText("Delete the selected resolution");
			setText("Delete");
		}
		
		@Override
		public void run() {
			boolean confirm = MessageDialog.openConfirm(getSection().getShell(), "Confirm Delete", "Delete this item(s)?");
			if(confirm) {
				controller.getIssueType().getIssueSeverityTypes().removeAll(issueResolutionTable.getSelectedElements());
				issueResolutionTable.refresh(true);
				markDirty();
			}
		}		
	}
	
	class EditResolutionAction 
	extends Action {		
		public EditResolutionAction() {
			super();
			setId(CreateResolutionAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					IssueTypeResolutionSection.class, 
					"Edit"));
			setToolTipText("Edit the selected resolution");
			setText("Edit");
		}
		
		@Override
		public void run() {
			IssueResolution issueResolution = issueResolutionTable.getFirstSelectedElement();
			IssueTypeResolutionEditWizard wizard = new IssueTypeResolutionEditWizard(issueResolution, false, null);
			try {
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
				if(dialog.open() == Dialog.OK) {
					issueResolutionTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}		
	}
}
