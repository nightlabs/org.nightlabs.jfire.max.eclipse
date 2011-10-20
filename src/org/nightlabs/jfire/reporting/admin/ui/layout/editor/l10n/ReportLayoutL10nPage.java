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

package org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.designer.ui.editors.IReportEditorPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.ReportLayoutL10nUtil.PreparedLayoutL10nData;
import org.nightlabs.jfire.reporting.layout.ReportLayoutLocalisationData;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.security.SecurityReflector;

import com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor;

/**
 * A page for the Report Designer that lets the user edit a reports localisation bundle.
 * It is based on the Eclipse ResourceBundle Editor (http://sourceforge.net/projects/eclipse-rbe/).
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportLayoutL10nPage
extends ResourceBundleEditor
implements IReportEditorPage, IReportLayoutL10nManager
{

	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(ReportLayoutL10nPage.class);

	public static final String ID_PAGE = ReportLayoutL10nPage.class.getName();

	private int staleType;
	private Control control;
	private FormEditor editor;

	private ReportRegistryItemID reportLayoutID;
	private Map<String, ReportLayoutLocalisationData> localisationBundle;
	private IFolder bundleFolder;

	private int index;

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (editorInput instanceof JFireRemoteReportEditorInput) {
			JFireRemoteReportEditorInput input = (JFireRemoteReportEditorInput) editorInput;
			reportLayoutID = input.getReportRegistryItemID();
			PreparedLayoutL10nData l10nData = ReportLayoutL10nUtil.prepareReportLayoutL10nData(input);
			bundleFolder = l10nData.getBundleFolder();
			localisationBundle = l10nData.getLocalisationBundle();

			IFile file = bundleFolder.getFile(ReportLayoutLocalisationData.PROPERIES_FILE_PREFIX+".properties"); //$NON-NLS-1$
			FileEditorInput newInput = new FileEditorInput(file);
			addPropertyListener(new IPropertyListener() {
				public void propertyChanged(Object source, int propId) {
					if (propId == IEditorPart.PROP_DIRTY)
						editor.editorDirtyStateChanged();
				}
			});
			super.init(site, newInput);
		}
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
		logger.debug("Can leave page"); //$NON-NLS-1$
		return true;
	}

	@Override
	public boolean isDirty() {
		return super.isDirty();
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

//	/*
//	* (non-Javadoc)
//	* @see org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview.ReportLayoutPreviewEditor#createPartControl(org.eclipse.swt.widgets.Composite)
//	*/
//	@Override
//	public void createPartControl(Composite parent) {
//	logger.debug("create part Control");
//	super.createPartControl(parent);
//	Control[] children = parent.getChildren( );
//	if (children.length < 1)
//	throw new IllegalStateException("Can not create "+this.getClass().getSimpleName()+", super iplementation did not create the part control!");
//	control = children[children.length - 1];
//	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getPartControl()
	 */
	public Control getPartControl() {
		logger.debug("getPartControl returning "+control); //$NON-NLS-1$
		return getContainer();
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
		return true;
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

//	/**
//	* {@inheritDoc}
//	* <p>
//	* Returns the provider from the editor set in {@link #initialize(FormEditor)}
//	* in order to share the provider with the other editor pages.
//	*
//	* @see org.nightlabs.jfire.reporting.admin.ui.layout.editor.preview.ReportLayoutPreviewEditor#getProvider()
//	*/
//	@Override
//	protected IReportProvider getProvider() {
//	IReportProvider provider = null;
//	if (editor != null)
//	provider = (IReportProvider) editor.getAdapter(IReportProvider.class);
//	if (provider != null)
//	super.setProvider(provider);
//	if(provider == null)
//	{
//	provider = super.getProvider();
//	}

//	return provider;
//	}

	private ActionRegistry registry;

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class required) {
		if (required.equals(ActionRegistry.class)) {
			if (registry == null) {
				registry = new ActionRegistry();
			}
			return registry;
		}
		return super.getAdapter(required);
	}

	private boolean pagesCreated = false;

	@Override
	protected void createPages() {
		super.createPages();
		pagesCreated = true;
	}

	public void saveLocalisationBundle(IProgressMonitor monitor) {
		if (!pagesCreated)
			return;
		doSave(monitor);
		File workspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		File file = new File(workspaceRoot, bundleFolder.getFullPath().toOSString());
		File[] files = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().contains(ReportLayoutLocalisationData.PROPERIES_FILE_PREFIX);
			}
		});
		if (files == null)
			return;
		Collection<ReportLayoutLocalisationData> dataToStore = new ArrayList<ReportLayoutLocalisationData>(files.length);
		for (File bundleFile : files) {
			String locale = ReportLayoutLocalisationData.extractLocale(bundleFile.getName());
			if (locale == null)
				locale = ""; //$NON-NLS-1$
			ReportLayoutLocalisationData data = null;
			if (localisationBundle.containsKey(locale))
				data = localisationBundle.get(locale);
			else {
				data = new ReportLayoutLocalisationData(reportLayoutID, locale);
			}
			try {
				data.loadFile(bundleFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			dataToStore.add(data);
		}
		Collection<ReportLayoutLocalisationData> bundle = null;
		try {
			ReportManagerRemote reportManager = JFireEjb3Factory.getRemoteBean(
					ReportManagerRemote.class, SecurityReflector.getInitialContextProperties());
			bundle = reportManager.storeReportLayoutLocalisationBundle(dataToStore, true,
					new String[] {FetchPlan.DEFAULT, ReportLayoutLocalisationData.FETCH_GROUP_LOCALISATOIN_DATA},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		editor.editorDirtyStateChanged();

		localisationBundle = new HashMap<String, ReportLayoutLocalisationData>();
		for (ReportLayoutLocalisationData data : bundle) {
			localisationBundle.put(data.getLocale(), data);
		}
	}

	public Collection<Locale> getBundleLocales() {
		return getResourceManager() != null ? new ArrayList<Locale>(getResourceManager().getLocales()) : null;
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 0) {  // switched to first page
            if (editor.isDirty())
            	editor.doSave(new NullProgressMonitor());
        }
	}
}
