package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;

public class ProjectCostSection extends ToolBarSectionPart {

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
		
		CostRevenueComposite costRevenueComposite = new CostRevenueComposite(costComposite, SWT.NONE);
	}
	
}
