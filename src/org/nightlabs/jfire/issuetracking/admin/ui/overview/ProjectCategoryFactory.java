package org.nightlabs.jfire.issuetracking.admin.ui.overview;

import org.nightlabs.jfire.base.ui.overview.Category;
import org.nightlabs.jfire.base.ui.overview.DefaultCategoryFactory;

/**
 * @author Chairat Kongarayawetchakun chairat[at] NightLabs [dot] de
 */
public class ProjectCategoryFactory
extends DefaultCategoryFactory
{
	/**
	 * 
	 */
	public ProjectCategoryFactory() {}
	
	@Override
	public Category createCategory() {
		return new ProjectCategory(this);
	}
}
