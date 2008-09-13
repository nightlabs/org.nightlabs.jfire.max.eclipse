package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.ui.IEditorPart;
import org.nightlabs.jfire.base.ui.login.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.trade.ArticleContainer;

/**
 * Common interface that all {@link IEditorPart}s that edit an {@link ArticleContainer} should implement. 
 *  
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IArticleContainerEditor
extends ICloseOnLogoutEditorPart
{
	/**
	 * @return The {@link ArticleContainerEdit} for this editor.
	 */
	ArticleContainerEdit getArticleContainerEdit();
}
