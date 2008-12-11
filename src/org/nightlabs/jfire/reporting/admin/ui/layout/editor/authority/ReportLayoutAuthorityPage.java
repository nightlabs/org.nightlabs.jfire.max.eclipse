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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.admin.ui.layout.editor.authority;

import org.eclipse.birt.report.designer.ui.editors.IReportEditorPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthorityPage;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthoritySection;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.base.admin.ui.editor.authority.InheritedSecuringAuthorityResolver;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.ReportLayoutEntityEditor;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;

/**
 * A page for the Report Designer that lets the edit the
 * {@link ReportTextPartConfiguration} for the current 
 * {@link ReportRegistryItem}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportLayoutAuthorityPage
extends ReportLayoutEntityEditor
implements IReportEditorPage
{

	public static final String ID_PAGE = ReportLayoutAuthorityPage.class.getName();

	@Override
	protected IFormPage createFormPage(EntityEditor entityEditor) {
		return new AbstractAuthorityPage(entityEditor, getId()) {

			@Override
			protected AbstractAuthoritySection createAuthoritySection(Composite parent) {
				return new AbstractAuthoritySection(this, parent) {
					@Override
					protected InheritedSecuringAuthorityResolver createInheritedSecuringAuthorityResolver() {
						return null;
					}
					@Override
					public void setPageController(IEntityEditorPageController pageController) {
						setAuthorityPageControllerHelper(((AuthorityPageController)pageController).getAuthorityPageControllerHelper());
					}
					
				};
			}

			@Override
			protected AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
				return ((AuthorityPageController)getPageController()).getAuthorityPageControllerHelper();
			}
			
		};
	}

	@Override
	protected IEntityEditorPageController createPageController(EntityEditor entityEditor) {
		return new AuthorityPageController(entityEditor);
	}

	@Override
	public String getId() {
		return ID_PAGE;
	}
	
}
