package org.nightlabs.jfire.issuetracking.ui.overview.action;

import javax.jdo.FetchPlan;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.config.dao.ConfigModuleDAO;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.config.IssueQueryConfigModule;
import org.nightlabs.jfire.issue.config.StoredIssueQuery;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListEditor;
import org.nightlabs.progress.NullProgressMonitor;


public class IssueQuerySaveAction 
extends AbstractIssueAction
{
	private IssueQuerySaveDialog dialog;
	
	@Override
	public boolean calculateEnabled() {
		return true;
	}

	@Override
	public boolean calculateVisible() {
		return true;
	}

	@Override
	public void run() {
		IWorkbenchPart part = getActivePart();
		if (part instanceof IssueEntryListEditor) {
			IssueEntryListEditor editor = (IssueEntryListEditor) part;
			// this is NOT safe!
			JDOQuerySearchEntryViewer<Issue, IssueQuery> viewer =
				(JDOQuerySearchEntryViewer<Issue, IssueQuery>)editor.getEntryViewer();
			QueryCollection<Issue, ? extends IssueQuery> queries = viewer.getManagedQueries();
			queries = new QueryCollection<Issue, IssueQuery>(queries);
//			List<? extends AbstractJDOQuery> queries = viewer.getFilterComposite().getJDOQueries();
			if (! queries.isEmpty())
			{
				dialog = new IssueQuerySaveDialog(RCPUtil.getActiveShell());
				if (dialog.open() == Window.OK) {
					try {
//						Collection<AbstractJDOQuery> queryCollection = new HashSet<AbstractJDOQuery>();
//						for (AbstractJDOQuery query : queries) {
//							queryCollection.add(query);
//						}
						
						StoredIssueQuery storedIssueQuery = new StoredIssueQuery(Login.sharedInstance().getOrganisationID(), 
								IDGenerator.nextID(StoredIssueQuery.class));
						storedIssueQuery.setName(dialog.getNameText());
						
						storedIssueQuery.setIssueQueries(queries);
						
						IssueQueryConfigModule cfMod = (IssueQueryConfigModule)ConfigUtil.getUserCfMod(
								IssueQueryConfigModule.class,
						        new String[] {FetchPlan.DEFAULT, IssueQueryConfigModule.FETCH_GROUP_STOREDISSUEQUERRYLIST},
						        NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						        new NullProgressMonitor()
						 );
						
						cfMod.addStoredIssueQuery(storedIssueQuery);
						
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
	
	class IssueQuerySaveDialog extends CenteredDialog 
	{
		private Text nameText;
		private Label errorLabel;

		public IssueQuerySaveDialog(Shell parentShell) {
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
			
			return wrapper;
		}
		
		private String nameString;
		public String getNameText() {
			return nameString;
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
