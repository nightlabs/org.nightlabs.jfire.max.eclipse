package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.config.dao.ConfigModuleDAO;
import org.nightlabs.jfire.issue.config.IssueQueryConfigModule;
import org.nightlabs.jfire.issue.config.StoredIssueQuery;
import org.nightlabs.jfire.issue.dao.StoredIssueQueryDAO;
import org.nightlabs.jfire.issue.id.StoredIssueQueryID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.progress.NullProgressMonitor;

public class StoredIssueQuerySection 
extends ToolBarSectionPart 
{
	private XComposite client;
	private StoredIssueQueryTable storedIssueQueryTable;
	
	public StoredIssueQuerySection(FormToolkit toolkit, Composite parent) {
		super(toolkit, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, "Stored Filters");
		
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());
		
		client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		storedIssueQueryTable = new StoredIssueQueryTable(client, SWT.NONE);
		
		getSection().setClient(client);
		
		getToolBarManager().add(new EditStoredIssueQueryAction());
		getToolBarManager().add(new DeleteStoredIssueQueryAction());
		
		updateToolBarManager();
	} 

	class EditStoredIssueQueryAction 
	extends Action 
	{
		public EditStoredIssueQueryAction() {
			super();
			setId(EditStoredIssueQueryAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					StoredIssueQuerySection.class, 
			"Edit"));
			setToolTipText("Edit Stored Issue Query");
			setText("Edit");
		}

		@Override
		public void run() {
			IssueQueryRenameDialog dialog = new IssueQueryRenameDialog(RCPUtil.getActiveShell());
			
			if (storedIssueQueryTable.getTable().getSelectionIndex() != -1) {
				StoredIssueQuery selectedIssueQuery = storedIssueQueryTable.getFirstSelectedElement();
				dialog.setNameString(selectedIssueQuery.getName());

				if (selectedIssueQuery != null) {
					if (dialog.open() == Dialog.OK) {
						try {
							IssueQueryConfigModule cfMod = (IssueQueryConfigModule)ConfigUtil.getUserCfMod(
									IssueQueryConfigModule.class,
									new String[] {FetchPlan.DEFAULT, IssueQueryConfigModule.FETCH_GROUP_STOREDISSUEQUERRYLIST},
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
									new NullProgressMonitor()
							);

							StoredIssueQuery query = storedIssueQueryTable.getFirstSelectedElement();
							query.setName(dialog.getNameText());

//							cfMod.addStoredIssueQuery(query);

							ConfigModuleDAO.sharedInstance().storeConfigModule(cfMod, 
									false, 
									new String[]{StoredIssueQuery.FETCH_GROUP_STOREDISSUEQUERY}, 
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
									new NullProgressMonitor());
						}
						catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
	}
	
	class DeleteStoredIssueQueryAction 
	extends Action 
	{
		public DeleteStoredIssueQueryAction() {
			super();
			setId(EditStoredIssueQueryAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					StoredIssueQuerySection.class, 
			"Delete"));
			setToolTipText("Delete Stored Issue Query");
			setText("Delete");
		}

		@Override
		public void run() {
			IssueQueryConfigModule cfMod = (IssueQueryConfigModule)ConfigUtil.getUserCfMod(
					IssueQueryConfigModule.class,
					new String[] {FetchPlan.DEFAULT, IssueQueryConfigModule.FETCH_GROUP_STOREDISSUEQUERRYLIST},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor()
			);

			Collection<StoredIssueQuery> queries = storedIssueQueryTable.getSelectedElements();
			for (StoredIssueQuery query : queries) {
				cfMod.removeStoredIssueQuery(query);
			}
			
			ConfigModuleDAO.sharedInstance().storeConfigModule(cfMod, 
					false, 
					new String[]{StoredIssueQuery.FETCH_GROUP_STOREDISSUEQUERY}, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					new NullProgressMonitor());
			
			for (StoredIssueQuery query : queries) {
				StoredIssueQueryDAO.sharedInstance().deleteStoredIssueQuery((StoredIssueQueryID)JDOHelper.getObjectId(query), new NullProgressMonitor());
			}
		}
	}
	
	class IssueQueryRenameDialog extends CenteredDialog 
	{
		private Text nameText;
		private Label errorLabel;

		public IssueQueryRenameDialog(Shell parentShell) {
			super(parentShell);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA);
			Label label = new Label(wrapper, SWT.BOLD);
			label.setText("Please enter the name for the filter.");
			GridData gd = new GridData();
			gd.heightHint = 40;
			label.setLayoutData(gd);
			
			new Label(wrapper, SWT.NONE).setText("Name");
			nameText = new Text(wrapper, SWT.BORDER);
			nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameText.setText(nameString);
			
			return wrapper;
		}
		
		private String nameString;
		public String getNameText() {
			return nameString;
		}
		
		public void setNameString(String nameString) {
			this.nameString = nameString;
		}
		
		@Override
		protected void okPressed() {
			this.nameString = nameText.getText();
			super.okPressed();
		}
		
		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Filter's Name");
			newShell.setSize(400, 300);
		}
	}
}