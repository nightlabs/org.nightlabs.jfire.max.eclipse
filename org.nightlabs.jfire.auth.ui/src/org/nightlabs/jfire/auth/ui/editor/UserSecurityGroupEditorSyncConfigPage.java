package org.nightlabs.jfire.auth.ui.editor;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.security.UserSecurityGroup;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;

/**
 * Page for editing mapping for sychronization of {@link UserSecurityGroup}s to some {@link UserManagementSystem}. 
 * 
 * Adds two sections: 
 * {@link UserSecurityGroupSyncConfigGenericSection} for editing general properties like host, port and name;
 * {@link UserSecurityGroupSyncConfigSpecificSection} which corresponds to selected {@link UserManagementSystem} 
 * and shows a {@link UserSecurityGroupSyncConfigSpecificComposite} inside depending on selected {@link UserManagementSystem}.
 * These composite is created via "org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping" extension point.
 * 
 * Page controller is {@link UserSecurityGroupEditorSyncConfigPageController}.  
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 * 
 */
public class UserSecurityGroupEditorSyncConfigPage extends EntityEditorPageWithProgress{
	
	public static final String ID_PAGE = UserSecurityGroupEditorSyncConfigPage.class.getName();
	
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link UserSecurityGroupEditorSyncConfigPage} and {@link UserSecurityGroupEditorSyncConfigPageController}.
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new UserSecurityGroupEditorSyncConfigPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new UserSecurityGroupEditorSyncConfigPageController(editor);
		}
		
	}
	
	private UserSecurityGroupSyncConfigGenericSection genericSection;
	private UserSecurityGroupSyncConfigSpecificSection specificSection;
	
	/**
	 * {@link ISelectionChangedListener} which is responsible for switching content in {@link UserSecurityGroupSyncConfigSpecificSection}
	 * depending on selected {@link UserManagementSystem}s in {@link UserSecurityGroupSyncConfigGenericSection}.
	 */
	private ISelectionChangedListener selectionChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			Collection<UserSecurityGroupSyncConfig<?, ?>> selectedSyncConfigs = genericSection.getSelectedSyncConfigs();
			if (selectedSyncConfigs.size() == 1){
				specificSection.updateSpecificComposite(selectedSyncConfigs.iterator().next());
			}else{
				specificSection.clearSpecificComposite();
			}
		}
	};
	
	/**
	 * Create an instance of {@link UserSecurityGroupEditorSyncConfigPage}.
	 * This constructor is used by the entity editor page extension system.
	 * 
	 * @param editor The editor for which to create this form page.
	 */
	public UserSecurityGroupEditorSyncConfigPage(FormEditor editor){
		super(editor, ID_PAGE, "External synchronization");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSections(final Composite parent) {
		genericSection = new UserSecurityGroupSyncConfigGenericSection(this, parent);
		getManagedForm().addPart(genericSection);
		specificSection = new UserSecurityGroupSyncConfigSpecificSection(this, parent);
		getManagedForm().addPart(specificSection);
		
		genericSection.addSelectionChangedListener(selectionChangeListener);
		genericSection.getContainer().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				genericSection.removeSelectionChangedListener(selectionChangeListener);
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPageFormTitle() {
		return "Edit synchronization options for UserSecurityGroup";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configurePageWrapper(Composite pageWrapper) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 10;
		layout.marginTop = 5;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.horizontalSpacing = 10;
		pageWrapper.setLayout(layout);
	}
}
