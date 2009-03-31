package org.nightlabs.jfire.issuetracking.ui.department;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.overview.CategoryFactory;
import org.nightlabs.jfire.base.ui.overview.CustomCompositeCategory;

/**
 * @author Chairat Kongarayawetchakun chairat[at] NightLabs [dot] de
 */
public class DepartmentCategory
extends CustomCompositeCategory {
	/**
	 * @param categoryFactory
	 */
	public DepartmentCategory(CategoryFactory categoryFactory) {
		super(categoryFactory);
	}
	
	@Override
	public Composite createComposite(Composite composite) {
		XComposite wrapper = new XComposite(composite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		DepartmentTableComposite departmentTableComposite = new DepartmentTableComposite(wrapper, SWT.None);
		departmentTableComposite.setHeaderVisible(false);
		return wrapper;
	}
}
