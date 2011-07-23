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
package org.nightlabs.jfire.scripting.editor2d.ui;

import java.util.Collection;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.NameProvider;
import org.nightlabs.editor2d.ui.AbstractEditor;
import org.nightlabs.editor2d.ui.EditorContextMenuProvider;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.login.ui.part.LSDPartController;
import org.nightlabs.jfire.scripting.editor2d.ScriptEditor2DFactory;
import org.nightlabs.jfire.scripting.editor2d.ScriptRootDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.impl.ScriptEditor2DFactoryImpl;
import org.nightlabs.jfire.scripting.editor2d.util.VisibleScriptUtil;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractScriptingEditor
extends AbstractEditor
implements ControllablePart
{
	private static final Logger logger = Logger.getLogger(AbstractScriptingEditor.class);

	public AbstractScriptingEditor()
	{
		super();
		login();
		LSDPartController.sharedInstance().registerPart(this, new FillLayout());
//		getScriptResultProvider().addScriptResultsChangedListener(scriptResultChangedListener);
	}

	protected void login()
	{
		try {
			Login.getLogin();
		} catch (LoginException e) {
			logger.warn("User decided to work offline or there was an authentication problem!", e); //$NON-NLS-1$
//			throw new RuntimeException(e);
		}
	}

	@Override
	protected void init() {
		login();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		if (!Login.isLoggedIn())
			LSDPartController.sharedInstance().createPartControl(this, parent);
	}

	public void createPartContents(Composite parent)
	{
		super.createPartControl(parent);
		super.setFocus();
		clearScriptResults();
		assignAllScripts();
		getEditorActionBarContributor().getEditorZoomComboContributionItem().setZoomManager(getZoomManager());
	}

	public void assignVisibleScriptResults()
	{
		VisibleScriptUtil.assignVisibleScriptResults(getScriptRootDrawComponent(), getScriptResults());
	}

	public ScriptRootDrawComponent getScriptRootDrawComponent() {
		return (ScriptRootDrawComponent) getRootDrawComponent();
	}

	public void assignAllScripts()
	{
		assignScriptResults();
		assignVisibleScriptResults();
	}

	public void assignScriptResults()
	{
		if (logger.isDebugEnabled())
			logger.debug("assignScriptResults()"); //$NON-NLS-1$
		getScriptRootDrawComponent().assignScriptResults(
				getScriptResults());
	}

	private Map<ScriptRegistryItemID, Object> scriptID2Result = null;
	public void clearScriptResults() {
		scriptID2Result = null;
	}

	protected Object getScriptResult(ScriptRegistryItemID scriptID) {
		return getScriptResults().get(scriptID);
	}

	protected Map<ScriptRegistryItemID, Object> getScriptResults() {
		if (scriptID2Result == null) {
			scriptID2Result = getScriptResults(getScriptRootDrawComponent().getScriptRegistryItemIDs());
		}
		return scriptID2Result;
	}

	@Override
	protected EditPartFactory createEditPartFactory() {
  	return new ScriptingEditorEditPartFactory();
  }

  @Override
  protected NameProvider createNameProvider() {
		return new ScriptingEditor2DNameProvider();
	}

  @Override
  protected ContextMenuProvider createContextMenuProvider() {
  	return new EditorContextMenuProvider(getGraphicalViewer(), getActionRegistry());
  }

	@Override
  protected EditPartFactory createOutlineEditPartFactory() {
  	return new ScriptingEditorTreePartFactory(getFilterManager());
  }

	@Override
	protected Editor2DFactory createModelFactory() {
		return new ScriptEditor2DFactoryImpl();
	}

	protected ScriptEditor2DFactory getScriptEditor2DFactory() {
		return (ScriptEditor2DFactory) getModelFactory();
	}

	@Override
	public void setFocus()
	{
		// TODO: to avoid NullPointerException at org.eclipse.gef.ui.parts.GraphicalEditor.setFocus(GraphicalEditor.java:354)
	}

	protected abstract Map<ScriptRegistryItemID, Object> getScriptResults(Collection<ScriptRegistryItemID> scriptIDs);

//	protected IScriptResultChangedListener scriptResultChangedListener = new IScriptResultChangedListener(){
//		public void scriptResultsChanged(ScriptResultsChangedEvent event) {
//			clearScriptResults();
//			assignAllScripts();
//		}
//	};

//	public abstract IScriptResultProvider getScriptResultProvider();

//	@Override
//	protected Map<ScriptRegistryItemID, Object> getScriptResults(Collection<ScriptRegistryItemID> scriptIDs) {
//		return getScriptResultProvider().getScriptResults(scriptIDs);
//	}
//
//	protected boolean isScriptResultAvailable() {
//		return getScriptResultProvider().getSelectedObject() != null;
//	}
//
//	public boolean canDisplayPart() {
//		return isScriptResultAvailable();
//	}
}
