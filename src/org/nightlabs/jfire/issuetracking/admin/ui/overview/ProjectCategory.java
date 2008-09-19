package org.nightlabs.jfire.issuetracking.admin.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.overview.CategoryFactory;
import org.nightlabs.jfire.base.ui.overview.CustomCompositeCategory;
import org.nightlabs.jfire.issuetracking.ui.issue.ProjectTreeComposite;

/**
 * @author Chairat Kongarayawetchakun chairat[at] NightLabs [dot] de
 */
public class ProjectCategory
extends CustomCompositeCategory {
	/**
	 * @param categoryFactory
	 */
	public ProjectCategory(CategoryFactory categoryFactory) {
		super(categoryFactory);
	}
	
	@Override
	public Composite createComposite(Composite composite) {
		XComposite wrapper = new XComposite(composite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		ProjectTreeComposite pc = new ProjectTreeComposite(wrapper);
		return pc;
	}
}
