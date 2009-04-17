package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.security.User;

public class UserCostSection extends ToolBarSectionPart {

	private XComposite client;
	private ListComposite<User> userList;
	
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
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());
		
		client = new XComposite(getSection(), SWT.NONE);
		client.getGridLayout().numColumns = 1; 
		GridData gridData = new GridData(GridData.FILL_BOTH);
		client.setLayoutData(gridData);
		
		
		XComposite userComposite = new XComposite(client, SWT.NONE);
		userComposite.getGridLayout().numColumns = 2;
		gridData = new GridData(GridData.FILL_BOTH);
		userComposite.setLayoutData(gridData);
		
		userList = new ListComposite<User>(userComposite, SWT.NONE);
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
		
		gridData = new GridData(GridData.FILL_BOTH);
		userList.setLayoutData(gridData);

		XComposite c = new XComposite(userComposite, SWT.NONE);
		c.getGridLayout().numColumns = 2;
		
		Label monthlyCostLabel = new Label(c, SWT.NONE);
		monthlyCostLabel.setText("Monthly Cost");
		Text costText = new Text(c, SWT.SINGLE);
		costText.setTextLimit(20);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		costText.setLayoutData(gridData);

		Label monthlyRevenueLabel = new Label(c, SWT.NONE);
		monthlyRevenueLabel.setText("Monthly Revenue");
		Text revenueText = new Text(c, SWT.SINGLE);
		revenueText.setTextLimit(20);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		revenueText.setLayoutData(gridData);
		
		getSection().setClient(client);
	}
	
	public XComposite getClient() {
		return client;
	}
	
	public void setProject(Project project) {
		userList.setInput(project.getMembers());
	}
}
