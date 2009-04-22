package org.nightlabs.jfire.issuetimetracking.admin.ui;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorPageController;
import org.nightlabs.progress.NullProgressMonitor;

/** 
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 */
public class ProjectCostSection 
extends ToolBarSectionPart 
{
	private CostRevenueComposite costRevenueComposite;
	private ProjectEditorPageController controller;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ProjectCostSection(FormPage page, Composite parent, ProjectEditorPageController controller) {
		super(
				page, parent, 
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				"Project Cost");
		this.controller = controller;
		
		XComposite costComposite = new XComposite(getContainer(), SWT.NONE);
		costComposite.setLayout(new GridLayout(2, false));
		
		costRevenueComposite = new CostRevenueComposite(costComposite, SWT.NONE);
		costRevenueComposite.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (priceFragmentType == null) {
					priceFragmentType =  
						PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(PriceFragmentType.PRICE_FRAGMENT_TYPE_ID_TOTAL,
								new String[] { FetchPlan.DEFAULT}, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
								new NullProgressMonitor());
				}
				
				projectCost.getDefaultCost().setAmount(priceFragmentType, costRevenueComposite.getCost());
				projectCost.getDefaultRevenue().setAmount(priceFragmentType, costRevenueComposite.getRevenue());
				
				markDirty();
			}
		});
	}
	
	private PriceFragmentType priceFragmentType;
	private ProjectCost projectCost;
	public void setProjectCost(ProjectCost projectCost) {
		this.projectCost = projectCost;
		costRevenueComposite.setProjectCost(projectCost);
	}
	
	public Currency getCurrency() {
		return costRevenueComposite.getSelectedCurrency();
	}
}