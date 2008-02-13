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
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleCreateEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleCreateListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ClientArticleSegmentGroups;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.CreateArticleEditEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.CreateArticleEditListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.IGeneralEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditArticleSelectionEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditArticleSelectionListener;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class GeneralEditorActionBarContributor
extends EditorActionBarContributor
{
	private static final Logger logger = Logger.getLogger(GeneralEditorActionBarContributor.class);

//	private GeneralEditor activeGeneralEditor = null;
	private IGeneralEditor activeGeneralEditor = null;
	private GeneralEditorComposite activeGeneralEditorComposite = null;
	private SegmentEdit activeSegmentEdit = null;

	public static final String SEPARATOR_BETWEEN_ARTICLE_CONTAINER_ACTIONS_AND_ARTICLE_EDIT_ACTIONS = "betweenArticleContainerActionsAndArticleEditActions"; //$NON-NLS-1$

	public GeneralEditorActionBarContributor()
	{
		RCPUtil.getActiveWorkbenchPage().addPartListener(partListener);
		RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(perspectiveListener);
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (activeGeneralEditor == targetEditor)
			return;

		if (activeGeneralEditorComposite != null && !activeGeneralEditorComposite.isDisposed()) {
			activeGeneralEditorComposite.removeActiveSegmentEditSelectionListener(activeSegmentEditSelectionListener);
			activeGeneralEditorComposite.removeDisposeListener(generalEditorCompositeDisposeListener);
		}

		activeGeneralEditor = (IGeneralEditor)targetEditor;
		activeGeneralEditorComposite = activeGeneralEditor == null ? null : activeGeneralEditor.getGeneralEditorComposite();

		if (activeGeneralEditorComposite != null && !activeGeneralEditorComposite.isDisposed()) {
			activeGeneralEditorComposite.setGeneralEditorActionBarContributor(this);
			activeGeneralEditorComposite.addActiveSegmentEditSelectionListener(activeSegmentEditSelectionListener);
			activeGeneralEditorComposite.addDisposeListener(generalEditorCompositeDisposeListener);
		}

		try {
			ArticleContainerActionRegistry.sharedInstance().setActiveGeneralEditorActionBarContributor(this);
			ArticleEditActionRegistry.sharedInstance().setActiveGeneralEditorActionBarContributor(this);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		activeSegmentEditSelected();
	}

	private DisposeListener generalEditorCompositeDisposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e)
		{
			GeneralEditorComposite gec = (GeneralEditorComposite) e.widget;
			if (gec == activeGeneralEditorComposite) // should be, because we remove the listeners when switching active, but secure is better
				setActiveEditor(null);
		}
	};

	public IGeneralEditor getActiveGeneralEditor()
	{
		return activeGeneralEditor;
	}
	
	public GeneralEditorComposite getActiveGeneralEditorComposite()
	{
		return activeGeneralEditorComposite;
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
//	 This listener is subscribed to the whole ClientArticleSegmentGroups and therefore not all Articles
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

		for (Iterator it = articleContainerActionRegistry.getActionDescriptors().iterator(); it.hasNext(); ) {
			ActionDescriptor actionDescriptor = (ActionDescriptor) it.next();
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
	protected void calculateArticleEditActionsEnabled(Set articleSelections)
	{
		ArticleEditActionRegistry articleEditActionRegistry;
		try {
			articleEditActionRegistry = ArticleEditActionRegistry.sharedInstance();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		for (Iterator itAC = articleEditActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = (ActionDescriptor) itAC.next();
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
		activeSegmentEdit = activeGeneralEditorComposite == null ? null : activeGeneralEditorComposite.getActiveSegmentEdit();
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

		ClientArticleSegmentGroups oldClientArticleSegmentGroups = oldActiveSegmentEdit == null ? null : oldActiveSegmentEdit.getClientArticleSegmentGroups();
		ClientArticleSegmentGroups clientArticleSegmentGroups = activeSegmentEdit == null ? null : activeSegmentEdit.getClientArticleSegmentGroups();

		if (oldClientArticleSegmentGroups != clientArticleSegmentGroups) {
			if (oldClientArticleSegmentGroups != null) {
				oldClientArticleSegmentGroups.removeArticleCreateListener(articleCreateListener);
				oldClientArticleSegmentGroups.removeArticleChangeListener(articleChangeListener);
			}
			if (clientArticleSegmentGroups != null) {
				clientArticleSegmentGroups.addArticleCreateListener(articleCreateListener);
				clientArticleSegmentGroups.addArticleChangeListener(articleChangeListener);
			}
		}

		contributeActions();
	}

//	/**
//	 * Because our dynamic tool management doesn't work with the normal toolbar, we are forced to work
//	 * with the {@link ICoolBarManager} (which is in reality a {@link SubCoolBarManager}) and handle
//	 * our toolBar ourselves.
//	 *
//	 * @see #contributeToCoolBar(ICoolBarManager)
//	 */
//	private IToolBarManager toolBarManager;

//	/**
//	 * This implementation searches for our self-managed toolbar in the
//	 * parent of the passed <code>coolBarManager</code> (i.e. the main CoolBarManager).
//	 * If it was found (because Eclipse RCP dumps and restores its workbench layout),
//	 * it is simply assigned to {@link #toolBarManager}; otherwise a new one is
//	 * created. The real contribution does not happen here, but in {@link #contributeActions()}.
//	 *
//	 * @param coolBarManager This is in reality a {@link SubCoolBarManager} which allows
//	 *		to access the real CoolBarManager via {@link SubContributionManager#getParent()}.
//	 *
//	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToCoolBar(org.eclipse.jface.action.ICoolBarManager)
//	 */
//	public void contributeToCoolBar(ICoolBarManager coolBarManager)
//	{
//		ToolBarContributionItem toolBarContributionItem = null;
//
//		((SubCoolBarManager)coolBarManager).setVisible(true);
//		IXContributionItem[] items = ((SubCoolBarManager)coolBarManager).getParent().getItems();
//		String toolBarID = GeneralEditorActionBarContributor.class.getName();
//		for (int i = 0; i < items.length; ++i) {
//			if (toolBarID.equals(items[i].getId())) {
//				toolBarContributionItem = (ToolBarContributionItem) items[i];
//				toolBarManager = toolBarContributionItem.getToolBarManager();
//				break;
//			}
//		}
//
//		if (toolBarContributionItem == null) {
//			toolBarManager = new ToolBarManager();
//			toolBarContributionItem = new ToolBarContributionItem(toolBarManager, toolBarID);
//			coolBarManager.add(toolBarContributionItem);
//		}
//	}

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
		for (Iterator itAC = articleContainerActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = (ActionDescriptor) itAC.next();
			IArticleContainerAction action = (IArticleContainerAction) actionDescriptor.getAction();
			IXContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (action != null) {
				action.setEnabled(false);
	
				if (activeGeneralEditorComposite == null)
					actionDescriptor.setVisible(false);
				else
					actionDescriptor.setVisible(action.calculateVisible());
			}
			else if (contributionItem != null) {
				contributionItem.setEnabled(false);

				if (activeGeneralEditorComposite == null)
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
		for (Iterator itAC = articleEditActionRegistry.getActionDescriptors().iterator(); itAC.hasNext(); ) {
			ActionDescriptor actionDescriptor = (ActionDescriptor) itAC.next();
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

				if (activeGeneralEditorComposite == null)
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
			localPulldownMenuManager = (IMenuManager) realPulldownMenuManager.find(GeneralEditorActionBarContributor.class.getPackage().getName());

			if (localPulldownMenuManager == null) {
				localPulldownMenuManager = new MenuManager(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.GeneralEditorActionBarContributor.pulldownMenu.text"), GeneralEditorActionBarContributor.class.getPackage().getName()); //$NON-NLS-1$
				realPulldownMenuManager.insertAfter(
						// IWorkbenchActionConstants.MB_ADDITIONS,
						IWorkbenchActionConstants.M_FILE,
						localPulldownMenuManager);
			}
		}
		localPulldownMenuManager.removeAll();
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

		for (Iterator it = activeSegmentEdit.getArticleSelections().iterator(); it.hasNext(); ) {
			ArticleSelection selection = (ArticleSelection) it.next();
			ArticleEdit articleEdit = selection.getArticleEdit();
			IArticleEditActionDelegate delegate = articleEdit.getArticleEditFactory().getArticleEditActionDelegate(action.getId());
			if (delegate == null) {
				logger.info(
						"No IArticleEditActionDelegate registered for articleEditFactory.productTypeClass=\"" + //$NON-NLS-1$
						articleEdit.getArticleEditFactory().getProductTypeClass() +
						"\" articleEditFactory.segmentContext=\"" + //$NON-NLS-1$
						articleEdit.getArticleEditFactory().getSegmentContext() +
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

//	/**
//	 * We must use a separate MenuManager for each Control
//	 * because the context menu doesn't work otherwise anymore after one Editor has been closed.
//	 * After taking a short look at the source, it seems to me, a MenuManager is not meant
//	 * to be used for multiple menus...
//	 * <p>
//	 * The entries are deleted from the Map when an Editor is closed via
//	 * the {@link #generalEditorCompositeDisposeListener}.
//	 * </p>
//	 * <p>
//	 * key: GeneralEditorComposite generalEditorComposite<br/>
//	 * value: Map {<br/>
//	 *		key: Control parent<br/>
//	 *		value: MenuManager articleEditContextMenuManager
//	 * }
//	 * </p>
//	 */
//	private Map articleEditContextMenuManagers = new HashMap();

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

//	protected MenuManager getArticleEditContextMenuManager(SegmentEdit segmentEdit, Control parent)
//	{
//		GeneralEditorComposite gec = segmentEdit.getGeneralEditorComposite();
//		Map m = (Map) articleEditContextMenuManagers.get(gec);
//		if (m == null) {
//			m = new HashMap();
//			gec.addDisposeListener(generalEditorCompositeDisposeListener);
//			articleEditContextMenuManagers.put(gec, m);
//		}
//		MenuManager res = (MenuManager) m.get(parent);
//		if (res == null) {
//			res = new MenuManager();
//			res.setRemoveAllWhenShown(true);
//			res.addMenuListener(new IMenuListener() {
//				public void menuAboutToShow(IMenuManager manager)
//				{
//					if (activeSegmentEdit != null) {
//						try {
//							ArticleEditActionRegistry.sharedInstance().contributeToContextMenu(manager);
//						} catch (EPProcessorException e) {
//							throw new RuntimeException(e);
//						}
//					}
//				}
//			});
//			m.put(parent, res);
//		}
//		return res;
//	}
	
	private IPartListener2 partListener = new IPartListener2 () {
		public void partActivated(IWorkbenchPartReference partRef) {
			if (activeGeneralEditor != null &&
				(partRef.getPart(false) instanceof IEditorPart) &&
				(!activeGeneralEditor.equals(partRef.getPart(false))))
			{
				logger.debug("Part activated"); //$NON-NLS-1$
				IActionBars2 actionBars = (IActionBars2) getActionBars();
				ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
				try {
					ArticleContainerActionRegistry.sharedInstance().removeAllFromCoolBar(coolBarManager);
					ArticleEditActionRegistry.sharedInstance().removeAllFromCoolBar(coolBarManager);
				} catch (EPProcessorException e) {
					throw new RuntimeException(e);
				}
				activeGeneralEditor = null;
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
			if (
//					activeGeneralEditor != null
//				 && System.identityHashCode(activeGeneralEditor) != System.identityHashCode(RCPUtil.getActiveWorkbenchPage().getActiveEditor())
//				 &&	(!activeGeneralEditor.equals(RCPUtil.getActiveWorkbenchPage().getActiveEditor()))
//				 &&
				 (!(TradePerspective.ID_PERSPECTIVE.equals(perspective.getId()) || (QuickSalePerspective.ID_PERSPECTIVE.equals(perspective.getId()))))
				)
			{
				logger.debug("Perspective activated");					 //$NON-NLS-1$
				IActionBars2 actionBars = (IActionBars2) getActionBars();
				ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
				try {
					ArticleContainerActionRegistry.sharedInstance().removeAllFromCoolBar(coolBarManager);
					ArticleEditActionRegistry.sharedInstance().removeAllFromCoolBar(coolBarManager);
				} catch (EPProcessorException e) {
					throw new RuntimeException(e);
				}
				activeGeneralEditor = null;
			}
		}
	};
}
