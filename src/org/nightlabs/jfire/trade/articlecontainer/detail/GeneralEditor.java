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

package org.nightlabs.jfire.trade.articlecontainer.detail;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * This editor is the frame for editing <tt>Order</tt>s, <tt>Offer</tt>s,
 * <tt>Invoice</tt>s and <tt>Delivery</tt>s. It delegates all work to the
 * {@link GeneralEditorComposite}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class GeneralEditor 
extends EditorPart
implements IGeneralEditor
{
	public static final String ID_EDITOR = GeneralEditor.class.getName();

	private GeneralEditorComposite generalEditorComposite;
	private GeneralEditorInput input;

	public GeneralEditor()
	{
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs()
	{
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
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
//		
//		getSite().setSelectionProvider(new ISelectionProvider() {
//			private LinkedList listeners = new LinkedList();
//			
//			public void addSelectionChangedListener(ISelectionChangedListener listener)
//			{
//				listeners.add(listener);
//			}
//
//			public ISelection getSelection()
//			{
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			public void removeSelectionChangedListener(ISelectionChangedListener listener)
//			{
//				listeners.remove(listener);
//			}
//
//			public void setSelection(ISelection selection)
//			{
//				// TODO Auto-generated method stub
//				System.out.println("");
//			}
//			
//		});
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
//		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(
//				TradePlugin.class, "../../../../../icons/submit16.gif");
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
	public void setFocus()
	{
	}

//	/**
//	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
//	 */
//	public void dispose()
//	{
//		LOGGER.debug("dispose() entered. generalEditorComposite.isDisposed()=" + generalEditorComposite.isDisposed());
//
//		// TODO the following line should NOT be necessary, but the dispose method of
//		// our composite is never called.
//		LOGGER.debug("manually calling generalEditorComposite.dispose()");
//		generalEditorComposite.dispose();
//
//		LOGGER.debug("dispose() calling super.dispose()");
//		super.dispose();
//	}
}
