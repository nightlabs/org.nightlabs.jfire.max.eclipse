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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.ArticleContainer;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class HeaderComposite extends XComposite
{
	private ArticleContainerEditorComposite articleContainerEditorComposite;
	private ArticleContainer articleContainer;

	public HeaderComposite(Composite parent, ArticleContainerEditorComposite articleContainerEditorComposite, ArticleContainer articleContainer)
	{
		super(parent, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
		this.articleContainerEditorComposite = articleContainerEditorComposite;
		this.articleContainer = articleContainer;
		
//		setBackground(DEFAULT_BG_COLOR);
//		setFont(DEFAULT_FONT);
//		label = new Label(this, SWT.NONE);
	}

	public static final Color DEFAULT_BG_COLOR = new Color(null, 255, 255, 255);
	protected Color bgColor = DEFAULT_BG_COLOR;
	public void setBgColor(Color c) {
		bgColor = c;
	}
	
	public static Font DEFAULT_FONT = new Font(null, "Arial", 8, SWT.BOLD); //$NON-NLS-1$ // TODO shouldn't these font infos be read from somewhere (e.g. other composites or a config?)
	protected Font font = DEFAULT_FONT;
	@Override
	public void setFont(Font f) {
		font = f;
	}

//	protected Label label;
//	public void setHeaderText(String text)
//	{
//		label.setText(" "+text);
//		label.setFont(font);
//		label.setBackground(bgColor);
//	}
	
	/**
	 * @return Returns the articleContainer.
	 */
	public ArticleContainer getArticleContainer()
	{
		return articleContainer;
	}
	/**
	 * @return Returns the articleContainerEditorComposite.
	 */
	public ArticleContainerEditorComposite getGeneralEditorComposite()
	{
		return articleContainerEditorComposite;
	}

	/**
	 * This method adds context menus recursively.
	 */
	protected void createArticleContainerContextMenu()
	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run()
			{
				ArticleContainerEditorComposite gec = getGeneralEditorComposite();
				if (gec != null && !gec.isDisposed())
					createArticleContainerContextMenu(gec, HeaderComposite.this);
			}
		});
	}

	private static void createArticleContainerContextMenu(ArticleContainerEditorComposite gec, Composite c)
	{
		gec.createArticleContainerContextMenu(c);

		Control[] children = c.getChildren();
		for (int i = 0; i < children.length; ++i) {
			Control child = children[i];
			if (child instanceof Composite)
				createArticleContainerContextMenu(gec, (Composite) child);
			else
				gec.createArticleContainerContextMenu(child);
		}
	}

	public void refresh()
	{
		
	}
}
