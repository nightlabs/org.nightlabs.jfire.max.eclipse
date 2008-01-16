/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.overview;

import org.nightlabs.jfire.base.ui.overview.Category;
import org.nightlabs.jfire.base.ui.overview.DefaultCategoryFactory;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueDefaultCategoryFactory extends DefaultCategoryFactory {

	/**
	 * 
	 */
	public IssueDefaultCategoryFactory() {
	}
	
	@Override
	public Category createCategory() {
		return new IssueDefaultCategory(this);
	}
}
