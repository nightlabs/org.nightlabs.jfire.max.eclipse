/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.jfire.reporting.admin.ui.layout.editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.birt.report.designer.internal.ui.editors.FileReportProvider;
import org.eclipse.birt.report.designer.ui.editors.IReportEditorPage;
import org.eclipse.birt.report.designer.ui.editors.IReportProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.composite.Fadeable;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.progress.RCPProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;


public abstract class ReportLayoutEntityEditor extends EditorPart
{
	private int staleType;
	private Control control;
	private FormEditor editor;
	
	private int index;
	
	/**
	 * The provider is needed for the page to be correctly 
	 * integrated into the editor (otherwise the dirty notifications won't work).
	 */
	private IReportProvider provider;
	
	public ReportLayoutEntityEditor()
	{	}
	
	/**
	 * Get the editor id.
	 * @return The editor id
	 */
	public String getEditorID() {
		return getEditorSite().getId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// Save as not supported by entity editor
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * This implementation additionally creates this editor's controller.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	private IRunnableWithProgress saveRunnable = new IRunnableWithProgress() {
		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			pageController.doSave(new org.nightlabs.base.ui.progress.ProgressMonitorWrapper(monitor));
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					editorDirtyStateChanged();
				}
			});
		}
	};

	private IFormPage formPage;

	private IEntityEditorPageController pageController;

	private ScrolledForm scrolledForm;	
	
	@Override
	public boolean isDirty() {
		return pageController != null ? pageController.isDirty() : false;
	}
	
	protected void editorDirtyStateChanged() {
		firePropertyChange(PROP_DIRTY);
		getEditor().editorDirtyStateChanged();		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * This implementation will start a job to save the
	 * editor. It will first let all pages commit and then
	 * call its controllers doSave() method. This will
	 * cause all page controllers to save their model.
	 * If the active page appears to be {@link Fadeable} it will
	 * be faded until the save operation is finished.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		Job saveJob = null;
		IFormPage page = getPage();
		if (page instanceof Fadeable) {
				saveJob = new FadeableCompositeJob("Saving entity", ((Fadeable)page), this) {
					@Override
					protected IStatus run(ProgressMonitor monitor, Object source) throws Exception {
						try {
							saveRunnable.run(new RCPProgressMonitor(monitor));
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return Status.OK_STATUS;
					}
			};
		}
		if (saveJob == null) {
			saveJob = new Job("Saving entity") {
				@Override
				protected IStatus run(ProgressMonitor monitor) {
					try {
						saveRunnable.run(getProgressMonitor());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return Status.OK_STATUS;
				}
			};
		}
//		saveJob.setUser(true);
		saveJob.schedule();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (pageController != null)
			pageController.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new NightlabsFormsToolkit(parent.getDisplay());
		XComposite wrapper = new XComposite(parent, SWT.BORDER);
		wrapper.setToolkit(toolkit);		
//		scrolledForm = toolkit.createScrolledForm(parent);
//		GridLayout gl = new GridLayout();
//		XComposite.configureLayout(LayoutMode.ORDINARY_WRAPPER, gl);
//		scrolledForm.getBody().setLayout(gl);
//		XComposite wrapper = new XComposite(scrolledForm.getBody(), SWT.NONE, LayoutMode.TOTAL_WRAPPER);
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		gd.widthHint = 1;
//		gd.heightHint = 1;
//		wrapper.setLayoutData(gd);
//		wrapper.setToolkit(toolkit);
//		
//		ManagedForm mForm = new ManagedForm(toolkit, scrolledForm);
//		
		EntityEditor dummyEditor = getDummyEntityEditor();
		
		pageController = createPageController(dummyEditor);
		formPage = createFormPage(dummyEditor);
		pageController.addPage(formPage);
		wrapper.setLayout(new FillLayout());
		formPage.createPartControl(wrapper);
		
		wrapper.adaptToToolkit();
//		scrolledForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		control = wrapper;
	}

	@Override
	public void setFocus() {
		formPage.setFocus();
	}
	
	protected abstract IEntityEditorPageController createPageController(EntityEditor entityEditor);
	
	protected abstract IFormPage createFormPage(EntityEditor entityEditor);
	
	protected ScrolledForm getForm() {
		return scrolledForm;
	}
	
	private IFormPage getPage() {
		return formPage;
	}
	
	@Override
	public JFireRemoteReportEditorInput getEditorInput() {
		return (JFireRemoteReportEditorInput) super.getEditorInput();
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
	private NightlabsFormsToolkit toolkit;
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
	
	private class DummyEntityEditor extends EntityEditor {
		private EntityEditorController c;
		
		public DummyEntityEditor() {
			c = new EntityEditorController(this) {
				@Override
				public IEntityEditorPageController getPageController(IFormPage page) {
					return pageController;
				}
			};
		}
		
		@Override
		public EntityEditorController getController() {
			return c;
		}
		
		@Override
		public FormToolkit getToolkit() {
			return toolkit;
		}
		
		@Override
		public JFireRemoteReportEditorInput getEditorInput() {
			return ReportLayoutEntityEditor.this.getEditorInput();
		}
	}
	
	public EntityEditor getDummyEntityEditor() {
		return new DummyEntityEditor();
	}
}
