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
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.internal.ActionSetContributionItem;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleSegmentGroup;
import org.nightlabs.jfire.trade.ui.QuickSalePerspective;
import org.nightlabs.jfire.trade.ui.TradePerspective;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ActiveSegmentEditSelectionEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ActiveSegmentEditSelectionListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorComposite;
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

public class ArticleContainerEditorActionBarContributor
extends EditorActionBarContributor
{
	private static final Logger logger = Logger.getLogger(ArticleContainerEditorActionBarContributor.class);

	public static final String SEPARATOR_BETWEEN_ARTICLE_CONTAINER_ACTIONS_AND_ARTICLE_EDIT_ACTIONS = "betweenArticleContainerActionsAndArticleEditActions"; //$NON-NLS-1$
	public static final String EDIT_MENU_ID = ArticleContainerEditorActionBarContributor.class.getPackage().getName();
	
	private IArticleContainerEditor activeArticleContainerEditor = null;
	private ArticleContainerEditorComposite activeArticleContainerEditorComposite = null;
	private SegmentEdit activeSegmentEdit = null;

	public ArticleContainerEditorActionBarContributor()
	{
		// TODO: WORKAROUND This is a workaround as setActiveEditor is not called when 
		// the perspective is switched and therefore the contributions are not removed
		RCPUtil.getActiveWorkbenchPage().addPartListener(partListener);
		RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(perspectiveListener);
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (activeArticleContainerEditor == targetEditor)
			return;

		if (activeArticleContainerEditorComposite != null && !activeArticleContainerEditorComposite.isDisposed()) {
			activeArticleContainerEditorComposite.removeActiveSegmentEditSelectionListener(activeSegmentEditSelectionListener);
			activeArticleContainerEditorComposite.removeDisposeListener(articleContainerEditorCompositeDisposeListener);
		}

		activeArticleContainerEditor = (IArticleContainerEditor)targetEditor;
		activeArticleContainerEditorComposite = activeArticleContainerEditor == null ? null : activeArticleContainerEditor.getArticleContainerEditorComposite();

		if (activeArticleContainerEditorComposite != null && !activeArticleContainerEditorComposite.isDisposed()) {
			activeArticleContainerEditorComposite.setArticleContainerEditorActionBarContributor(this);
			activeArticleContainerEditorComposite.addActiveSegmentEditSelectionListener(activeSegmentEditSelectionListener);
			activeArticleContainerEditorComposite.addDisposeListener(articleContainerEditorCompositeDisposeListener);
		}

		try {
			ArticleContainerActionRegistry.sharedInstance().setActiveArticleContainerEditorActionBarContributor(this);
			ArticleEditActionRegistry.sharedInstance().setActiveArticleContainerEditorActionBarContributor(this);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		activeSegmentEditSelected();
	}

	private DisposeListener articleContainerEditorCompositeDisposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e)
		{
			ArticleContainerEditorComposite gec = (ArticleContainerEditorComposite) e.widget;
			if (gec == activeArticleContainerEditorComposite) // should be, because we remove the listeners when switching active, but secure is better
				setActiveEditor(null);
		}
	};

	public IArticleContainerEditor getActiveArticleContainerEditor()
	{
		return activeArticleContainerEditor;
	}
	
	public ArticleContainerEditorComposite getActiveArticleContainerEditorComposite()
	{
		return activeArticleContainerEditorComposite;
	}
	
	public SegmentEdit getActiveSegmentEdit()
	{
		return activeSegmentEdit;
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
		ArticleContainerActionRegistry articleContainerActionRegistry;
		try {
			articleContainerActionRegistry = ArticleContainerActionRegistry.sharedInstance();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

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
		ArticleEditActionRegistry articleEditActionRegistry;
		try {
			articleEditActionRegistry = ArticleEditActionRegistry.sharedInstance();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		for (Iterator<ActionDescriptor> itAC = articleEditActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = itAC.next();
			if (!actionDescriptor.isVisible())
				continue; // ignore invisible actions - enabled doesn't matter for them

			IArticleEditAction action = (IArticleEditAction) actionDescriptor.getAction();
			IXContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (action != null) {
				// If there's nothing selected, all actions must be disabled.
				if (articleSelections.isEmpty())
					action.setEnabled(false);
				else
					action.setEnabled(action.calculateEnabled(articleSelections));
			}
			else if (contributionItem != null) {
				if (articleSelections.isEmpty())
					contributionItem.setEnabled(false);
				else {
					if (contributionItem instanceof IArticleEditContributionItem) {
						IArticleEditContributionItem aeci = (IArticleEditContributionItem)contributionItem;
						aeci.setEnabled(aeci.calculateEnabled(articleSelections));
					}
					else
						contributionItem.setEnabled(true);
				}
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
		activeSegmentEdit = activeArticleContainerEditorComposite == null ? null : activeArticleContainerEditorComposite.getActiveSegmentEdit();
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

	/**
	 * This method is called by {@link #activeSegmentEditSelected()} and {@link #createArticleEditListener} TODO it must be called, too, when an ArticleEdit is removed.
	 * It flushes all Actions (in the toolbar, the pulldown menu, the articleContainerContextMenu and
	 * the articleEditContextMenu) and then contributes
	 * first the {@link IArticleContainerAction}s and then the {@link IArticleEditAction}s. Only the
	 * {@link IArticleContainerAction}s are contributed to the articleContainerContextMenu.
	 */
	protected void contributeActions()
	{
		IActionBars2 actionBars = (IActionBars2) getActionBars();
		logger.debug("contributeActions"); //$NON-NLS-1$

//		if (toolBarManager == null)
//			throw new IllegalStateException("toolBarManager is null! Why has contributeToCoolBar(...) not been called?!");

		ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
		if (coolBarManager == null)
			throw new IllegalStateException("coolBarManager is null! Why has init(...) not been called?!"); //$NON-NLS-1$
		
		IMenuManager pulldownMenuManager = actionBars.getMenuManager();
		if (pulldownMenuManager == null)
			throw new IllegalStateException("pulldownMenuManager is null! Why has init(...) not been called?!"); //$NON-NLS-1$

		IMenuManager realPulldownMenuManager = (IMenuManager) ((SubContributionManager)pulldownMenuManager).getParent();

		ArticleContainerActionRegistry articleContainerActionRegistry;
		ArticleEditActionRegistry articleEditActionRegistry;
		try {
			articleContainerActionRegistry = ArticleContainerActionRegistry.sharedInstance();
			articleEditActionRegistry = ArticleEditActionRegistry.sharedInstance();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		// disable all ArticleContainerActions and calculate which ones must be visible
		for (Iterator<ActionDescriptor> itAC = articleContainerActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = itAC.next();
			IArticleContainerAction action = (IArticleContainerAction) actionDescriptor.getAction();
			IXContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (action != null) {
				action.setEnabled(false);
	
				if (activeArticleContainerEditorComposite == null)
					actionDescriptor.setVisible(false);
				else
					actionDescriptor.setVisible(action.calculateVisible());
			}
			else if (contributionItem != null) {
				contributionItem.setEnabled(false);

				if (activeArticleContainerEditorComposite == null)
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

				if (activeArticleContainerEditorComposite == null)
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
			IContributionItem contributionItem = (IContributionItem) realPulldownMenuManager.find(EDIT_MENU_ID);
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

		// contribute to the coolbar
		articleContainerActionRegistry.contributeToCoolBar(coolBarManager);
		articleEditActionRegistry.contributeToCoolBar(coolBarManager);

		calculateArticleContainerActionsEnabled();

		if (activeSegmentEdit != null)
			calculateArticleEditActionsEnabled(activeSegmentEdit.getArticleSelections());

		getActionBars().updateActionBars();
	}
	

	/**
	 * This method is called by {@link ArticleEditAction#run()}. It iterates all
	 * {@link ArticleSelection}s and calls the method
	 * {@link IArticleEditActionDelegate#run(IArticleEditAction, ArticleSelection)} on all
	 * associated delegates.
	 */
	protected void articleEditActionDelegatesRun(IArticleEditAction action)
	{
		if (activeSegmentEdit == null)
			throw new IllegalStateException("No activeSegmentEdit set!"); //$NON-NLS-1$

		for (Iterator<ArticleSelection> it = activeSegmentEdit.getArticleSelections().iterator(); it.hasNext(); ) {
			ArticleSelection selection = it.next();
			ArticleEdit articleEdit = selection.getArticleEdit();
			IArticleEditActionDelegate delegate = articleEdit.getArticleEditFactory().getArticleEditActionDelegate(action.getId());
			if (delegate == null) {
				logger.info(
						"No IArticleEditActionDelegate registered for articleEditFactory.productTypeClass=\"" + //$NON-NLS-1$
						articleEdit.getArticleEditFactory().getProductTypeClass() +
						"\" articleEditFactory.articleContainerClass=\"" + //$NON-NLS-1$
						articleEdit.getArticleEditFactory().getArticleContainerClass() +
						"\" articleEditFactory.segmentTypeClass=\"" + //$NON-NLS-1$
						articleEdit.getArticleEditFactory().getSegmentTypeClass() +
						"\" articleEditActionID=\"" + action.getId() +"\""); //$NON-NLS-1$ //$NON-NLS-2$
//				throw new IllegalStateException("No IArticleEditActionDelegate registered for articleEditActionID=" + action.getId());
			}
			else
				delegate.run(action, selection);
		}
	}

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

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager)
			{
				try {
					ArticleContainerActionRegistry.sharedInstance().contributeToContextMenu(manager);
				} catch (EPProcessorException e) {
					throw new RuntimeException(e);
				}
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

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager)
			{
				try {
					ArticleContainerActionRegistry.sharedInstance().contributeToContextMenu(manager);
					if (activeSegmentEdit != null) {
						manager.add(new Separator(SEPARATOR_BETWEEN_ARTICLE_CONTAINER_ACTIONS_AND_ARTICLE_EDIT_ACTIONS));
						ArticleEditActionRegistry.sharedInstance().contributeToContextMenu(manager);
					}
				} catch (EPProcessorException e) {
					throw new RuntimeException(e);
				}
			}
		});

		menu = menuManager.createContextMenu(parent);
		parent.setMenu(menu);
		return menu;
	}
	
	private IPartListener2 partListener = new IPartListener2 () {
		public void partActivated(IWorkbenchPartReference partRef) {
			if (activeArticleContainerEditor != null &&
				(partRef.getPart(false) instanceof IEditorPart) &&
				(!activeArticleContainerEditor.equals(partRef.getPart(false))))
			{
				logger.debug("Part activated"); //$NON-NLS-1$
				removeContributions();
				activeArticleContainerEditor = null;
			}
		}
		public void partBroughtToTop(IWorkbenchPartReference arg0) {
		}
		public void partClosed(IWorkbenchPartReference arg0) {
		}
		public void partDeactivated(IWorkbenchPartReference partRef) {
		}
		public void partHidden(IWorkbenchPartReference arg0) {
		}
		public void partInputChanged(IWorkbenchPartReference arg0) {
		}
		public void partOpened(IWorkbenchPartReference arg0) {
		}
		public void partVisible(IWorkbenchPartReference arg0) {
		}
	};
	
	private IPerspectiveListener4 perspectiveListener = new PerspectiveAdapter() {
		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			if ((!(TradePerspective.ID_PERSPECTIVE.equals(perspective.getId()) || 
				 (QuickSalePerspective.ID_PERSPECTIVE.equals(perspective.getId())))))
			{
				logger.debug("Perspective activated");					 //$NON-NLS-1$
				removeContributions();				
				activeArticleContainerEditor = null;
			}
		}
	};
	
	private void removeContributions() {
		IActionBars2 actionBars = (IActionBars2) getActionBars();
		IMenuManager menuManager = actionBars.getMenuManager();
		ICoolBarManager coolBarManager = actionBars.getCoolBarManager();				
		try {
			ArticleContainerActionRegistry.sharedInstance().removeAllFromCoolBar(coolBarManager);
			ArticleEditActionRegistry.sharedInstance().removeAllFromCoolBar(coolBarManager);

			ArticleContainerActionRegistry.sharedInstance().removeAllFromMenuBar(localPulldownMenuManager);
			ArticleEditActionRegistry.sharedInstance().removeAllFromMenuBar(localPulldownMenuManager);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
	}
}
