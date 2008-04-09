package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.querystore.SaveQueryCollectionAction.QueryStoreEditDialog;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
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
//	FIXME: If I delete something from the Database this weird foreign key exception occurs: Caused by: java.sql.BatchUpdateException: Cannot delete or update a parent row: a foreign key constraint fails (`JFire_chezfrancois_jfire_org/JFIREQUERYSTORE_BASEQUERYSTORE`, CONSTRAINT `JFIREQUERYSTORE_BASEQUERYSTORE_FK3` FOREIGN KEY (`NAME_ORGANISATION_ID_OID`, `NAME_QUERY_STORE_ID_OID`) REFERENCES `J)
//				 When this is cleared up just uncomment the following line to enabled the deletion of QueryStores. (marius)
//		getToolBarManager().add(new DeleteStoredIssueQueryAction());
		
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
		public void run() 
		{
			if (storedIssueQueryTable == null || storedIssueQueryTable.isDisposed())
				return;
			
			BaseQueryStore<?, ?> store = storedIssueQueryTable.getFirstSelectedElement();
			if (store == null)
				return;
			
			QueryStoreEditDialog dialog = new QueryStoreEditDialog(getSection().getShell(), store);
			
			if (dialog.open() != Window.OK)	return;
			
			Collection<BaseQueryStore<?, ?>> input = 
				(Collection<BaseQueryStore<?, ?>>) storedIssueQueryTable.getTableViewer().getInput();
			
			input.remove(store);
			
			store = QueryStoreDAO.sharedInstance().storeQueryStore(store, 
				StoredIssueQueryTable.FETCHGROUPS_BASEQUERYSTORE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				true, new NullProgressMonitor()
			);
			
			input.add(store);
			storedIssueQueryTable.setInput(input);
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
		public void run()
		{
			if (storedIssueQueryTable == null || storedIssueQueryTable.isDisposed())
				return;
			
			BaseQueryStore<?, ?> store = storedIssueQueryTable.getFirstSelectedElement();
			if (store == null)
				return;

			boolean removed = QueryStoreDAO.sharedInstance().removeQueryStore(store, new NullProgressMonitor());
			
			if (removed)
			{
				Collection<BaseQueryStore<?, ?>> input = 
					(Collection<BaseQueryStore<?, ?>>) storedIssueQueryTable.getTableViewer().getInput();
				
				input.remove(store);
				storedIssueQueryTable.setInput(input);
			}
		}
	}

}