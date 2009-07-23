package org.nightlabs.jfire.issuetimetracking.admin.ui;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jdo.NLJDOHelper;
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
	private Spinner costSpinner;
	private Spinner revenueSpinner;

	private User selectedUser;
	private ProjectCostEditorPageController controller;
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public UserCostSection(FormPage page, Composite parent, final ProjectCostEditorPageController controller) {
		super(
				page, parent, 
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
		"User Cost");
		this.controller = controller;
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

				ProjectCostValue projectCostValue = controller.getControllerObject().getProjectCostValue(selectedUser.getUserID());
				if (projectCostValue == null) 
					projectCostValue = createProjectCostValue(selectedUser);
				
				costSpinner.setSelection((int)(projectCostValue.getCost().getAmount()));
				revenueSpinner.setSelection((int)(projectCostValue.getRevenue().getAmount()));
			}
		});

		gridData = new GridData(GridData.FILL_BOTH);
		userList.setLayoutData(gridData);

		XComposite c = new XComposite(userComposite, SWT.NONE);
		c.getGridLayout().numColumns = 2;

		//Cost
		Label monthlyCostLabel = new Label(c, SWT.NONE);
		monthlyCostLabel.setText("Hourly Cost");
		costSpinner = new Spinner(c, SWT.NONE);
		costSpinner.setEnabled(false);
		costSpinner.addListener (SWT.Verify, new Listener () {
			public void handleEvent (Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i=0; i<chars.length; i++) {
					if (!('0' <= chars [i] && chars [i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		costSpinner.addModifyListener(modifyListener);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		costSpinner.setLayoutData(gridData);
		costSpinner.setMinimum(0);
		costSpinner.setMaximum(Integer.MAX_VALUE);
		
		//Revenue
		Label monthlyRevenueLabel = new Label(c, SWT.NONE);
		monthlyRevenueLabel.setText("Hourly Revenue");
		revenueSpinner = new Spinner(c, SWT.NONE);
		revenueSpinner.setMinimum(0);
		revenueSpinner.setMaximum(Integer.MAX_VALUE);
		
		revenueSpinner.setEnabled(false);
		revenueSpinner.addListener (SWT.Verify, new Listener () {
			public void handleEvent (Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i=0; i<chars.length; i++) {
					if (!('0' <= chars [i] && chars [i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
		
		revenueSpinner.addModifyListener(modifyListener);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		revenueSpinner.setLayoutData(gridData);

		getSection().setClient(client);
	}

	public XComposite getClient() {
		return client;
	}

	private PriceFragmentType priceFragmentType;

	private ModifyListener modifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if (priceFragmentType == null)
				priceFragmentType =  
					PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(PriceFragmentType.PRICE_FRAGMENT_TYPE_ID_TOTAL,
							new String[] { FetchPlan.DEFAULT}, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
							new NullProgressMonitor());

			ProjectCostValue projectCostValue = controller.getControllerObject().getProjectCostValue(selectedUser.getUserID());
			if (projectCostValue == null) {
				projectCostValue = createProjectCostValue(selectedUser);
			}

			if (e.getSource() == costSpinner)
				projectCostValue.getCost().setAmount(priceFragmentType, costSpinner.getSelection());
			if (e.getSource() == revenueSpinner)
				projectCostValue.getRevenue().setAmount(priceFragmentType, revenueSpinner.getSelection());

			markDirty();

		}
	};

	public void setProjectCost(final ProjectCost projectCost) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				userList.setInput(projectCost.getProject().getMembers());		
				if (!projectCost.getProject().getMembers().isEmpty()) {
					userList.selectElementByIndex(0);
					selectedUser = userList.getSelectedElement();
					costSpinner.setEnabled(true);
					revenueSpinner.setEnabled(true);
					ProjectCostValue projectCostValue = projectCost.getProjectCostValue(selectedUser.getUserID());
					
					if (projectCostValue == null)
						projectCostValue = createProjectCostValue(selectedUser);

					costSpinner.setSelection((int)(projectCostValue.getCost().getAmount()));
					revenueSpinner.setSelection((int)(projectCostValue.getRevenue().getAmount()));
				}
			}
		});
	}

	private ProjectCostValue createProjectCostValue(User user) {
		ProjectCostValue projectCostValue = new ProjectCostValue(selectedUser, controller.getControllerObject(), IDGenerator.nextID(ProjectCostValue.class));

		if (priceFragmentType == null)
			priceFragmentType =  
				PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(PriceFragmentType.PRICE_FRAGMENT_TYPE_ID_TOTAL,
						new String[] { FetchPlan.DEFAULT}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor());
		projectCostValue.getCost().setAmount(priceFragmentType, controller.getControllerObject().getDefaultCost().getAmount());
		projectCostValue.getRevenue().setAmount(priceFragmentType, controller.getControllerObject().getDefaultRevenue().getAmount());
		controller.getControllerObject().addProjectCostValue(projectCostValue);
		
		return projectCostValue;
	}
}