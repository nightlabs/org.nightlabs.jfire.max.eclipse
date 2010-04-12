package org.nightlabs.jfire.trade.ui.articlecontainer.detail.info;

import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Implementations of this interface can provide text and image informations for a specific {@link ArticleContainerID}.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface ArticleContainerInfoDelegate
{
	/**
	 * Return a text for the {@link ArticleContainer} with the given {@link ArticleContainerID}.
	 *
	 * @param articleContainerID the {@link ArticleContainerID} for the {@link ArticleContainer} to return a text
	 * @param monitor the {@link ProgressMonitor} to display the progress if this method is a long running operation
	 * @return the text for the {@link ArticleContainer} with the given {@link ArticleContainerID}
	 */
	String getText(ArticleContainerID articleContainerID, ProgressMonitor monitor);

	/**
	 * Returns a {@link ImageDescriptor} for the {@link ArticleContainer} with the given {@link ArticleContainerID}.
	 *
	 * @param articleContainerID the {@link ArticleContainerID} for the {@link ArticleContainer} to return a text
	 * @param monitor the {@link ProgressMonitor} to display the progress if this method is a long running operation
	 * @return the {@link ImageDescriptor} for the {@link ArticleContainer} with the given {@link ArticleContainerID}
	 */
	ImageDescriptor getImageDescriptor(ArticleContainerID articleContainerID, ProgressMonitor monitor);
}
