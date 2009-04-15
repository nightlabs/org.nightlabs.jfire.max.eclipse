package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;
import org.nightlabs.progress.NullProgressMonitor;

public class UserCostSection extends ToolBarSectionPart {

	private XComposite client;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public UserCostSection(FormPage page, Composite parent) {
		super(
				page, parent, 
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				"User Cost");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		XComposite userComposite = new XComposite(client, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		userComposite.getGridLayout().numColumns = 2;
		
		final ListComposite<User> userList = new ListComposite<User>(userComposite, SWT.NONE);
		userList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof User) {
					User user = (User) element;
					return user.getName();
				}
				return "";
			}
		});
		
		Job job = new Job("Loading Users................") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					final java.util.List<User> users = UserDAO.sharedInstance().getUsers(
							Login.getLogin().getOrganisationID(),
							(String[]) null,
							new String[] {
								User.FETCH_GROUP_NAME
							},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new NullProgressMonitor()
					);
					
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							userList.setInput(users);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		userList.setLayoutData(gridData);
		
		XComposite detailComposite = new XComposite(userComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		detailComposite.getGridLayout().numColumns = 2;
		
		new Label(detailComposite, SWT.NONE).setText("Currency");
		CurrencyCombo currencyCombo2 = new CurrencyCombo(detailComposite, SWT.NONE);
		
		new Label(detailComposite, SWT.NONE).setText("Monthly Cost");
		Text costText2 = new Text(detailComposite, SWT.SINGLE);

		new Label(detailComposite, SWT.NONE).setText("Monthly Revenue");
		Text revenueText2 = new Text(detailComposite, SWT.SINGLE);
		
		getSection().setClient(client);
	}
	
	public XComposite getClient() {
		return client;
	}
}
