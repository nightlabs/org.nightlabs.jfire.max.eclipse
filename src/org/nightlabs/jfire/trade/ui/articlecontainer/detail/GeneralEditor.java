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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.notification.NotificationEvent;

/**
 * This editor is the frame for editing <tt>Order</tt>s, <tt>Offer</tt>s,
 * <tt>Invoice</tt>s and <tt>Delivery</tt>s. It delegates all work to the
 * {@link GeneralEditorComposite}.
 * 
 * @author Marco Schulze - marco at nightlabs dot de
 * 
 * @author Fitas Amine - fitas at nightlabs dot de
 * 
 */

public class GeneralEditor 
extends EditorPart 
//extends LSDEditorPart
implements IGeneralEditor 
{
	public static final String ID_EDITOR = GeneralEditor.class.getName();

	private static final Logger logger = Logger.getLogger(GeneralEditor.class);

	private GeneralEditorComposite generalEditorComposite;
	private GeneralEditorInput input;
	private static int numEditorsOpen = 0;
	private static boolean partInitialized = false;

	public GeneralEditor() {
		registerActivatePartListener();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException 
	{
		if (!(input instanceof GeneralEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be an instance of GeneralEditorInput but is " + input); //$NON-NLS-1$

		this.input = (GeneralEditorInput) input;

		setSite(site);
		setInput(input);

		setPartName(input.getName());
		ImageDescriptor img = input.getImageDescriptor();
		if (img != null)
			setTitleImage(img.createImage());
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
//	public void createPartContents(Composite parent) 
//	{
		generalEditorComposite = new GeneralEditorComposite(getSite(), parent,
				input);
	}

	public GeneralEditorComposite getGeneralEditorComposite() {
		return generalEditorComposite;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	protected synchronized static void registerActivatePartListener() {
		if (partInitialized)
			return;

		RCPUtil.getActiveWorkbenchPage().addPartListener(partListener);
		partInitialized = true;
	}

	private static ActivateListener partListener = new ActivateListener();

	protected static class ActivateListener implements IPartListener {

		private void fireEvent(GeneralEditor generalEditor) {

			ArticleContainerID articleContainerID = null;

			if (generalEditor != null && 
					generalEditor.getEditorInput() != null) 
			{
				GeneralEditorInput input = (GeneralEditorInput) generalEditor.getEditorInput();
				articleContainerID = input.getArticleContainerID();
			}
			if (logger.isDebugEnabled())
				logger.debug("ActivateListener.fireEvent: entered for " + articleContainerID);

			NotificationEvent event = new NotificationEvent(this,
					TradePlugin.ZONE_SALE, articleContainerID,
					ArticleContainer.class);

			SelectionManager.sharedInstance().notify(event);
		}

		public void partActivated(final IWorkbenchPart part) {
			if (part instanceof GeneralEditor)
				fireEvent((GeneralEditor) part);
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {
		}

		@Override
		public void partClosed(final IWorkbenchPart part) {

			
			if(Login.sharedInstance().getLoginState() == LoginState.ABOUT_TO_LOG_OUT)
			{
				if (RCPUtil.getActiveWorkbenchPage() != null)
					RCPUtil.getActiveWorkbenchPage().removePartListener(
							partListener);
				
				partInitialized = false;
				
				return;
			}

			if (!(part instanceof GeneralEditor))
				return;

			GeneralEditor generalEditor = (GeneralEditor) part;

			if (numEditorsOpen <= 0)
				throw new IllegalStateException(
						"Closing more editors as have been opened!!! How can this happen! generalEditor.editorInput: "
						+ generalEditor.getEditorInput());

			--numEditorsOpen;

			if (numEditorsOpen == 0  && RCPUtil.getActiveWorkbenchPage() != null) {
				fireEvent(null);

				if (RCPUtil.getActiveWorkbenchPage() != null)
					RCPUtil.getActiveWorkbenchPage().removePartListener(
							partListener);

				partInitialized = false;
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPart part) {
		}

		@Override
		public void partOpened(final IWorkbenchPart part) {
			if (!(part instanceof GeneralEditor))
				return;

			//if (logger.isDebugEnabled())
			logger.debug("Part Opened !!!!");


			numEditorsOpen++;
			registerActivatePartListener();
			//	fireEvent((GeneralEditor) part);
		}
	}

	// /**
	// * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	// */
	// public void dispose()
	// {
	// LOGGER.debug("dispose() entered. generalEditorComposite.isDisposed()=" +
	// generalEditorComposite.isDisposed());

	// // TODO the following line should NOT be necessary, but the dispose
	// method of
	// // our composite is never called.
	// LOGGER.debug("manually calling generalEditorComposite.dispose()");
	// generalEditorComposite.dispose();

	// LOGGER.debug("dispose() calling super.dispose()");
	// super.dispose();
	// }
}
