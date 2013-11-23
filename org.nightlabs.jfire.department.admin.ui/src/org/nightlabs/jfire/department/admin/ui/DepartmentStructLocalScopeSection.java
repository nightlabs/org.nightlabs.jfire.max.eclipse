///**
// *
// */
//package org.nightlabs.jfire.department.admin.ui;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.ui.forms.editor.IFormPage;
//import org.nightlabs.base.ui.composite.XComposite;
//import org.nightlabs.base.ui.editor.ToolBarSectionPart;
//import org.nightlabs.jfire.department.Department;
//import org.nightlabs.jfire.departmenttracking.ui.resource.Messages;
//import org.nightlabs.jfire.prop.StructLocal;
//import org.nightlabs.jfire.prop.dao.StructLocalDAO;
//import org.nightlabs.progress.NullProgressMonitor;
//
///**
// * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
// *
// */
//public class DepartmentStructLocalScopeSection extends ToolBarSectionPart {
//
//	private String structScope;
//	private String structLocalScope;
//	private Text structLocalScopeText;
//
//	/**
//	 * @param page
//	 * @param parent
//	 * @param style
//	 * @param title
//	 */
//	public DepartmentStructLocalScopeSection(IFormPage page, Composite parent, int style)
//	{
//		super(page, parent, style, "Name"); //$NON-NLS-1$
//		updateToolBarManager();
//		structLocalScopeText = new Text(getContainer(), XComposite.getBorderStyle(getContainer()) | SWT.SINGLE | SWT.READ_ONLY);
//		structLocalScopeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//	}
//
//	public void setDepartment(Department department) {
//		structScope = department.getPropertySet().getStructScope();
//		structLocalScope = department.getPropertySet().getStructLocalScope();
//		StructLocal sl = StructLocalDAO.sharedInstance().getStructLocal(
//				department.getPropertySet().getStructLocalObjectID(),
////				Department.class, structScope, structLocalScope,
//				new NullProgressMonitor()
//		);
//		if (sl != null && sl.getName() != null) {
//			structLocalScopeText.setText(sl.getName().getText());
//		} else {
//			structLocalScopeText.setText(structLocalScope);
//		}
//	}
//
//}