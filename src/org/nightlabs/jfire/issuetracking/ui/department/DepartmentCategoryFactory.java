package org.nightlabs.jfire.issuetracking.ui.department;

import org.nightlabs.jfire.base.ui.overview.Category;
import org.nightlabs.jfire.base.ui.overview.DefaultCategoryFactory;

/**
 * @author Chairat Kongarayawetchakun chairat[at] NightLabs [dot] de
 */
public class DepartmentCategoryFactory
extends DefaultCategoryFactory
{
	/**
	 * 
	 */
	public DepartmentCategoryFactory() {}
	
	@Override
	public Category createCategory() {
		return new DepartmentCategory(this);
	}
}
