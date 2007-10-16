package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.graphics.Image;

/**
 * @author Chairat Kongarayawetchakun chairatk [at] NightLabs [dot] de
 *
 */
public interface IssueCategoryFactory
extends IExecutableExtension
{
	/**
	 * returns the name of the category
	 * @return the name of the category
	 */
	String getName();
	
	/**
	 * returns the optional image of the category, may be null
	 * @return the optional image of the category
	 */
	Image getImage();
	
	/**
	 * returns the index of the category
	 * @return the index of the category
	 */
	int getIndex();

	IssueCategory createIssueCategory();
}
