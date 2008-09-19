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
public class IssueTypeCategoryFactory
extends DefaultCategoryFactory {

	/**
	 * 
	 */
	public IssueTypeCategoryFactory() {
	}
	
	@Override
	public Category createCategory() {
		return new IssueTypeCategory(this);
	}
}
