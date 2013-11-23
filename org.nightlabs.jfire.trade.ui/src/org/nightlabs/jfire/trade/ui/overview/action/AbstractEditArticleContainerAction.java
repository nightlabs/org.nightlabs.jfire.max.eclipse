package org.nightlabs.jfire.trade.ui.overview.action;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.overview.action.IOverviewEditAction;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractEditArticleContainerAction
extends AbstractArticleContainerAction
implements IOverviewEditAction
{
	public static final String ID = AbstractEditArticleContainerAction.class.getName();

	public AbstractEditArticleContainerAction() {
		super();
		init();
	}

	protected void init() {
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.action.AbstractEditArticleContainerAction.text")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				TradePlugin.getDefault(), AbstractEditArticleContainerAction.class));
	}

	@Override
	public void run()
	{
		IEditorInput input = getEditorInput();
		if (! (input instanceof ArticleContainerEditorInput) )
			throw new IllegalArgumentException("This subclass: "+this+" does not return an input type, which is not a subclass of ArticleContainerEditorInput. This must not be allowed!"); //$NON-NLS-1$ //$NON-NLS-2$

		ArticleContainerEditorUtil.openArticleContainerInTradePespective(input, getEditorID());
	}

}
