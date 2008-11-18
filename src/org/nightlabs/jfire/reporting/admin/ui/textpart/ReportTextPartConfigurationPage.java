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

package org.nightlabs.jfire.reporting.admin.ui.textpart;

import org.eclipse.birt.report.designer.internal.ui.editors.FileReportProvider;
import org.eclipse.birt.report.designer.ui.editors.IReportEditorPage;
import org.eclipse.birt.report.designer.ui.editors.IReportProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;
import org.nightlabs.jfire.reporting.ui.textpart.IReportTextPartConfigurationChangedListener;
import org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartConfigurationChangedEvent;

/**
 * A page for the Report Designer that lets the edit the
 * {@link ReportTextPartConfiguration} for the current 
 * {@link ReportRegistryItem}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationPage
extends ReportTextPartConfigurationEditor
implements IReportEditorPage
{

	public static final String ID_PAGE = ReportTextPartConfigurationPage.class.getName();
	
	private XComposite wrapper;
	
	private int staleType;
	private Control control;
	private FormEditor editor;
	
	private int index;

	private IReportTextPartConfigurationChangedListener changedListener = new IReportTextPartConfigurationChangedListener() {
		@Override
		public void reportTextPartConfigurationChanged(ReportTextPartConfigurationChangedEvent evt) {
			if (editor != null) {
				editor.editorDirtyStateChanged();
			}
		}
	};
	
	/**
	 * The provider is needed for the page to be correctly 
	 * integrated into the editor (otherwise the dirty notifications won't work).
	 */
	private IReportProvider provider;
	
	@Override
	public void createPartControl(Composite parent)
	{
		wrapper = new XComposite(parent, SWT.BORDER, LayoutMode.NONE);
		wrapper.setLayout(new FillLayout());
		super.createPartControl(wrapper);
		control = wrapper;
		getConfigurationEditComposite().addReportTextPartConfigurationChangedListener(changedListener);
		getConfigurationEditComposite().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				getConfigurationEditComposite().removeReportTextPartConfigurationChangedListener(changedListener);
			}			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getPartControl()
	 */
	public Control getPartControl() {
		return control;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	public void initialize(FormEditor editor) {
		this.editor = editor;
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.editors.IReportEditorPage#onBroughtToTop(org.eclipse.birt.report.designer.ui.editors.IReportEditorPage)
	 */
	public boolean onBroughtToTop(IReportEditorPage prePage) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#canLeaveThePage()
	 */
	public boolean canLeaveThePage() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getEditor()
	 */
	public FormEditor getEditor() {
		return editor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.editors.IReportEditorPage#getStaleType()
	 */
	public int getStaleType() {
		return staleType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.editors.IReportEditorPage#markPageStale(int)
	 */
	public void markPageStale(int type) {
		staleType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getId()
	 */
	public String getId() {
		return ID_PAGE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getIndex()
	 */
	public int getIndex() {
		return index;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getManagedForm()
	 */
	public IManagedForm getManagedForm() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#isActive()
	 */
	public boolean isActive() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#isEditor()
	 */
	public boolean isEditor() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#selectReveal(java.lang.Object)
	 */
	public boolean selectReveal(Object object) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#setActive(boolean)
	 */
	public void setActive(boolean active) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#setIndex(int)
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		if (getEditor() != null)
			getEditor().editorDirtyStateChanged();
	}
	
	@Override
	public void setInput(IEditorInput input) {
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

	private ActionRegistry registry;
	@Override
	public Object getAdapter(Class required) {
		if (required.equals( ActionRegistry.class ) )
		{
			if ( registry == null )
			{
				registry = new ActionRegistry( );
			}
			return registry;
		}
		return super.getAdapter(required);
	}
}
