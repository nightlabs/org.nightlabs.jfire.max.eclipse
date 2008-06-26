package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.nightlabs.jfire.base.ui.login.part.ICloseOnLogoutEditorPart;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface IArticleContainerEditor
extends ICloseOnLogoutEditorPart
{
	ArticleContainerEditorComposite getArticleContainerEditorComposite();
}
