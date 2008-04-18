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


import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ArticleContainer;
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
implements IGeneralEditor
{
	public static final String ID_EDITOR = GeneralEditor.class.getName();

	private static final Logger logger = Logger.getLogger(GeneralEditor.class);

	private GeneralEditorComposite generalEditorComposite;
	private GeneralEditorInput input;
	private boolean focusReleased = true;
	private static int numEditorsOpen = 0;
	private static boolean partInitialized = false;

	public GeneralEditor()
	{
		registerActivatePartListener();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException
	{

		if (!(input instanceof GeneralEditorInput))
			throw new PartInitException("Invalid Input: Must be an instance of GeneralEditorInput but is "+input); //$NON-NLS-1$

		this.input = (GeneralEditorInput) input;

		setSite(site);
		setInput(input);

		setPartName(input.getName());
		ImageDescriptor img = input.getImageDescriptor();
		if (img != null)
			setTitleImage(img.createImage());

//		getSite().setSelectionProvider(new ISelectionProvider() {
//		private LinkedList listeners = new LinkedList();

//		public void addSelectionChangedListener(ISelectionChangedListener listener)
//		{
//		listeners.add(listener);
//		}

//		public ISelection getSelection()
//		{
//		// TODO Auto-generated method stub
//		return null;
//		}

//		public void removeSelectionChangedListener(ISelectionChangedListener listener)
//		{
//		listeners.remove(listener);
//		}

//		public void setSelection(ISelection selection)
//		{
//		// TODO Auto-generated method stub
//		System.out.println("");
//		}

//		});
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
//		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(
//		TradePlugin.class, "../../../../../icons/submit16.gif");
//		setTitleImage(imageDescriptor.createImage());
		generalEditorComposite = new GeneralEditorComposite(getSite(), parent, input);
	}

	public GeneralEditorComposite getGeneralEditorComposite()
	{
		return generalEditorComposite;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	protected synchronized static void registerActivatePartListener()
	{
		if(partInitialized)
			return;

		//RCPUtil.getActiveWorkbenchPage().addPartListener(new ActivateListener());

		RCPUtil.getActiveWorkbenchPage().addPartListener(partListener);
		
		partInitialized = true;
	}




	private static ActivateListener partListener = new ActivateListener();


	protected static class ActivateListener implements IPartListener {
		public void partActivated(final IWorkbenchPart part) {
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

			if (editor == null)
				return;


			if (editor instanceof GeneralEditor) {
				GeneralEditor ge = (GeneralEditor) editor;
				ArticleContainer ac = ge.getGeneralEditorComposite().getArticleContainer();

				if (ac == null)
					return;

				NotificationEvent event = new NotificationEvent(
						GeneralEditor.class, TradePlugin.ZONE_SALE, 
						JDOHelper.getObjectId(ac));


				SelectionManager.sharedInstance().notify(event);

				if (logger.isDebugEnabled()) {
					logger.debug("partActivated: " + ge.getTitle());
				}
			}
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {
		}
		@Override
		public void partClosed(final IWorkbenchPart part) {

			if(numEditorsOpen != 0)
				numEditorsOpen--;

			if(numEditorsOpen == 0)
			{
				RCPUtil.getActiveWorkbenchPage().removePartListener(partListener);
				partInitialized = false;
			}


		}
		@Override
		public void partDeactivated(final IWorkbenchPart part) {
		}
		@Override
		public void partOpened(final IWorkbenchPart part) {
			//	MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Opened", "test");
			numEditorsOpen++;
			registerActivatePartListener();
		}
	}

//	/**
//	* @see org.eclipse.ui.part.WorkbenchPart#dispose()
//	*/
//	public void dispose()
//	{
//	LOGGER.debug("dispose() entered. generalEditorComposite.isDisposed()=" + generalEditorComposite.isDisposed());

//	// TODO the following line should NOT be necessary, but the dispose method of
//	// our composite is never called.
//	LOGGER.debug("manually calling generalEditorComposite.dispose()");
//	generalEditorComposite.dispose();

//	LOGGER.debug("dispose() calling super.dispose()");
//	super.dispose();
//	}
}
