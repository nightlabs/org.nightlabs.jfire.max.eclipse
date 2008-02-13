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

package org.nightlabs.jfire.trade.ui.legalentity.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.StructFieldID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.config.LegalEntityViewConfigModule;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LEViewPersonStructFieldTable extends AbstractTableComposite {

	private static class ContentProvider implements IStructuredContentProvider {
		
		private List<String> cfModFields = new ArrayList<String>();
		private LegalEntityViewConfigModule inputCfMod;
		
		public Object[] getElements(Object inputElement) {
			if (! (inputElement instanceof LegalEntityViewConfigModule))
				return null;
			LegalEntityViewConfigModule cfMod = (LegalEntityViewConfigModule)inputElement;
			if ((cfMod != null) && (cfMod != inputCfMod)) {
				cfModFields.clear();
				cfModFields.addAll(cfMod.getStructFields());
				inputCfMod = cfMod;
			}
			return cfModFields.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
		public void add(String structFieldOID)
		{
			cfModFields.add(structFieldOID);
		}
		
		public void moveUp(String structFieldOID) {
			int i = cfModFields.indexOf(structFieldOID);
			if (i <= 0 )
				return;
			int newIdx = i-1;
			cfModFields.remove(i);
			cfModFields.add(newIdx,structFieldOID);
		}
		
		public void moveDown(String structFieldOID) {
			int i = cfModFields.indexOf(structFieldOID);
			if ((i >= cfModFields.size()-1) || (i < 0))
				return;
			int newIdx = i+1;
			cfModFields.remove(i);
			cfModFields.add(newIdx,structFieldOID);
		}
		
		public void remove(String structFieldOID) {
			cfModFields.remove(structFieldOID);
		}
		
		public List getCfModFields() {
			return cfModFields;
		}
	}
	
	private static class LabelProvider extends org.eclipse.jface.viewers.LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			StructFieldID fieldID = null;
			try {
				fieldID = new StructFieldID((String)element);
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			IStruct struct = StructLocalDAO.sharedInstance().getStructLocal(Person.class, LegalEntity.PROPERTY_SET_SCOPE, new NullProgressMonitor());
			
			StructField field = null;
			try {
				field = struct.getStructField(
					fieldID.structBlockOrganisationID,
					fieldID.structBlockID,
					fieldID.structFieldOrganisationID,
					fieldID.structFieldID
				);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return field.getName().getText(Locale.getDefault().getLanguage());
		}
	}
	
	
	/**
	 * 
	 */
	public LEViewPersonStructFieldTable(Composite parent, int style) {
		super(parent, style, true);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
	
	public void moveSelectedUp() {
		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		if ( selection.size() != 1)
			return;
		((ContentProvider)tableViewer.getContentProvider()).moveUp((String)selection.getFirstElement());
	}
	
	public void moveSelectedDown() {
		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		if ( selection.size() != 1)
			return;
		((ContentProvider)tableViewer.getContentProvider()).moveDown((String)selection.getFirstElement());
	}
	
	public void removeSelected() {
		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		if ( selection.size() != 1)
			return;
		((ContentProvider)tableViewer.getContentProvider()).remove((String)selection.getFirstElement());
	}
	
	public List getStructFields() {
		return ((ContentProvider)tableViewer.getContentProvider()).getCfModFields();
	}
}
