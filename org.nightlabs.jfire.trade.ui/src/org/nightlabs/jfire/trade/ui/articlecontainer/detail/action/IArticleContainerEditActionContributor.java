/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IArticleContainerEditActionContributor {

	Menu createArticleContainerContextMenu(Control parent);
	Menu createArticleEditContextMenu(Control parent);
}
