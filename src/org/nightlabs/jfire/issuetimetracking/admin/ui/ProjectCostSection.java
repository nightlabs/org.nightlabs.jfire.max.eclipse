package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;

public class ProjectCostSection extends ToolBarSectionPart {

	private CostRevenueComposite costRevenueComposite;
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ProjectCostSection(FormPage page, Composite parent) {
		super(
				page, parent, 
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				"Project Cost");
		
		XComposite costComposite = new XComposite(getContainer(), SWT.NONE);
		costComposite.setLayout(new GridLayout(2, false));
		
		costRevenueComposite = new CostRevenueComposite(costComposite, SWT.NONE);
	}
	
	public void setProject(Project project) {
		
	}
	
	public void setProjectCost(ProjectCost projectCost) {
		costRevenueComposite.setProjectCost(projectCost);
	}
	
	public Currency getCurrency() {
		return costRevenueComposite.getSelectedCurrency();
	}
}
