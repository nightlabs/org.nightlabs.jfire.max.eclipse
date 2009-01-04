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

package org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.designer.internal.ui.editors.FileReportProvider;
import org.eclipse.birt.report.designer.ui.editors.IReportEditorPage;
import org.eclipse.birt.report.designer.ui.editors.IReportProvider;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.composite.ComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.admin.ui.ReportingAdminPlugin;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.IJFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireReportEditor;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.IReportLayoutL10nManager;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.ui.config.BirtOutputCombo;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCase;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCaseRegistry;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule.UseCaseConfig;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard.WizardResult;
import org.nightlabs.jfire.reporting.ui.viewer.NoReportViewerFoundException;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewer;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerFactory;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerRegistry;
import org.nightlabs.util.NLLocale;

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
public class ReportLayoutPreviewPage
extends EditorPart
implements IReportEditorPage
{

	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(ReportLayoutPreviewPage.class);
	
	public static final String ID_PAGE = ReportLayoutPreviewPage.class.getName();
	
	
	private Object model;
	
	private IReportProvider provider;
	
	private XComposite wrapper;
	private XComposite topWrapper;
	private Button parameterButton;
	
	private BirtOutputCombo outputCombo;
	private ComboComposite<Locale> localeCombo;
	
	private ReportViewer reportViewer;
	
	private boolean parameterAquisitionDone = false;
	private Map<String, Object> reportParameters;
	
	
	private int staleType;
	private Control control;
	private FormEditor editor;
	
	private int index;
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview.ReportLayoutPreviewEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		logger.debug("create part Control"); //$NON-NLS-1$
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		topWrapper = new XComposite(wrapper, SWT.NONE);
		topWrapper.getGridLayout().numColumns = 5;
		topWrapper.getGridLayout().makeColumnsEqualWidth = false;
		topWrapper.getGridData().grabExcessVerticalSpace = false;
		topWrapper.getGridData().verticalAlignment = SWT.TOP;
		
		parameterButton = new Button(topWrapper, SWT.PUSH);
		parameterButton.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview.ReportLayoutPreviewPage.parameterButton.text")); //$NON-NLS-1$
		parameterButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				IJFireRemoteReportEditorInput jfireInput = (IJFireRemoteReportEditorInput) getEditorInput();
				Map<String, Object> params = ReportParameterWizard.open(jfireInput.getReportRegistryItemID(), false);
				if (params == null)
					return;
				setReportParameters(params);
				showPreview(jfireInput.getReportRegistryItemID(), getReportParameters());
			}
		});
		Label label = new Label(topWrapper, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview.ReportLayoutPreviewPage.outputTypeLabel.text")); //$NON-NLS-1$
		outputCombo = new BirtOutputCombo(topWrapper, SWT.READ_ONLY);
		ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCase(ReportingAdminPlugin.REPORT_USECASE_PREVIEW);
		ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
		UseCaseConfig useCaseConfig = cfMod.getReportUseCaseConfigs().get(useCase.getId());
		outputCombo.setSelection(Birt.parseOutputFormat(useCaseConfig.getViewerFormat()));
		outputCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IJFireRemoteReportEditorInput jfireInput = (IJFireRemoteReportEditorInput) getEditorInput();
				Map<String, Object> params = getReportParameters();
				if (params == null) {
					WizardResult wizardResult = ReportParameterWizard.openResult(jfireInput.getReportRegistryItemID(), false);
					if (wizardResult.isAcquisitionFinished()) {
						params = wizardResult.getParameters();
					}
					else
						return;
				}
				setReportParameters(params);
				showPreview(jfireInput.getReportRegistryItemID(), getReportParameters());
			}
		});
		
		
		Label label2 = new Label(topWrapper, SWT.NONE);
		label2.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview.ReportLayoutPreviewPage.localeLabel.text")); //$NON-NLS-1$
		localeCombo = new ComboComposite<Locale>(topWrapper, SWT.READ_ONLY);
		localeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IJFireRemoteReportEditorInput jfireInput = (IJFireRemoteReportEditorInput) getEditorInput();
				Map<String, Object> params = getReportParameters();
				if (params == null) {
					params = ReportParameterWizard.open(jfireInput.getReportRegistryItemID(), false);
					if (params == null)
						return;
				}
				setReportParameters(params);
				showPreview(jfireInput.getReportRegistryItemID(), getReportParameters());
			}
		});
		localeCombo.getGridData().grabExcessVerticalSpace = false;
		
		Control[] children = parent.getChildren( );
		if (children.length < 1)
			throw new IllegalStateException("Can not create "+this.getClass().getSimpleName()+", super iplementation did not create the part control!"); //$NON-NLS-1$ //$NON-NLS-2$
		control = children[children.length - 1];
//		viewerWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getPartControl()
	 */
	public Control getPartControl() {
		logger.debug("getPartControl returning "+control); //$NON-NLS-1$
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
		if (isDirtyModel()) {
			logger.debug("Have dirty model, save first"); //$NON-NLS-1$
			if (editor != null) {
				editor.doSave(new NullProgressMonitor());
				editor.editorDirtyStateChanged();
			}
		}
		
		if (getEditor() instanceof JFireReportEditor) {
			IReportLayoutL10nManager l10nManager = ((JFireReportEditor)getEditor()).getReportLayoutL10nManager();
			if (l10nManager != null) {
				Locale selLocale = localeCombo.getSelectedElement();
				localeCombo.removeAll();
				Collection<Locale> locales = l10nManager.getBundleLocales();
				if (locales == null || locales.size() == 0) {
					locales = new ArrayList<Locale>(1);
					selLocale = NLLocale.getDefault();
					locales.add(selLocale);
				}
				if (!locales.contains(NLLocale.getDefault()))
					locales.add(NLLocale.getDefault());
				
				localeCombo.addElements(locales);
				if (selLocale == null)
					selLocale = NLLocale.getDefault();
				localeCombo.setSelection(selLocale);
			}
		}
		
		IEditorInput input = getEditorInput();
		if (input instanceof IJFireRemoteReportEditorInput) {
			IJFireRemoteReportEditorInput jfireInput = (IJFireRemoteReportEditorInput) input;
			if (!isParameterAquisitionDone()) {
				WizardResult paramResult = ReportParameterWizard.openResult(jfireInput.getReportRegistryItemID(), false);
				Map<String, Object> params = paramResult.getParameters();
				if (paramResult.isAcquisitionFinished()) {
					setParameterAquisitionDone(true);
					setReportParameters(params);
				}
			}
			if (isParameterAquisitionDone()) {
				showPreview(jfireInput.getReportRegistryItemID(), getReportParameters());
			}
			return true;
		}
		else
			throw new RuntimeException("The editor is not associated with a JFire report layout!"); //$NON-NLS-1$
		
		
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
	
	private ActionRegistry registry;
	
	@Override
	public Object getAdapter( Class required )
	{
		if ( required.equals( ActionRegistry.class ) ) {
			if ( registry == null ) {
				registry = new ActionRegistry( );
			}
			return registry;
		}
		return super.getAdapter( required );
	}
	
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
		IEditorInput input = getEditorInput();
		if (input instanceof IJFireRemoteReportEditorInput) {
			// save remote
			IJFireRemoteReportEditorInput jfireInput = (IJFireRemoteReportEditorInput) input;
			JFireRemoteReportEditorInput.saveRemoteLayout(jfireInput, monitor);
		}
		IReportProvider provider = getProvider( );
		if (provider != null) {
			// save local
			provider.saveReport(
					(ModuleHandle) getModel(),
					getEditorInput(),
					monitor
				);
			firePropertyChange( PROP_DIRTY );
		}
	}

	@Override
	public void doSaveAs() {
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
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void setFocus() {
	}

	protected void showPreview(ReportRegistryItemID registryItemID, Map<String, Object> parameters) {
		ReportViewer viewer = createReportViewer();
		if (parameters != null)
			viewer.showReport(createRenderRequest(registryItemID, parameters));
		else
			viewer.showReport(createRenderRequest(registryItemID, new HashMap<String, Object>()));
		wrapper.layout(true, true);
	}
	
	protected RenderReportRequest createRenderRequest(
			ReportRegistryItemID registryItemID,
			Map<String, Object> parameters
		)
	{
		// TODO: Add ComboBox for Preview format
		RenderReportRequest renderRequest = new RenderReportRequest();
		renderRequest.setReportRegistryItemID(registryItemID);
		renderRequest.setParameters(parameters);
		renderRequest.setOutputFormat(Birt.parseOutputFormat(outputCombo.getSelectedElement().toString()));
		renderRequest.setLocale(localeCombo.getSelectedElement());
		return renderRequest;
	}
	
	protected ReportViewer createReportViewer() {
		if (reportViewer == null) {
			ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCase(ReportingAdminPlugin.REPORT_USECASE_PREVIEW);
			ReportViewerFactory factory = null;
			try {
				factory = ReportViewerRegistry.sharedInstance().getReportViewerFactory(useCase);
			} catch (NoReportViewerFoundException e) {
				throw new RuntimeException(e);
			}
			if (!factory.isAdaptable(Composite.class))
				throw new IllegalStateException("ReportViewerFactory associated with ReportPreviewUseCase is not adaptable to Composite!"); //$NON-NLS-1$
			reportViewer = factory.createReportViewer();
			
			reportViewer.getAdapter(wrapper);
		}
		return reportViewer;
	}

	/**
	 * @return Whether parameters were already acquired for this report.
	 */
	protected boolean isParameterAquisitionDone() {
		return parameterAquisitionDone;
	}
	
	protected void setParameterAquisitionDone(boolean parameterAquisitionDone) {
		this.parameterAquisitionDone = parameterAquisitionDone;
	}
	
	/**
	 * @return the reportParameters
	 */
	protected Map<String, Object> getReportParameters() {
		return reportParameters;
	}

	/**
	 * @param reportParameters the reportParameters to set
	 */
	protected void setReportParameters(Map<String, Object> reportParameters) {
		this.reportParameters = reportParameters;
	}
	
}
