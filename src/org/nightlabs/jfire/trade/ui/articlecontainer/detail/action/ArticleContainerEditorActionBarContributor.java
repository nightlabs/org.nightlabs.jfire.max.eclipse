/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.internal.ActionSetContributionItem;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleSegmentGroup;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.link.ArticleContainerLink;
import org.nightlabs.jfire.trade.link.ArticleContainerLinkType;
import org.nightlabs.jfire.trade.link.dao.ArticleContainerLinkDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ActiveSegmentEditSelectionEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ActiveSegmentEditSelectionListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleCreateEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleCreateListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ClientArticleSegmentGroupSet;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.CreateArticleEditEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.CreateArticleEditListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.IArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditArticleSelectionEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditArticleSelectionListener;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class ArticleContainerEditorActionBarContributor
extends EditorActionBarContributor
implements IArticleContainerEditActionContributor
{
	private static final Logger logger = Logger.getLogger(ArticleContainerEditorActionBarContributor.class);

	public static final String SEPARATOR_BETWEEN_ARTICLE_CONTAINER_ACTIONS_AND_ARTICLE_EDIT_ACTIONS = "betweenArticleContainerActionsAndArticleEditActions"; //$NON-NLS-1$
	public static final String EDIT_MENU_ID = ArticleContainerEditorActionBarContributor.class.getPackage().getName();

//	public static final String SEPARATOR_ARTICLE_CONTAINER_LINKS = "separateArticleContainerLinks"; //$NON-NLS-1$
	public static final String ARTICLE_CONTAINER_LINKS_MENU_ID = ArticleContainerLink.class.getName();

	private IArticleContainerEditor activeArticleContainerEditor = null;
	private ArticleContainerEdit activeArticleContainerEdit = null;
	private SegmentEdit activeSegmentEdit = null;

	public ArticleContainerEditorActionBarContributor()
	{
		// TODO: WORKAROUND This is a workaround as setActiveEditor is not called when
		// the perspective is switched and therefore the contributions are not removed
		RCPUtil.getActiveWorkbenchPage().addPartListener(partListener);
		RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(perspectiveListener);
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (activeArticleContainerEditor == targetEditor)
			return;

		// First clean up everything (the ActionRegistry might change or become null (if targetEditor is null, which we emulate even though Eclipse doesn't notify this contributor).
		if (activeArticleContainerEdit != null) {
			removeContributions();
//			ArticleContainer articleContainer = activeArticleContainerEdit.getArticleContainer();
//			try {
//				ArticleContainerActionRegistry.sharedInstance(articleContainer).setActiveArticleContainerEditorActionBarContributor(null);
//				ArticleEditActionRegistry.sharedInstance().setActiveArticleContainerEditorActionBarContributor(null);
//			} catch (EPProcessorException e) {
//				throw new RuntimeException(e);
//			}
		}

		if (activeArticleContainerEdit != null && !activeArticleContainerEdit.getComposite().isDisposed()) {
			activeArticleContainerEdit.removeActiveSegmentEditSelectionListener(activeSegmentEditSelectionListener);
			activeArticleContainerEdit.getComposite().removeDisposeListener(articleContainerEditorCompositeDisposeListener);
		}

		// Make sure we have no active stuff.
		activeArticleContainerEditor = null;
		activeArticleContainerEdit = null;
		activeSegmentEdit = null;

		// Assign the new active stuff.
		activeArticleContainerEditor = (IArticleContainerEditor) targetEditor;
		activeArticleContainerEdit = activeArticleContainerEditor != null ? activeArticleContainerEditor.getArticleContainerEdit() : null;

		// If the new active stuff is not null, we register various listeners and assign other stuff.
		if (activeArticleContainerEdit != null && !activeArticleContainerEdit.getComposite().isDisposed()) {
			activeArticleContainerEdit.setArticleContainerEditActionContributor(this);
			activeArticleContainerEdit.addActiveSegmentEditSelectionListener(activeSegmentEditSelectionListener);
			activeArticleContainerEdit.getComposite().addDisposeListener(articleContainerEditorCompositeDisposeListener);
		}

//		if (activeArticleContainerEdit != null) {
//			ArticleContainer articleContainer = activeArticleContainerEdit.getArticleContainer();
//			try {
//				ArticleContainerActionRegistry.sharedInstance(articleContainer).setActiveArticleContainerEditorActionBarContributor(this);
//				ArticleEditActionRegistry.sharedInstance().setActiveArticleContainerEditorActionBarContributor(this);
//			} catch (EPProcessorException e) {
//				throw new RuntimeException(e);
//			}
//		}

		activeSegmentEditSelected();
	}

	private DisposeListener articleContainerEditorCompositeDisposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e)
		{
			Control gec = (Control) e.widget;
			if (gec == activeArticleContainerEdit.getComposite()) // should be, because we remove the listeners when switching active, but secure is better
				setActiveEditor(null);
		}
	};

	public ArticleContainerEdit getActiveArticleContainerEdit()
	{
		return activeArticleContainerEdit;
	}

	private ActiveSegmentEditSelectionListener activeSegmentEditSelectionListener = new ActiveSegmentEditSelectionListener() {
		public void selected(ActiveSegmentEditSelectionEvent event) {
			activeSegmentEditSelected();
		}
	};

	private SegmentEditArticleSelectionListener segmentEditArticleSelectionListener = new SegmentEditArticleSelectionListener() {
		public void selected(SegmentEditArticleSelectionEvent event) {
			contributeActions(); // TODO is it really better / necessary to recreate the actions instead of only recalculate the enabled state?
//			calculateArticleEditActionsEnabled(event.getArticleSelections());
		}
	};

	private CreateArticleEditListener createArticleEditListener = new CreateArticleEditListener() {
		public void createdArticleEdits(CreateArticleEditEvent event)
		{
			contributeActions();
		}
	};

	private void contributeActionsIfArticleCarriersAffectActiveSegmentEdit(Collection<ArticleCarrier> articleCarriers)
	{
//	 This listener is subscribed to the whole ClientArticleSegmentGroupSet and therefore not all Articles
		// in the current event are necessarily in the currently active SegmentEdit.
		if (activeSegmentEdit == null)
			return;

		ArticleSegmentGroup articleSegmentGroup = activeSegmentEdit.getArticleSegmentGroup();
		for (ArticleCarrier articleCarrier : articleCarriers) {
			if (articleCarrier.getArticleSegmentGroup() == articleSegmentGroup) {
				contributeActions(); // TODO is it really better / necessary to recreate the actions instead of only recalculate the enabled state?
				return;
			}
		}
	}

	private ArticleCreateListener articleCreateListener = new ArticleCreateListener() {
		public void articlesCreated(ArticleCreateEvent articleCreateEvent)
		{
			contributeActionsIfArticleCarriersAffectActiveSegmentEdit(articleCreateEvent.getArticleCarriers());
		}
	};

	private ArticleChangeListener articleChangeListener = new ArticleChangeListener() {
		public void articlesChanged(ArticleChangeEvent articleChangeEvent)
		{
			contributeActionsIfArticleCarriersAffectActiveSegmentEdit(articleChangeEvent.getDeletedArticleCarriers());
			contributeActionsIfArticleCarriersAffectActiveSegmentEdit(articleChangeEvent.getDirtyArticleCarriers());
		}
	};

	protected void calculateArticleContainerActionsEnabled()
	{
		if (activeArticleContainerEdit == null)
			return;

		ArticleContainer articleContainer = activeArticleContainerEdit.getArticleContainer();
		if (articleContainer == null)
			return;

		ArticleContainerActionRegistry articleContainerActionRegistry;
		try {
			articleContainerActionRegistry = ArticleContainerActionRegistry.sharedInstance(articleContainer);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		articleContainerActionRegistry.setActiveArticleContainerEditorActionBarContributor(this);

		for (Iterator<ActionDescriptor> it = articleContainerActionRegistry.getActionDescriptors().iterator(); it.hasNext(); ) {
			ActionDescriptor actionDescriptor = it.next();
			if (!actionDescriptor.isVisible())
				continue; // ignore invisible actions - enabled doesn't matter for them

			IArticleContainerAction action = (IArticleContainerAction) actionDescriptor.getAction();
			IXContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (action != null)
				action.setEnabled(action.calculateEnabled());
			else if (contributionItem != null) {
				if (contributionItem instanceof IArticleContainerContributionItem) {
					IArticleContainerContributionItem acci = (IArticleContainerContributionItem)contributionItem;
					acci.setEnabled(acci.calculateEnabled());
				}
			}
			else
				throw new IllegalStateException("both, action and contribution item are null!"); //$NON-NLS-1$
		}
	}

	/**
	 * This method is called whenever the selection of {@link org.nightlabs.jfire.trade.ui.Article}s within
	 * an {@link ArticleEdit} changes.
	 *
	 * @param articleSelections Instances of {@link ArticleSelection} as returned by {@link SegmentEdit#getArticleSelections()}
	 *		and {@link SegmentEditArticleSelectionEvent#getArticleSelections()}.
	 */
	protected void calculateArticleEditActionsEnabled(Set<ArticleSelection> articleSelections)
	{
		ArticleContainer articleContainer = activeArticleContainerEdit.getArticleContainer();
		if (articleContainer == null)
			return;

		ArticleEditActionRegistry articleEditActionRegistry;
		try {
			articleEditActionRegistry = ArticleEditActionRegistry.sharedInstance(articleContainer);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		articleEditActionRegistry.setActiveArticleContainerEditorActionBarContributor(this);

		for (Iterator<ActionDescriptor> itAC = articleEditActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = itAC.next();
			if (!actionDescriptor.isVisible())
				continue; // ignore invisible actions - enabled doesn't matter for them

			IArticleEditAction action = (IArticleEditAction) actionDescriptor.getAction();
			IXContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (action != null) {
				// If there's nothing selected, all actions must be disabled.
//				if (articleSelections.isEmpty())
//					action.setEnabled(false);
//				else
					action.setEnabled(action.calculateEnabled(articleSelections));
			}
			else if (contributionItem != null) {
//				if (articleSelections.isEmpty())
//					contributionItem.setEnabled(false);
//				else {
					if (contributionItem instanceof IArticleEditContributionItem) {
						IArticleEditContributionItem aeci = (IArticleEditContributionItem)contributionItem;
						aeci.setEnabled(aeci.calculateEnabled(articleSelections));
					}
					else
						contributionItem.setEnabled(!articleSelections.isEmpty());
//				}
			}
			else
				throw new IllegalStateException("both, action and contribution item are null!"); //$NON-NLS-1$
		} // for (Iterator itAC = articleEditActionRegistry.getActionCarriers().iterator(); itAC.hasNext(); ) {
	}

	/**
	 * This method is called whenever the active {@link SegmentEdit} has changed. This
	 * can either happen when the user switches the focus to another editor or when
	 * the user switches to another <code>SegmentEdit</code> within the same editor.
	 * <p>
	 * This method calls {@link #contributeActions()}.
	 * </p>
	 */
	private void activeSegmentEditSelected()
	{
		SegmentEdit oldActiveSegmentEdit = activeSegmentEdit;
		activeSegmentEdit = activeArticleContainerEdit == null ? null : activeArticleContainerEdit.getActiveSegmentEdit();
		if (oldActiveSegmentEdit != activeSegmentEdit) {
			if (oldActiveSegmentEdit != null) {
				oldActiveSegmentEdit.removeSegmentEditArticleSelectionListener(segmentEditArticleSelectionListener);
				oldActiveSegmentEdit.removeCreateArticleEditListener(createArticleEditListener);
			}

			if (activeSegmentEdit != null) {
				activeSegmentEdit.addSegmentEditArticleSelectionListener(segmentEditArticleSelectionListener);
				activeSegmentEdit.addCreateArticleEditListener(createArticleEditListener);
			}
		}

		ClientArticleSegmentGroupSet oldClientArticleSegmentGroups = oldActiveSegmentEdit == null ? null : oldActiveSegmentEdit.getClientArticleSegmentGroupSet();
		ClientArticleSegmentGroupSet clientArticleSegmentGroupSet = activeSegmentEdit == null ? null : activeSegmentEdit.getClientArticleSegmentGroupSet();

		if (oldClientArticleSegmentGroups != clientArticleSegmentGroupSet) {
			if (oldClientArticleSegmentGroups != null) {
				oldClientArticleSegmentGroups.removeArticleCreateListener(articleCreateListener);
				oldClientArticleSegmentGroups.removeArticleChangeListener(articleChangeListener);
			}
			if (clientArticleSegmentGroupSet != null) {
				clientArticleSegmentGroupSet.addArticleCreateListener(articleCreateListener);
				clientArticleSegmentGroupSet.addArticleChangeListener(articleChangeListener);
			}
		}

		contributeActions();
	}

	private ICoolBarManager getCoolBarManager()
	{
		IActionBars2 actionBars = (IActionBars2) getActionBars();
		ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
		if (coolBarManager == null)
			throw new IllegalStateException("coolBarManager is null! Why has init(...) not been called?!"); //$NON-NLS-1$
		return coolBarManager;
	}

	private IMenuManager getPulldownMenuManager()
	{
		IActionBars2 actionBars = (IActionBars2) getActionBars();
		IMenuManager pulldownMenuManager = actionBars.getMenuManager();
		if (pulldownMenuManager == null)
			throw new IllegalStateException("pulldownMenuManager is null! Why has init(...) not been called?!"); //$NON-NLS-1$

		IMenuManager realPulldownMenuManager = (IMenuManager) ((SubContributionManager)pulldownMenuManager).getParent();
		return realPulldownMenuManager;
	}

	/**
	 * This method is called by {@link #activeSegmentEditSelected()} and {@link #createArticleEditListener} TODO it must be called, too, when an ArticleEdit is removed.
	 * It flushes all Actions (in the toolbar, the pulldown menu, the articleContainerContextMenu and
	 * the articleEditContextMenu) and then contributes
	 * first the {@link IArticleContainerAction}s and then the {@link IArticleEditAction}s. Only the
	 * {@link IArticleContainerAction}s are contributed to the articleContainerContextMenu.
	 */
	protected void contributeActions()
	{
		if (logger.isDebugEnabled())
			logger.debug("contributeActions"); //$NON-NLS-1$

		if (activeArticleContainerEdit == null)
			return;

		ArticleContainer articleContainer = activeArticleContainerEdit.getArticleContainer();
		if (articleContainer == null)
			return;

		ICoolBarManager coolBarManager = getCoolBarManager();
		IMenuManager realPulldownMenuManager = getPulldownMenuManager();

		ArticleContainerActionRegistry articleContainerActionRegistry;
		ArticleEditActionRegistry articleEditActionRegistry;
		try {
			articleContainerActionRegistry = ArticleContainerActionRegistry.sharedInstance(articleContainer);
			articleEditActionRegistry = ArticleEditActionRegistry.sharedInstance(articleContainer);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		articleContainerActionRegistry.setActiveArticleContainerEditorActionBarContributor(this);
		articleEditActionRegistry.setActiveArticleContainerEditorActionBarContributor(this);

		// disable all ArticleContainerActions and calculate which ones must be visible
		for (Iterator<ActionDescriptor> itAC = articleContainerActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = itAC.next();
			IArticleContainerAction action = (IArticleContainerAction) actionDescriptor.getAction();
			IXContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (action != null) {
				action.setEnabled(false);

				if (activeArticleContainerEditor == null)
					actionDescriptor.setVisible(false);
				else
					actionDescriptor.setVisible(action.calculateVisible());
			}
			else if (contributionItem != null) {
				contributionItem.setEnabled(false);

				if (activeArticleContainerEditor == null)
					contributionItem.setVisible(false);
				else {
					if (contributionItem instanceof IArticleContainerContributionItem)
						contributionItem.setVisible(((IArticleContainerContributionItem)contributionItem).calculateVisible());
					else
						contributionItem.setVisible(true);
				}
			}
			else
				throw new IllegalStateException("both, action and contribution item, are null!"); //$NON-NLS-1$
		}

		// disable all ArticleEditActions and calculate which ones must be visible
		for (Iterator<ActionDescriptor> itAC = articleEditActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = itAC.next();
			IArticleEditAction action = (IArticleEditAction) actionDescriptor.getAction();
			IXContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (action != null) {
				action.setEnabled(false);

				if (activeSegmentEdit == null || activeSegmentEdit.getArticleEdits().isEmpty())
					actionDescriptor.setVisible(false);
				else
					actionDescriptor.setVisible(action.calculateVisible());
			}
			else if (contributionItem != null) {
				contributionItem.setEnabled(false);

				if (activeArticleContainerEditor == null)
					contributionItem.setVisible(false);
				else {
					if (contributionItem instanceof IArticleEditContributionItem)
						contributionItem.setVisible(((IArticleEditContributionItem)contributionItem).calculateVisible());
					else
						contributionItem.setVisible(true);
				}
			}
			else
				throw new IllegalStateException("both, action and contribution item, are null!"); //$NON-NLS-1$
		}

		// contribute to the main pulldown menu
		if (localPulldownMenuManager == null) {
			// do we have the menu already from a previous session (=> workbench.xml)?
			IContributionItem contributionItem = realPulldownMenuManager.find(EDIT_MENU_ID);
			if (contributionItem instanceof IMenuManager) {
				localPulldownMenuManager = (IMenuManager) contributionItem;
			}
			// added to avoid ClasscastException when same menu already comes from actionSet extension-point
			else if (contributionItem instanceof ActionSetContributionItem) {
				ActionSetContributionItem actionItem = (ActionSetContributionItem) contributionItem;
				IContributionItem innerItem = actionItem.getInnerItem();
				if (innerItem != null && innerItem instanceof IMenuManager) {
					localPulldownMenuManager = (IMenuManager) innerItem;
				}
			}

			if (localPulldownMenuManager == null) {
				localPulldownMenuManager = new MenuManager(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor.pulldownMenu.text"), //$NON-NLS-1$
						EDIT_MENU_ID);
				realPulldownMenuManager.insertAfter(
						// IWorkbenchActionConstants.MB_ADDITIONS,
						IWorkbenchActionConstants.M_FILE,
						localPulldownMenuManager);
			}
		}
//		localPulldownMenuManager.removeAll();
		articleContainerActionRegistry.removeAllFromMenuBar(localPulldownMenuManager);
		articleEditActionRegistry.removeAllFromMenuBar(localPulldownMenuManager);

		articleContainerActionRegistry.contributeToMenuBar(localPulldownMenuManager);
		localPulldownMenuManager.add(new Separator(SEPARATOR_BETWEEN_ARTICLE_CONTAINER_ACTIONS_AND_ARTICLE_EDIT_ACTIONS));
		articleEditActionRegistry.contributeToMenuBar(localPulldownMenuManager);


		populateMenuManagerWithArticleContainerLinks(localPulldownMenuManager);


		// contribute to the coolbar
		articleContainerActionRegistry.contributeToCoolBar(coolBarManager);
		articleEditActionRegistry.contributeToCoolBar(coolBarManager);

		calculateArticleContainerActionsEnabled();

		if (activeSegmentEdit != null)
			calculateArticleEditActionsEnabled(activeSegmentEdit.getArticleSelections());

		getActionBars().updateActionBars();
	}


	private void populateMenuManagerWithArticleContainerLinks(IMenuManager containerMenuManager)
	{
		final Display display = Display.getCurrent();
		if (display == null)
			throw new IllegalStateException("Thread mismatch! Must call this method on the SWT UI thread!!!"); //$NON-NLS-1$

		if (activeArticleContainerEdit == null)
			return;

		ArticleContainer articleContainer = activeArticleContainerEdit.getArticleContainer();
		if (articleContainer == null)
			return;

		// If the 'Open related' sub-menu exists (i.e. openRelatedSubMenuExists becomes true), we add our new
		// sub-menu as a sibling directly below it - otherwise, we add it to the end.
		String openRelatedSubMenuID = "org.nightlabs.jfire.trade.ui.articleEditAction.openRelatedMenu"; //$NON-NLS-1$
		boolean openRelatedSubMenuExists = false;

		// menuManager will be our sub-menu (within containerMenuManager).
		IMenuManager menuManager = null;

		// Search for an old sub-menu (in case it was registered in a previous run).
		for (IContributionItem item : containerMenuManager.getItems()) {
			if (openRelatedSubMenuID.equals(item.getId()))
				openRelatedSubMenuExists = true;

			if (ARTICLE_CONTAINER_LINKS_MENU_ID.equals(item.getId()) && item instanceof IMenuManager)
					menuManager = (IMenuManager) item;
		}

		// Delete it, if it exists.
		if (menuManager != null) {
			containerMenuManager.remove(menuManager);
			menuManager = null;
		}

		// Create the new menu.
		menuManager = new MenuManager(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor.articleContainerLinksMenu.text"), ARTICLE_CONTAINER_LINKS_MENU_ID); //$NON-NLS-1$
		if (openRelatedSubMenuExists)
			containerMenuManager.insertAfter(openRelatedSubMenuID, menuManager);
		else
			containerMenuManager.add(menuManager);

		// Populate our new sub-menu.
		final ArticleContainerID articleContainerID = (ArticleContainerID) JDOHelper.getObjectId(articleContainer);
		if (articleContainerID == null)
			throw new IllegalStateException("JDOHelper.getObjectId(articleContainer) returned null!!!"); //$NON-NLS-1$


		List<ArticleContainerLink> currentArticleContainerLinks = null;
		synchronized (articleContainerLinksRetrieveMutex) {
			if (articleContainerID.equals(articleContainerLinksBelongingToArticleContainerID) && articleContainerLinks != null)
				currentArticleContainerLinks = articleContainerLinks;
		}

		if (currentArticleContainerLinks != null) {
			internalPopulateMenuManagerWithArticleContainerLinks(menuManager, currentArticleContainerLinks);
		}
		else {
			final IMenuManager mm = menuManager;

			final String loadingID = ArticleContainerLink.class.getName() + "/loading"; //$NON-NLS-1$
			mm.add(new Action() {
				{
					setId(loadingID);
					setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor.loadArticleContainerLinksDummyAction.text")); //$NON-NLS-1$
				}
			});

			Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor.loadArticleContainerLinksJob.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception
				{
					List<ArticleContainerLink> currentArticleContainerLinks = null;
					synchronized (articleContainerLinksRetrieveMutex) {
						if (articleContainerID.equals(articleContainerLinksBelongingToArticleContainerID) && articleContainerLinks != null)
							currentArticleContainerLinks = articleContainerLinks;
					}

					if (currentArticleContainerLinks == null) {
						logger.debug("Loading ArticleContainerLinks for " + articleContainerID + "..."); //$NON-NLS-1$ //$NON-NLS-2$

						currentArticleContainerLinks = ArticleContainerLinkDAO.sharedInstance().getArticleContainerLinks(
								articleContainerID,
								null,
								new String[] {
										FetchPlan.DEFAULT,
										ArticleContainerLink.FETCH_GROUP_ARTICLE_CONTAINER_LINK_TYPE,
										ArticleContainerLinkType.FETCH_GROUP_NAME,
										ArticleContainerLink.FETCH_GROUP_TO
								},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								monitor
						);

						logger.debug("Loading ArticleContainerLinks for " + articleContainerID + " DONE!"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else
						logger.debug("Loading ArticleContainerLinks for " + articleContainerID + " not necessary, because they were already loaded in the meantime."); //$NON-NLS-1$ //$NON-NLS-2$

					final List<ArticleContainerLink> links = currentArticleContainerLinks;

					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							synchronized (articleContainerLinksRetrieveMutex) {
								articleContainerLinks = links;
								articleContainerLinksBelongingToArticleContainerID = articleContainerID;
							}

							mm.remove(loadingID);
							internalPopulateMenuManagerWithArticleContainerLinks(mm, links);
							mm.updateAll(true);
						}
					});

					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.setRule(articleContainerLinksRetrieveSchedulingRule);
			job.schedule();
		}
	}

	private static void internalPopulateMenuManagerWithArticleContainerLinks(IMenuManager menuManager, Collection<? extends ArticleContainerLink> articleContainerLinks)
	{
		final String localOrganisationID;
		try {
			localOrganisationID = Login.getLogin().getOrganisationID();
		} catch (LoginException e) {
			throw new RuntimeException(e); // should never happen, since we should never come here when not logged in.
		}

		for (final ArticleContainerLink articleContainerLink : articleContainerLinks) {
			menuManager.add(new Action() {
				{
					final String linkedArticleContainerTypeName = TradePlugin.getArticleContainerTypeString(articleContainerLink.getTo().getClass(), false);
					final String linkedArticleContainerID =
						(localOrganisationID.equals(articleContainerLink.getTo().getOrganisationID()) ? "" : (articleContainerLink.getTo().getOrganisationID() + "/")) + //$NON-NLS-1$ //$NON-NLS-2$
						articleContainerLink.getTo().getArticleContainerIDPrefix() + '/' +
						articleContainerLink.getTo().getArticleContainerIDAsString();

					setId(ArticleContainerLink.class.getName() + '/' + articleContainerLink.getOrganisationID() + '/' + articleContainerLink.getArticleContainerLinkID());
					setText(
							String.format(
									Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor.articleContainerLinkAction.text"), //$NON-NLS-1$
									articleContainerLink.getArticleContainerLinkType().getName().getText(),
									linkedArticleContainerTypeName,
									linkedArticleContainerID
							)
					);
					setImageDescriptor(TradePlugin.getArticleContainerImageDescriptor(articleContainerLink.getTo().getClass()));
				}

				@Override
				public void run() {
					ArticleContainerID articleContainerID = (ArticleContainerID) JDOHelper.getObjectId(articleContainerLink.getTo());
					ArticleContainerEditorInput input = new ArticleContainerEditorInput(articleContainerID);
					try {
						RCPUtil.openEditor(input, ArticleContainerEditor.ID_EDITOR);
					} catch (PartInitException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}

	private Object articleContainerLinksRetrieveMutex = new Object();
	private List<ArticleContainerLink> articleContainerLinks;
	private ArticleContainerID articleContainerLinksBelongingToArticleContainerID;
	private ISchedulingRule articleContainerLinksRetrieveSchedulingRule = new ISchedulingRule() {
		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			return this == rule;
		}
		@Override
		public boolean contains(ISchedulingRule rule) {
			return this == rule;
		}
	};


	/**
	 * This is the manager for the local (article container/edit actions) pulldown menu.
	 */
	private IMenuManager localPulldownMenuManager = null;

	public Menu createArticleContainerContextMenu(Control parent)
	{
		Menu menu = parent.getMenu();
		if (menu != null) {
			parent.setMenu(null);
			menu.dispose();
			menu = null;
		}

		final ArticleContainer articleContainer = activeArticleContainerEdit == null ? null : activeArticleContainerEdit.getArticleContainer();

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager)
			{
				if (articleContainer == null)
					return;

				ArticleContainerActionRegistry articleContainerActionRegistry;
				try {
					articleContainerActionRegistry = ArticleContainerActionRegistry.sharedInstance(articleContainer);
				} catch (EPProcessorException e) {
					throw new RuntimeException(e);
				}
				articleContainerActionRegistry.setActiveArticleContainerEditorActionBarContributor(ArticleContainerEditorActionBarContributor.this);
				articleContainerActionRegistry.contributeToContextMenu(manager);
				populateMenuManagerWithArticleContainerLinks(manager);
			}
		});

		menu = menuManager.createContextMenu(parent);
		parent.setMenu(menu);
		return menu;
	}

	public Menu createArticleEditContextMenu(Control parent)
	{
		Menu menu = parent.getMenu();
		if (menu != null) {
			parent.setMenu(null);
			menu.dispose();
			menu = null;
		}

		final ArticleContainer articleContainer = activeArticleContainerEdit == null ? null : activeArticleContainerEdit.getArticleContainer();

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager)
			{
				if (articleContainer == null)
					return;

				ArticleContainerActionRegistry articleContainerActionRegistry;
				ArticleEditActionRegistry articleEditActionRegistry;
				try {
					articleContainerActionRegistry = ArticleContainerActionRegistry.sharedInstance(articleContainer);
					articleEditActionRegistry = ArticleEditActionRegistry.sharedInstance(articleContainer);
				} catch (EPProcessorException e) {
					throw new RuntimeException(e);
				}
				articleContainerActionRegistry.setActiveArticleContainerEditorActionBarContributor(ArticleContainerEditorActionBarContributor.this);
				articleEditActionRegistry.setActiveArticleContainerEditorActionBarContributor(ArticleContainerEditorActionBarContributor.this);
				articleContainerActionRegistry.contributeToContextMenu(manager);
				if (activeSegmentEdit != null) {
					manager.add(new Separator(SEPARATOR_BETWEEN_ARTICLE_CONTAINER_ACTIONS_AND_ARTICLE_EDIT_ACTIONS));
					articleEditActionRegistry.contributeToContextMenu(manager);
				}
				populateMenuManagerWithArticleContainerLinks(manager);
			}
		});

		menu = menuManager.createContextMenu(parent);
		parent.setMenu(menu);
		return menu;
	}

	private IPartListener2 partListener = new IPartListener2 () {
		public void partActivated(IWorkbenchPartReference partRef) {
//			if (activeArticleContainerEditor != null &&
//				(partRef.getPart(false) instanceof IEditorPart) &&
//				(!activeArticleContainerEditor.equals(partRef.getPart(false)))
//			)
//			{
//				logger.debug("Part activated"); //$NON-NLS-1$
////				removeContributions();
////				activeArticleContainerEditor = null;
//				setActiveEditor(null);
//			}
		}
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}
		public void partClosed(IWorkbenchPartReference partRef) {
			if (activeArticleContainerEditor != null) {
				IWorkbenchPart part = partRef.getPart(false);
				if (activeArticleContainerEditor.equals(part))
					setActiveEditor(null);
			}
		}
		public void partDeactivated(IWorkbenchPartReference partRef) {
//			if (activeArticleContainerEditor != null) {
//				IWorkbenchPart part = partRef.getPart(false);
//				if (activeArticleContainerEditor.equals(part))
//					setActiveEditor(null);
//			}
		}
		public void partHidden(IWorkbenchPartReference partRef) {
		}
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}
		public void partOpened(IWorkbenchPartReference partRef) {
		}
		public void partVisible(IWorkbenchPartReference partRef) {
		}
	};

	private IPerspectiveListener4 perspectiveListener = new PerspectiveAdapter() {
		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
//			if ((!(TradePerspective.ID_PERSPECTIVE.equals(perspective.getId()) ||
//				 (QuickSalePerspective.ID_PERSPECTIVE.equals(perspective.getId())))))
//			{
//				logger.debug("Perspective activated: " + perspective.getId() + ", contributions will be removed.");					 //$NON-NLS-1$ //$NON-NLS-2$
////				removeContributions();
////				activeArticleContainerEditor = null;
//				setActiveEditor(null);
//			}
		}
		@Override
		public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
		{
			setActiveEditor(null);
		}
	};

	private void removeContributions() {
		if (activeArticleContainerEdit == null)
			return;

		ArticleContainer articleContainer = activeArticleContainerEdit.getArticleContainer();
		if (articleContainer == null)
			return;

		IActionBars2 actionBars = (IActionBars2) getActionBars();
//		IMenuManager menuManager = actionBars.getMenuManager();
		ICoolBarManager coolBarManager = actionBars.getCoolBarManager();

		ArticleContainerActionRegistry articleContainerActionRegistry;
		ArticleEditActionRegistry articleEditActionRegistry;
		try {
			articleContainerActionRegistry = ArticleContainerActionRegistry.sharedInstance(articleContainer);
			articleEditActionRegistry = ArticleEditActionRegistry.sharedInstance(articleContainer);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		articleContainerActionRegistry.setActiveArticleContainerEditorActionBarContributor(this);
		articleEditActionRegistry.setActiveArticleContainerEditorActionBarContributor(this);

		articleContainerActionRegistry.removeAllFromCoolBar(coolBarManager);
		articleEditActionRegistry.removeAllFromCoolBar(coolBarManager);

		if (localPulldownMenuManager != null) {
			articleContainerActionRegistry.removeAllFromMenuBar(localPulldownMenuManager);
			articleEditActionRegistry.removeAllFromMenuBar(localPulldownMenuManager);
		}
	}
}
