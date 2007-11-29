/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview;

import org.nightlabs.jfire.base.ui.overview.Category;
import org.nightlabs.jfire.base.ui.overview.DefaultCategoryFactory;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueOverviewCategoryFactory extends DefaultCategoryFactory {

	/**
	 * 
	 */
	public IssueOverviewCategoryFactory() {
	}
	
	@Override
	public Category createCategory() {
		return new IssueOverviewCategory(this);
	}
}
