package org.nightlabs.jfire.trade.ui.overview;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractArticleContainerActionBarContributor
extends OverviewEntryEditorActionBarContributor
{

	public AbstractArticleContainerActionBarContributor() {
		super();
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
		if (getShowAction() != null)
			toolBarManager.add(getShowAction());
		if (getPrintAction() != null)
			toolBarManager.add(getPrintAction());
	}

	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) {
		super.contributeToCoolBar(coolBarManager);
		if (getShowAction() != null)
			coolBarManager.add(getShowAction());
		if (getPrintAction() != null)
			coolBarManager.add(getPrintAction());
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		if (getShowAction() != null)
			menuManager.add(getShowAction());
		if (getPrintAction() != null)
			menuManager.add(getPrintAction());
	}
	
	protected abstract AbstractShowArticleContainerAction createShowAction();
	private AbstractShowArticleContainerAction showAction = null;
	public  AbstractShowArticleContainerAction getShowAction() {
		if (showAction == null && getEditor() != null)
			showAction = createShowAction();
		return showAction;
	}
	
	protected abstract AbstractPrintArticleContainerAction createPrintAction();
	private AbstractPrintArticleContainerAction printAction = null;
	public AbstractPrintArticleContainerAction getPrintAction() {
		if (printAction == null && getEditor() != null)
			printAction = createPrintAction();
		return printAction;
	}
}
