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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * This editor is the frame for editing <tt>Order</tt>s, <tt>Offer</tt>s,
 * <tt>Invoice</tt>s and <tt>Delivery</tt>s. It delegates all work to the
 * {@link ArticleContainerEditorComposite}.
 * 
 * @author Marco Schulze - marco at nightlabs dot de
 * 
 * @author Fitas Amine - fitas at nightlabs dot de
 * 
 */
public class ArticleContainerEditor 
extends AbstractArticleContainerEditor  
{
	public static final String ID_EDITOR = ArticleContainerEditor.class.getName();

	private ArticleContainerEditorComposite articleContainerEditorComposite;
	private ArticleContainerEditorInput input;
	
	public ArticleContainerEditor() {}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException 
	{
		if (!(input instanceof ArticleContainerEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be an instance of ArticleContainerEditorInput but is " + input); //$NON-NLS-1$

		this.input = (ArticleContainerEditorInput) input;

		setSite(site);
		setInput(input);

		setPartName(input.getName());
		ImageDescriptor img = input.getImageDescriptor();
		if (img != null)
			setTitleImage(img.createImage());
	}

	@Override
	public void createPartControl(Composite parent) {
//	public void createPartContents(Composite parent) 
//	{
		articleContainerEditorComposite = new ArticleContainerEditorComposite(getSite(), parent,
				input);
	}

	public ArticleContainerEditorComposite getArticleContainerEditorComposite() {
		return articleContainerEditorComposite;
	}

}
