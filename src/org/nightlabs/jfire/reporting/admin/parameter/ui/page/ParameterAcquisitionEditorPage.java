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

package org.nightlabs.jfire.reporting.admin.parameter.ui.page;

import java.util.EventObject;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.designer.internal.ui.editors.FileReportProvider;
import org.eclipse.birt.report.designer.ui.editors.IReportEditorPage;
import org.eclipse.birt.report.designer.ui.editors.IReportProvider;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.reporting.admin.parameter.ui.ReportParameterEditor;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueAcquisitionSetupEditPart;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.action.IActionUpdateDelegate;

/**
 * A page for the Report Designer that lets the user preview
 * his reports within the JFire server environment.
 * 
 * It uses an adapted {@link ReportViewer} to
 * view ReportLayouts.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ParameterAcquisitionEditorPage
extends ReportParameterEditor
implements IReportEditorPage, IActionUpdateDelegate
{

	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(ParameterAcquisitionEditorPage.class);
	
	public static final String ID_PAGE = ParameterAcquisitionEditorPage.class.getName();
	
	
	private Object model;
	
	private IReportProvider provider;
	
	private XComposite wrapper;
//	private XComposite topWrapper;
	
	private int staleType;
	private Control control;
	private FormEditor editor;
	
	private int index;
	
	@Override
	public void createPartControl(Composite arg0)
	{
		wrapper = new XComposite(arg0, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
		wrapper.setLayout(new FillLayout());
		super.createPartControl(wrapper);
		control = wrapper;
		getCommandStack().addCommandStackListener(new CommandStackListener() {
			public void commandStackChanged(EventObject event)
			{
				if (getEditor() != null)
					getEditor().editorDirtyStateChanged();
			}
		});
//		ModelNotificationManager.sharedInstance().addPropertyChangeListener(null, new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent evt) {
//				getEditor().editorDirtyStateChanged();
//			}
//		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getPartControl()
	 */
	public Control getPartControl() {
//		logger.debug("getPartControl returning "+control);
		return control;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	public void initialize(FormEditor editor) {
		logger.debug("initialize "+editor); //$NON-NLS-1$
		this.editor = editor;
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.editors.IReportEditorPage#onBroughtToTop(org.eclipse.birt.report.designer.ui.editors.IReportEditorPage)
	 */
	public boolean onBroughtToTop(IReportEditorPage prePage) {
		logger.debug("On brought to top "); //$NON-NLS-1$
		List rootChildren = getRootEditPart().getChildren();
		if (rootChildren.size() > 0 ) {
			if (rootChildren.get(0) instanceof ValueAcquisitionSetupEditPart)
				((ValueAcquisitionSetupEditPart)rootChildren.get(0)).clearAndRefresh();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#canLeaveThePage()
	 */
	public boolean canLeaveThePage() {
		logger.debug("Can leave page"); //$NON-NLS-1$
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getEditor()
	 */
	public FormEditor getEditor() {
		logger.debug("getEditor returning "+editor); //$NON-NLS-1$
		return editor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.editors.IReportEditorPage#getStaleType()
	 */
	public int getStaleType() {
		logger.debug("getStaleType returning "+staleType); //$NON-NLS-1$
		return staleType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.editors.IReportEditorPage#markPageStale(int)
	 */
	public void markPageStale(int type) {
		logger.debug("setStaleType to "+type); //$NON-NLS-1$
		staleType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getId()
	 */
	public String getId() {
		logger.debug("getId returning "+ID_PAGE); //$NON-NLS-1$
		return ID_PAGE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getIndex()
	 */
	public int getIndex() {
		logger.debug("getIndex returning "+index); //$NON-NLS-1$
		return index;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getManagedForm()
	 */
	public IManagedForm getManagedForm() {
		logger.debug("getManagedForm returning "+null); //$NON-NLS-1$
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#isActive()
	 */
	public boolean isActive() {
		logger.debug("isActive returning "+false); //$NON-NLS-1$
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#isEditor()
	 */
	public boolean isEditor() {
		logger.debug("isEditor returning "+false); //$NON-NLS-1$
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#selectReveal(java.lang.Object)
	 */
	public boolean selectReveal(Object object) {
		logger.debug("selectReveal returning "+false); //$NON-NLS-1$
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#setActive(boolean)
	 */
	public void setActive(boolean active) {
		logger.debug("setActive "+active); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#setIndex(int)
	 */
	public void setIndex(int index) {
		logger.debug("setIndex "+index); //$NON-NLS-1$
		this.index = index;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void setInput(IEditorInput input) {
		logger.debug("setInput "+input); //$NON-NLS-1$
		super.setInput(input);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the provider from the editor set in {@link #initialize(FormEditor)}
	 * in order to share the provider with the other editor pages.
	 * 
	 * @see org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview.ReportLayoutPreviewEditor#getProvider()
	 */
	protected IReportProvider getProvider() {
		IReportProvider provider = null;
		if (editor != null)
			 provider = (IReportProvider) editor.getAdapter(IReportProvider.class);
		if (provider != null)
			setProvider(provider);
		if(provider == null)
		{
			provider = getDefaultProvider();
		}

		return provider;
	}
	
//	private ActionRegistry registry;
//
//	public Object getAdapter( Class required )
//	{
//		System.out.println("Get Adapter called for "+required);
//		if ( required.equals( ActionRegistry.class ) )
//		{
//			if ( registry == null )
//			{
//				registry = new ActionRegistry( );
//			}
//			return registry;
//		}
//		return super.getAdapter( required );
//	}
	
	public Object getModel() {
		if (model == null)
			model = getProvider().getReportModuleHandle(getEditorInput());
		return model;
	}
 	
	protected boolean isDirtyModel() {
		if (getModel() != null && getModel() instanceof ModuleHandle) {
			return ((ModuleHandle) getModel()).needsSave();
		}
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		if (getEditor() != null)
			getEditor().editorDirtyStateChanged();
	}
	
	protected IReportProvider getDefaultProvider()
	{
		if (provider == null) {
			provider = new FileReportProvider();
		}
		return provider;
	}
	
	protected void setProvider(IReportProvider provider) {
		this.provider = provider;
	}
	
//	@Override
//	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
//		super.setSite(site);
//		setInput(input);
//		initializeActionRegistry();
//	}

//	@Override
//	public boolean isDirty() {
//		return super.isDirty();
//	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void setFocus() {
	}

	@Override
	public ModuleHandle getReportHandle() {
		return (ModuleHandle) getModel();
	}

	public void updateActions() {
		super.updateActions(getSelectionActions());
		super.updateActions(getPropertyActions());
		super.updateActions(getStackActions());
	}

	@Override
	public Object getAdapter(Class type) {
		if (type == IActionUpdateDelegate.class)
			return this;
		return super.getAdapter(type);
	}
}
