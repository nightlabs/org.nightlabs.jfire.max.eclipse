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

package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataElementAttributes;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.InputElementAttributes;
import org.eclipse.datatools.connectivity.oda.design.InputParameterAttributes;
import org.eclipse.datatools.connectivity.oda.design.ParameterDefinition;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditorFactory;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetEditorRegistry;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.oda.client.jfs.ClientJFSDriver;
import org.nightlabs.jfire.reporting.oda.jfs.JFSParameterUtil;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryUtil;

/**
 * DataSet WizardPage that lets the user pick a JFireScript that
 * should be executed for the DataSet and also lets the user
 * configure the {@link JFSQueryPropertySet} for the data-set script.
 * 
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 */
public class JFSQueryPropertySetWizardPage extends DataSetWizardPage {

	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(JFSQueryPropertySetWizardPage.class);

	private SashForm wrapper;
	private SelectedScriptComposite selectedScriptComposite;
	private Group propertyGroup;
	private IJFSQueryPropertySetEditorFactory propertySetEditorFactory;
	private IJFSQueryPropertySetEditor propertySetEditor;

	/**
	 * Create a new {@link JFSQueryPropertySetWizardPage}.
	 * 
	 * @param name The page name.
	 */
	public JFSQueryPropertySetWizardPage(String name) {
		super(name);
	}


	/**
	 * Create a new {@link JFSQueryPropertySetWizardPage}.
	 *  
	 * @param name The page name.
	 * @param title The page title.
	 * @param icon The page icon.
	 */
	public JFSQueryPropertySetWizardPage(String name, String title,
			ImageDescriptor icon) {
		super(name, title, icon);
	}

	private JFSQueryPropertySet queryPropertySet = null;

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#createPageCustomControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageCustomControl(Composite parent) {
//		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		wrapper = new SashForm(parent, SWT.VERTICAL);

		selectedScriptComposite = new SelectedScriptComposite(wrapper, SWT.NONE);

		propertyGroup = new Group(wrapper, SWT.NONE);
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, gl);
		propertyGroup.setLayout(gl);
		propertyGroup.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSQueryPropertySetWizardPage.propertyGroup.text")); //$NON-NLS-1$		
		wrapper.setWeights(new int[] {3, 2});
		setControl(wrapper);
		setMessage(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSQueryPropertySetWizardPage.message")); //$NON-NLS-1$
		refresh(null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#refresh(org.eclipse.datatools.connectivity.oda.design.DataSetDesign)
	 */
	@Override
	protected void refresh(DataSetDesign design) {
		super.refresh(design);
		if (design != null && design.getQueryText() != null) {
			try {
				queryPropertySet = JFSQueryUtil.createPropertySetFromQueryString(design.getQueryText());
				if (queryPropertySet == null)
					queryPropertySet = new JFSQueryPropertySet();
			} catch (Exception e) {
				logger.error("Have query text, but can not create JFSQueryProperySet out of it!", e); //$NON-NLS-1$
				queryPropertySet = new JFSQueryPropertySet();
			}
			logDesignParameters("refresh: ", design); //$NON-NLS-1$
		}
		if (queryPropertySet == null) {
			queryPropertySet = new JFSQueryPropertySet();
		}
		if (queryPropertySet.getScriptRegistryItemID() != null) {
			if (selectedScriptComposite != null && !selectedScriptComposite.isDisposed()) {
				selectedScriptComposite.setScriptRegistryItemID(queryPropertySet.getScriptRegistryItemID());
			}

			IJFSQueryPropertySetEditorFactory newFactory = JFSQueryPropertySetEditorRegistry.sharedInstance()
				.getJFSQueryPropertySetFactory(queryPropertySet.getScriptRegistryItemID());

			if (newFactory == null) {
				throw new IllegalStateException(JFSQueryPropertySetEditorRegistry.class.getName() + " returned null on getJFSQueryPropertySetFactory()");
			}

			if (newFactory != null && newFactory != propertySetEditorFactory) {
				if (
						propertySetEditor != null && 
						propertySetEditor.getControl() != null &&
						!propertySetEditor.getControl().isDisposed()
				) {
					propertySetEditor.getControl().dispose();
				}
				propertySetEditorFactory = newFactory;
				propertySetEditor = propertySetEditorFactory.createJFSQueryPropertySetEditor();
				if (propertySetEditor == null) {
					throw new IllegalStateException(propertySetEditorFactory.getClass().getName() + " returned no editor on createJFSQueryPropertySetEditor()", new NullPointerException("propertySetEditor"));
				}
				propertySetEditor.createControl(propertyGroup);
			}
			if (propertySetEditor != null) {
				propertySetEditor.setJFSQueryPropertySet(queryPropertySet);
			}
		} // if (queryPropertySet.getScriptRegistryItemID() != null)
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#collectDataSetDesign(org.eclipse.datatools.connectivity.oda.design.DataSetDesign)
	 */
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		if (queryPropertySet != null) {
			if (selectedScriptComposite != null && !selectedScriptComposite.isDisposed()) {
				queryPropertySet.setScriptRegistryItemID(selectedScriptComposite.getScriptRegistryItemID());
			}
			if (propertySetEditor != null) {
				queryPropertySet.setProperties(propertySetEditor.getProperties());
			}
			String queryText = JFSQueryUtil.createQueryStringFromPropertySet(queryPropertySet);
			design.setQueryText(queryText);

			// obtain query's current runtime metadata, and maps it to the dataSetDesign
			IConnection customConn = null;
			try
			{
				// instantiate your custom ODA runtime driver class
				/* Note: You may need to manually update your ODA runtime extension's
				 * plug-in manifest to export its package for visibility here.
				 */
				IDriver customDriver = new ClientJFSDriver();

				// obtain and open a live connection
				customConn = customDriver.getConnection( null );
				java.util.Properties connProps =
					DesignUtil.convertDataSourceProperties(
							getInitializationDesign().getDataSourceDesign() );
				customConn.open( connProps );

				// update the data set design with the
				// query's current runtime metadata
				updateDesign( design, customConn, queryText );
			}
			catch( OdaException e )
			{
				// not able to get current metadata, reset previous derived metadata
				design.setResultSets( null );
				design.setParameters( null );

				e.printStackTrace();
			}
			finally
			{
				closeConnection( customConn );
			}
		}
		logDesignParameters("collect: ", design);		 //$NON-NLS-1$
		return design;
	}

	/**
	 * Updates the given dataSetDesign with the queryText and its derived metadata
	 * obtained from the ODA runtime connection.
	 */
	private void updateDesign(DataSetDesign dataSetDesign, IConnection conn, String queryText)
	throws OdaException
	{
		IQuery query = conn.newQuery( null );
		query.prepare( queryText );

		// TODO a runtime driver might require a query to first execute before
		// its metadata is available
//		query.setMaxRows( 1 );
//		query.executeQuery();

		try {
			IResultSetMetaData md = query.getMetaData();
			updateResultSetDesign( md, dataSetDesign );
		} catch( OdaException e ) {
			// no result set definition available, reset previous derived metadata
			dataSetDesign.setResultSets( null );
			e.printStackTrace();
		}

		// proceed to get parameter design definition
		try {
			IParameterMetaData paramMd = query.getParameterMetaData();
			updateParameterDesign( paramMd, dataSetDesign );
		} catch( OdaException ex ) {
			// no parameter definition available, reset previous derived metadata
			dataSetDesign.setParameters( null );
			ex.printStackTrace();
		}
	}

	/**
	 * Updates the specified data set design's result set definition based on the
	 * specified runtime metadata.
	 * @param md    runtime result set metadata instance
	 * @param dataSetDesign     data set design instance to update
	 * @throws OdaException
	 */
	private void updateResultSetDesign( IResultSetMetaData md,
			DataSetDesign dataSetDesign )
	throws OdaException
	{
		ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign( md );

		ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE
		.createResultSetDefinition();
		// resultSetDefn.setName( value );  // result set name
		resultSetDefn.setResultSetColumns( columns );

		// no exception in conversion; go ahead and assign to specified dataSetDesign
		dataSetDesign.setPrimaryResultSet( resultSetDefn );
		dataSetDesign.getResultSets().setDerivedMetaData( true );
	}

	/**
	 * Updates the specified data set design's parameter definition based on the
	 * specified runtime metadata.
	 * @param paramMd The runtime parameter metadata instance.
	 * @param dataSetDesign The data set design instance to update.
	 * @throws OdaException
	 */
	private void updateParameterDesign( IParameterMetaData paramMd,
			DataSetDesign dataSetDesign )
	throws OdaException
	{
		DataSetParameters paramDesign =
			DesignSessionUtil.toDataSetParametersDesign( paramMd,
					DesignSessionUtil.toParameterModeDesign( IParameterMetaData.parameterModeIn ) );

		if (paramDesign == null)
			return;
		// no exception in conversion; go ahead and assign to specified dataSetDesign
		paramDesign.setDerivedMetaData( true );
		// TODO WORKAROUND
		// hard-coded parameter's default because bindings will be ignored if no default value set :-(
		for (int i = 0; i < paramDesign.getParameterDefinitions().size(); i++) {
			ParameterDefinition paramDef = (ParameterDefinition) paramDesign.getParameterDefinitions().get(i);
			if( paramDef != null )
				paramDef.setDefaultScalarValue(JFSParameterUtil.DUMMY_DEFAULT_PARAMETER_VALUE);
		}
		dataSetDesign.setParameters( paramDesign );

	}

	/**
	 * Attempts to close given ODA connection.
	 */
	private void closeConnection( IConnection conn )
	{
		try
		{
			if( conn != null && conn.isOpen() )
				conn.close();
		}
		catch ( OdaException e )
		{
			// ignore
			e.printStackTrace();
		}
	}


	private void logDesignParameters(String prefix, DataSetDesign design) {
		if (design.getParameters() == null)
			return;
		EList paramDefns = design.getParameters().getParameterDefinitions();
		for (Iterator iter = paramDefns.iterator(); iter.hasNext();) {
			ParameterDefinition definition = (ParameterDefinition) iter.next();
			logger.info(prefix + "Found parameter with InOutMode: " + definition.getInOutMode().toString());			 //$NON-NLS-1$
			DataElementAttributes dataElementAttributes = definition.getAttributes();
			InputParameterAttributes attrs = definition.getInputAttributes();
			if (attrs == null)
				return;
			InputElementAttributes elementAttributes = attrs.getElementAttributes();
			logger.info(prefix + "Parameter name: " + dataElementAttributes.getName()); //$NON-NLS-1$
			logger.info(prefix + "Parameter allowsNull: " + dataElementAttributes.allowsNull()); //$NON-NLS-1$
			logger.info(prefix + "Parameter defaultValue: " + elementAttributes.getDefaultScalarValue()); //$NON-NLS-1$
		}
	}

}
