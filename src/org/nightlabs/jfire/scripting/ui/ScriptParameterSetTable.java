/**
 *
 */
package org.nightlabs.jfire.scripting.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.util.NLLocale;

/**
 * A table displaying a Collection ScriptParameterSets.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptParameterSetTable extends AbstractTableComposite<ScriptParameterSet> {

//	private static class ContentProvider extends TableContentProvider {
//		@Override
//		public Object[] getElements(Object inputElement) {
//			if (inputElement instanceof Collection)
//				return ((Collection)inputElement).toArray();
//			return super.getElements(inputElement);
//		}
//	}

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ScriptParameterSet) {
				return ((ScriptParameterSet)element).getName().getText(NLLocale.getDefault().getLanguage());
			}
			else if (element instanceof String)
				return (String)element;
			return ""; //$NON-NLS-1$
		}
		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}
	}

	/**
	 * @param parent
	 * @param style
	 */
	public ScriptParameterSetTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public ScriptParameterSetTable(Composite parent, int style, boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public ScriptParameterSetTable(Composite parent, int style,
			boolean initTable, int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

}
