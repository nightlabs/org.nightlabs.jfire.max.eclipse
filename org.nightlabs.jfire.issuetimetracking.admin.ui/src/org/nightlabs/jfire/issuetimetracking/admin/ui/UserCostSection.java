package org.nightlabs.jfire.issuetimetracking.admin.ui;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;
import org.nightlabs.jfire.issuetimetracking.ProjectCostValue;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 */
public class UserCostSection
extends ToolBarSectionPart
{
	private XComposite client;
	private ListComposite<User> userList;
//	private Spinner costSpinner;
//	private Spinner revenueSpinner;
	private CostRevenueComposite costRevenueComposite;

	private User selectedUser;
//	private ProjectCostValue projectCostValue;
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

		//User List
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

		userList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedUser = userList.getSelectedElement();
				if (selectedUser == null){
					return;
				}

				ProjectCostValue projectCostValue = projectCost.getProjectCostValue(selectedUser.getUserID());
				if (projectCostValue == null)
					projectCostValue = createProjectCostValue(selectedUser);

				costRevenueComposite.setCurrency(projectCost.getCurrency());
				costRevenueComposite.setRevenue(projectCostValue.getRevenue().getAmount());
				costRevenueComposite.setCost(projectCostValue.getCost().getAmount());
				costRevenueComposite.setEnabled(true);

//				costSpinner.setEnabled(true);
//				revenueSpinner.setEnabled(true);
//
//				costSpinner.setSelection((int)(projectCostValue.getCost().getAmount()));
//				revenueSpinner.setSelection((int)(projectCostValue.getRevenue().getAmount()));
			}
		});

		gridData = new GridData(GridData.FILL_BOTH);
		userList.setLayoutData(gridData);

		XComposite c = new XComposite(userComposite, SWT.NONE);
		c.getGridLayout().numColumns = 2;

		//Cost
		costRevenueComposite = new CostRevenueComposite(c, SWT.NONE, false);
		costRevenueComposite.setEnabled(false);
//		Label monthlyCostLabel = new Label(c, SWT.NONE);
//		monthlyCostLabel.setText("Hourly Cost");
//		costSpinner = new Spinner(c, SWT.NONE);
//		costSpinner.setEnabled(false);
//		costSpinner.addModifyListener(modifyListener);
//		gridData = new GridData();
//		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
//		gridData.widthHint = 150;
//		gridData.verticalIndent = 5;
//		costSpinner.setLayoutData(gridData);
//		costSpinner.setMinimum(0);
//		costSpinner.setMaximum(Integer.MAX_VALUE);

		//Revenue
//		Label monthlyRevenueLabel = new Label(c, SWT.NONE);
//		monthlyRevenueLabel.setText("Hourly Revenue");
//		revenueSpinner = new Spinner(c, SWT.NONE);
//		revenueSpinner.setMinimum(0);
//		revenueSpinner.setMaximum(Integer.MAX_VALUE);
//		revenueSpinner.setEnabled(false);
//		revenueSpinner.addModifyListener(modifyListener);
//		gridData = new GridData();
//		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
//		gridData.widthHint = 150;
//		gridData.verticalIndent = 5;
//		revenueSpinner.setLayoutData(gridData);

		getSection().setClient(client);
	}

	public XComposite getClient() {
		return client;
	}

	private PriceFragmentType priceFragmentType;

//	private ModifyListener modifyListener = new ModifyListener() {
//		@Override
//		public void modifyText(ModifyEvent e) {
//			if (priceFragmentType == null)
//				priceFragmentType =
//					PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(PriceFragmentType.PRICE_FRAGMENT_TYPE_ID_TOTAL,
//							new String[] { FetchPlan.DEFAULT},
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//							new NullProgressMonitor());
//
//			if (selectedUser != null) {
//				ProjectCostValue projectCostValue = projectCost.getProjectCostValue(selectedUser.getUserID());
//				if (projectCostValue == null) {
//					projectCostValue = createProjectCostValue(selectedUser);
//				}
//
////				if (e.getSource() == costSpinner)
////					projectCostValue.getCost().setAmount(priceFragmentType, costSpinner.getSelection());
////				if (e.getSource() == revenueSpinner)
////					projectCostValue.getRevenue().setAmount(priceFragmentType, revenueSpinner.getSelection());
//
//				markDirty();
//			}
//		}
//	};

	protected void assertSWTThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");
	}


	private ProjectCost projectCost;
	public void setProjectCost(final ProjectCost projectCost) {
		assertSWTThread();

		this.projectCost = projectCost;
		userList.setInput(projectCost.getProject().getMembers());
	}

	@Override
	public void refresh() {
		super.refresh();

		if (projectCost != null)
			costRevenueComposite.setCurrency(projectCost.getCurrency());
	}

	public void setCurrency(Currency currency) {
//		costSpinner.setDigits(currency.getDecimalDigitCount());
//		revenueSpinner.setDigits(currency.getDecimalDigitCount());
	}

	private ProjectCostValue createProjectCostValue(User user) {
		ProjectCostValue projectCostValue = new ProjectCostValue(selectedUser, projectCost, IDGenerator.nextID(ProjectCostValue.class));

		if (priceFragmentType == null)
			priceFragmentType =
				PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(PriceFragmentType.PRICE_FRAGMENT_TYPE_ID_TOTAL,
						new String[] { FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor());
		projectCostValue.getCost().setAmount(priceFragmentType, projectCost.getDefaultCost().getAmount());
		projectCostValue.getRevenue().setAmount(priceFragmentType, projectCost.getDefaultRevenue().getAmount());
		projectCost.addProjectCostValue(projectCostValue);

		return projectCostValue;
	}
}