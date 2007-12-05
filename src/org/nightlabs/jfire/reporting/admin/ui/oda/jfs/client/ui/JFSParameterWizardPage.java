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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.designer.ui.dialogs.ExpressionBuilder;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataElementAttributes;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ElementNullability;
import org.eclipse.datatools.connectivity.oda.design.InputElementAttributes;
import org.eclipse.datatools.connectivity.oda.design.InputParameterAttributes;
import org.eclipse.datatools.connectivity.oda.design.ParameterDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.oda.JFireReportingOdaException;
import org.nightlabs.jfire.reporting.oda.ParameterMetaData;
import org.nightlabs.jfire.reporting.oda.ParameterMetaData.ParameterDescriptor;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryUtil;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.dao.ScriptParameterSetDAO;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 */
public class JFSParameterWizardPage extends DataSetWizardPage implements ICellModifier {

	private static final Logger logger = Logger.getLogger(JFSParameterWizardPage.class);
	
	private static final String PROPERTY_NAME = "Name"; //$NON-NLS-1$
	private static final String PROPERTY_TYPE = "Type"; //$NON-NLS-1$
	private static final String PROPERTY_DEFAULT_VALUE = "Type"; //$NON-NLS-1$
	
	private class Table extends AbstractTableComposite<ParameterDescriptor> {

		public Table(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected void createTableColumns(TableViewer tableViewer, org.eclipse.swt.widgets.Table table) {
			TableColumn tc;

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSParameterWizardPage.nameColumn.text")); //$NON-NLS-1$

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSParameterWizardPage.typeColumn.text")); //$NON-NLS-1$

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSParameterWizardPage.defaultValueColumn.text")); //$NON-NLS-1$

//			table.setLayout(new WeightedTableLayout(new int[] {10, 10, 10, 10, 10, 10}));
			TableLayout l = new TableLayout();
			l.addColumnData(new ColumnWeightData(1, 50));
			l.addColumnData(new ColumnWeightData(1, 50));
			l.addColumnData(new ColumnWeightData(1, 50));
			table.setLayout(l);
			
			tableViewer.setColumnProperties(new String[] {
					PROPERTY_NAME,
					PROPERTY_TYPE,
					PROPERTY_DEFAULT_VALUE					
			});
			tableViewer.setCellEditors(new CellEditor[] {
					null,
					null,
					new DefaultValueCellEditor(table)
			});
			tableViewer.setCellModifier(JFSParameterWizardPage.this);
			
		}

		@Override
		protected void setTableProvider(TableViewer tableViewer) {
			tableViewer.setContentProvider(new ContentProvider());
			tableViewer.setLabelProvider(new LabelProvider());
		}
	}
	
	private class ContentProvider extends TableContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {			
			if (inputElement instanceof ParameterMetaData) {
				ParameterMetaData metaData = (ParameterMetaData) inputElement;
				try {
					List<ParameterDescriptor> result = new ArrayList<ParameterDescriptor>(metaData.getParameterCount());
					for (int i = 1; i <= metaData.getParameterCount(); i++) {
						result.add(metaData.getDescriptor(i));
					}
					return result.toArray();
				} catch (OdaException e) {
					throw new RuntimeException(e);
				}
			}
			return super.getElements(inputElement);
		}
	}
	
	private class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIdx) {
			ParameterDescriptor descriptor = (ParameterDescriptor) element;
			String defValue = (parameterDefaultValues != null) ? parameterDefaultValues.get(descriptor.getParameterName()) : null;
			switch (columnIdx) {
				case 0: return ((ParameterDescriptor)element).getParameterName();
				case 1: return ((ParameterDescriptor)element).getDataTypeName() + "("+((ParameterDescriptor)element).getRealDataTypeName()+")"; //$NON-NLS-1$ //$NON-NLS-2$
				case 2: return defValue != null ? defValue : ""; //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}
	}
	
	private class DefaultValueCellEditor extends DialogCellEditor {

		public DefaultValueCellEditor(org.eclipse.swt.widgets.Table table) {
			super(table);
		}
		
		@Override
		protected Object openDialogBox(Control parent) {
			ParameterDescriptor descriptor = table.getFirstSelectedElement();			
			String initExpression = parameterDefaultValues.get(descriptor.getParameterName());
			ExpressionBuilder expressionBuilder = new ExpressionBuilder(initExpression != null ? initExpression : ""); //$NON-NLS-1$
//			ScalarParameterHandle handle = (ScalarParameterHandle) ElementProcessorFactory.createProcessor( "ScalarParameter" ).createElement( null );
//			expressionBuilder.setExpressionProvier(new ParameterExpressionProvider(
//					handle, dataSetDesign.getName())
//			);
			if (expressionBuilder.open( ) == Window.OK){
				return expressionBuilder.getResult().trim();
			}
			return null;
		}
		
	}
	

	private ParameterMetaData parameterMetaData;
	private Map<String, String> parameterDefaultValues;
	private DataSetDesign dataSetDesign;

	private XComposite wrapper; 
	private Table table;

	/**
	 * @param arg0
	 */
	public JFSParameterWizardPage(String name) {
		super(name);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JFSParameterWizardPage(String name, String title,
			ImageDescriptor icon) {
		super(name, title, icon);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#createPageCustomControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageCustomControl(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		table = new Table(wrapper, SWT.NONE);
		setControl(wrapper);
	}


	@Override
	protected void refresh(DataSetDesign design) {
		super.refresh(design);
		this.dataSetDesign = design;
		getParameterMetaData(dataSetDesign);
		getParameterDefaultValues(dataSetDesign);
		table.setInput(parameterMetaData);
		logDesignParameters("refresh: ", design); //$NON-NLS-1$
	}

	private void logDesignParameters(String prefix, DataSetDesign design) {
		EList paramDefns = design.getParameters().getParameterDefinitions();
		for (Iterator iter = paramDefns.iterator(); iter.hasNext();) {			
			ParameterDefinition definition = (ParameterDefinition) iter.next();
			logger.info(prefix + "Found parameter with InOutMode: " + definition.getInOutMode().toString());			 //$NON-NLS-1$
			DataElementAttributes dataElementAttributes = definition.getAttributes();
			InputParameterAttributes attrs = definition.getInputAttributes();
			InputElementAttributes elementAttributes = attrs.getElementAttributes();			
			logger.info(prefix + "Parameter name: " + dataElementAttributes.getName()); //$NON-NLS-1$
			logger.info(prefix + "Parameter allowsNull: " + dataElementAttributes.allowsNull()); //$NON-NLS-1$
			logger.info(prefix + "Parameter defaultValue: " + elementAttributes.getDefaultScalarValue()); //$NON-NLS-1$
		}
	}

	protected ParameterMetaData getParameterMetaData(DataSetDesign design) {
		if (parameterMetaData != null)
			return parameterMetaData;
		JFSQueryPropertySet queryPropertySet = JFSQueryUtil.createPropertySetFromQueryString(design.getQuery().getQueryText());
		ScriptRegistryItemID scriptID = queryPropertySet.getScriptRegistryItemID();
		ScriptParameterSet parameterSet = ScriptParameterSetDAO.sharedInstance().getScriptParameterSet(
				scriptID, ScriptParameterSetDAO.DEFAULT_FETCH_GROUPS, new NullProgressMonitor()
			);
		parameterMetaData = null;
		try {
			parameterMetaData = ParameterMetaData.createMetaDataFromParameterSet(parameterSet);
		} catch (JFireReportingOdaException e) {
			throw new RuntimeException(e);
		}
		return parameterMetaData;
	}
	
	protected void getParameterDefaultValues(DataSetDesign design) {
		if (design.getParameters() == null)
			return;
		EList paramDefns = design.getParameters().getParameterDefinitions();
		for (Iterator iter = paramDefns.iterator(); iter.hasNext();) {			
			ParameterDefinition definition = (ParameterDefinition) iter.next();
			if (parameterDefaultValues == null)
				parameterDefaultValues = new HashMap<String, String>();
			DataElementAttributes dataElementAttributes = definition.getAttributes();
			InputParameterAttributes attrs = definition.getInputAttributes();
			InputElementAttributes elementAttributes = attrs.getElementAttributes();			
			parameterDefaultValues.put(dataElementAttributes.getName(), elementAttributes.getDefaultScalarValue());
		}
	}
	
	protected void setParameterDefaultValues(DataSetDesign design) {
		for (Iterator iter = design.getParameters().getParameterDefinitions().iterator(); iter.hasNext();) {			
			ParameterDefinition definition = (ParameterDefinition) iter.next();
			if (parameterDefaultValues == null)
				parameterDefaultValues = new HashMap<String, String>();
			DataElementAttributes dataElementAttributes = definition.getAttributes();
			InputParameterAttributes attrs = definition.getInputAttributes();
			InputElementAttributes elementAttributes = null;
			if (attrs == null) {
				attrs = DesignFactory.eINSTANCE.createInputParameterAttributes();
				definition.setInputAttributes(attrs);
				attrs.setElementAttributes(DesignFactory.eINSTANCE.createInputElementAttributes());
			}
			elementAttributes = attrs.getElementAttributes();
			String defaultValue = parameterDefaultValues.get(dataElementAttributes.getName());
			if (defaultValue == null || "".equals(defaultValue)) //$NON-NLS-1$
				continue; 
			elementAttributes.setDefaultScalarValue(defaultValue);
		}
	}
	
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		DataSetDesign superResult = super.collectDataSetDesign(design);
		IParameterMetaData metaData = getParameterMetaData(superResult);
		DataSetParameters params = null;
		try {
			params = createDataSetParameters(metaData);
		} catch (OdaException e) {
			throw new RuntimeException(e);
		}
		superResult.setParameters(params);
		setParameterDefaultValues(superResult);
		logDesignParameters("collectDataSetDesign: ", design); //$NON-NLS-1$
		return superResult;		
	}

	public static DataSetParameters createDataSetParameters( 
			IParameterMetaData pmd)
	throws OdaException
	{
		DataSetParameters params = DesignSessionUtil.toDataSetParametersDesign(pmd);
		EList pDefinitions = params.getParameterDefinitions();
		for (Iterator iter = pDefinitions.iterator(); iter.hasNext();) {			
			ParameterDefinition definition = (ParameterDefinition) iter.next();
			definition.getAttributes().setName(pmd.getParameterName(definition.getAttributes().getPosition()));
			definition.getAttributes().setNullability(ElementNullability.NULLABLE_LITERAL);
		}
		return params;
	}

	public boolean canModify(Object element, String property) {
		return PROPERTY_DEFAULT_VALUE.equals(property);
	}

	public Object getValue(Object element, String property) {
		ParameterDescriptor descriptor = (ParameterDescriptor) element;
		if (parameterDefaultValues == null)
			return null;
		return parameterDefaultValues.get(descriptor.getParameterName());
	}

	public void modify(Object element, String property, Object value) {
		TableItem tableItem = (TableItem)element;
		ParameterDescriptor descriptor = (ParameterDescriptor) tableItem.getData();
		if (parameterDefaultValues == null)
			parameterDefaultValues = new HashMap<String, String>();
		parameterDefaultValues.put(descriptor.getParameterName(), (String) value);
		table.refresh();
	}
}

