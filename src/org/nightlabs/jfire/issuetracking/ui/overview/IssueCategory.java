package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.widgets.Composite;

public interface IssueCategory
{
	IssueCategoryFactory getIssueCategoryFactory();

	/**
	 * returns the Composite which will be displayed
	 * @return the Composite which will be displayed
	 */
	Composite createComposite(Composite parent);

	/**
	 * @return the <code>Composite</code> that has been created by {@link #createComposite(Composite)} or <code>null</code>, if {@link #createComposite(Composite)} was
	 *		not yet called. 
	 */
	Composite getComposite();
}
