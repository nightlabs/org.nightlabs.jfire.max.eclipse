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

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEditFactory;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;

public class ArticleEditAction extends Action implements IArticleEditAction
{
	private Logger logger = Logger.getLogger(ArticleEditAction.class);
	
	public ArticleEditAction()
	{
//		super("", AS_PUSH_BUTTON); //$NON-NLS-1$
	}

	public ArticleEditAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	public ArticleEditAction(String text, int style) {
		super(text, style);
	}

	public ArticleEditAction(String text) {
		super(text);
	}

	private ArticleEditActionRegistry articleEditActionRegistry;

	@Override
	public void init(
			ArticleEditActionRegistry articleEditActionRegistry)
	{
		this.articleEditActionRegistry = articleEditActionRegistry;
	}

	@Override
	public ArticleEditActionRegistry getArticleEditActionRegistry()
	{
		return articleEditActionRegistry;
	}

	@Override
	public boolean calculateVisible()
	{
		// Find out whether there is any delegate for this action in the active SegmentEdit

		// First, check if there is an active SegmentEdit at all.
		ArticleContainerEdit edit = getArticleContainerEdit();
		if (edit == null)
			return false;
		
		SegmentEdit activeSegmentEdit = edit.getActiveSegmentEdit();
		if (activeSegmentEdit == null)
			return false;

		// Now iterate and search for a delegate.
		boolean visible = false;
		for (Iterator<ArticleEdit> itAE = activeSegmentEdit.getArticleEdits().iterator(); itAE.hasNext(); ) {
			ArticleEdit articleEdit = itAE.next();
			if (articleEdit.getArticleEditFactory().getArticleEditActionDelegate(this.getId()) != null)
				visible = true;
		}
		return visible;
	}

	@Override
	public boolean calculateEnabled(Set<ArticleSelection> articleSelections)
	{
		boolean enabled = true;

		// Iterate all ArticleSelections.
		for (Iterator<ArticleSelection> itAS = articleSelections.iterator(); itAS.hasNext(); ) {
			ArticleSelection articleSelection = itAS.next();
			ArticleEditFactory articleEditFactory = articleSelection.getArticleEdit().getArticleEditFactory();

			// get the ActionDelegate for the ArticleEdit
			IArticleEditActionDelegate delegate = articleEditFactory.getArticleEditActionDelegate(this.getId());
			if (delegate == null) // if there's no delegate existing, we disable the action
				enabled = false;
			else {
				if (!delegate.calculateEnabled(articleSelection, articleSelections))
					enabled = false;
			}

			if (!enabled)
				break;
		} // for (Iterator itAS = articleSelections.iterator(); itAS.hasNext(); ) {

		return enabled;
	}

	/**
	 * This implementation calls
	 * {@link ArticleContainerEditorActionBarContributor#articleEditActionDelegatesRun(IArticleEditAction)}.
	 *
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run()
	{
//		ArticleContainerEditorActionBarContributor contributor = getArticleEditActionRegistry().getActiveArticleContainerEditorActionBarContributor();
//		if (contributor == null)
//			throw new IllegalStateException("No activeArticleContainerEditorActionBarContributor set in ArticleEditActionRegistry!"); //$NON-NLS-1$
//
		articleEditActionDelegatesRun(this);
	}
	
	/**
	 * This method is called by {@link ArticleEditAction#run()}. It iterates all
	 * {@link ArticleSelection}s and calls the method
	 * {@link IArticleEditActionDelegate#run(IArticleEditAction, ArticleSelection)} on all
	 * associated delegates.
	 */
	protected void articleEditActionDelegatesRun(IArticleEditAction action)
	{
		ArticleContainerEdit edit = getArticleContainerEdit(); 
		if (edit == null)
			throw new IllegalStateException("No activeArticleContainerEdit set!"); //$NON-NLS-1$
		if (edit.getActiveSegmentEdit() == null)
			throw new IllegalStateException("No activeSegmentEdit set!"); //$NON-NLS-1$
		
		for (Iterator<ArticleSelection> it = edit.getActiveSegmentEdit().getArticleSelections().iterator(); it.hasNext(); ) {
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
	 * This is a convenience method for: 
	 * <pre>
	 * getArticleEditActionRegistry().getActiveArticleContainerEdit()
	 * </pre>
	 * However you should always use this method.
	 * 
	 * @return The active {@link ArticleContainerEdit}. Note that this might be <code>null</code>.
	 */
	protected ArticleContainerEdit getArticleContainerEdit() {
		return getArticleEditActionRegistry().getActiveArticleContainerEdit();
	}
	
	/**
	 * This method attempts to get the {@link ArticleContainer} from the active {@link ArticleContainerEdit}.
	 * The edit might be <code>null</code> and this method will also return <code>null</code> then.
	 *   
	 * @return The {@link ArticleContainer} of the {@link ArticleContainerEdit} this action associated with or null, if there is currently no edit active.
	 */
	protected ArticleContainer getArticleContainer() {
		ArticleContainerEdit articleContainerEdit = getArticleContainerEdit();
		if (articleContainerEdit == null)
			return null;

		return articleContainerEdit.getArticleContainer();
	}
	
	/**
	 * This method attempts to get the {@link ArticleContainerID} from the active {@link ArticleContainerEdit}.
	 * The edit might be <code>null</code> and this method will also return <code>null</code> then.
	 *   
	 * @return The {@link ArticleContainerID} of the {@link ArticleContainerEdit} this action associated with or null, if there is currently no edit active.
	 */
	protected ArticleContainerID getArticleContainerID() {
		ArticleContainerEdit articleContainerEdit = getArticleContainerEdit();
		if (articleContainerEdit == null)
			return null;
		
		return articleContainerEdit.getArticleContainerID();
	}
	
}
