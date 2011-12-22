package org.nightlabs.jfire.auth.ui.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.jfire.auth.ui.UserManagementSystemUIMappingRegistry;
import org.nightlabs.jfire.auth.ui.editor.UserSecurityGroupSyncConfigSpecificComposite.SyncConfigChangedListener;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;

/**
 * Section of {@link UserSecurityGroupEditorSyncConfigPage} which displays {@link UserSecurityGroupSyncConfigSpecificComposite}
 * for selected {@link UserManagementSystem} type in {@link UserSecurityGroupSyncConfigGenericSection}. It gets a composite to
 * display via "org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping" extension point. It is expected that all editor UI 
 * will be contained inside {@link UserSecurityGroupSyncConfigSpecificComposite} but not in this section itself. All generic
 * options should be edited in {@link UserSecurityGroupSyncConfigGenericSection}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserSecurityGroupSyncConfigSpecificSection extends ToolBarSectionPart {

	/**
	 * Default label to be displayed when no {@link UserSecurityGroupSyncConfigSpecificComposite} is selected
	 */
	private Label defaultLabel;
	
	/**
	 * {@link UserSecurityGroupSyncConfigSpecificComposite} which is currently displayed on top
	 */
	private UserSecurityGroupSyncConfigSpecificComposite currentComposite;
	
	/**
	 * Simple {@link Map} cache to hold {@link UserSecurityGroupSyncConfigSpecificComposite}s which were already created
	 */
	private Map<Class<? extends UserManagementSystemType<?>>, UserSecurityGroupSyncConfigSpecificComposite> compositeCache;

	/**
	 * Simple listener which marks this secion dirty when object is changed inside {@link UserSecurityGroupSyncConfigSpecificComposite}
	 */
	private SyncConfigChangedListener listener = new SyncConfigChangedListener() {
		@Override
		public void objectChanged(UserSecurityGroupSyncConfig<?, ?> changed) {
			markDirty();
		}
	};

	private StackLayout stackLayout;
	private Composite wrapper;
	private FormToolkit toolkit;

	public UserSecurityGroupSyncConfigSpecificSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "General");
		compositeCache = new HashMap<Class<? extends UserManagementSystemType<?>>, UserSecurityGroupSyncConfigSpecificComposite>();
		toolkit = page.getEditor().getToolkit();
		createContents(getSection(), toolkit);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit(boolean onSave) {
		if (currentComposite != null && !currentComposite.isDisposed()){
			currentComposite.commitChanges();
		}
		super.commit(onSave);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		if (currentComposite != null && !currentComposite.isDisposed()){
			currentComposite.refresh();
		}
		super.refresh();
	}
	
	/**
	 * Update section content by given {@link UserSecurityGroupSyncConfig} which means getting {@link UserSecurityGroupSyncConfigSpecificComposite}
	 * by related {@link UserManagementSystem} type and updating it with given {@link UserSecurityGroupSyncConfig} data.
	 * 
	 * @param syncConfig {@link UserSecurityGroupSyncConfig} instance to be edited with {@link UserSecurityGroupSyncConfigSpecificComposite}
	 */
	@SuppressWarnings("unchecked")
	public void updateSpecificComposite(UserSecurityGroupSyncConfig<?, ?> syncConfig) {
		Class<? extends UserManagementSystemType<?>> userManagementSystemTypeClass = (Class<? extends UserManagementSystemType<?>>) syncConfig.getUserManagementSystem().getType().getClass();
		
		UserSecurityGroupSyncConfigSpecificComposite topComposite = compositeCache.get(userManagementSystemTypeClass);
		if (topComposite == null) {
			IUserSecurityGroupSyncConfigDelegate delegate = UserManagementSystemUIMappingRegistry.sharedInstance().getUserGroupSyncConfigDelegate(
					userManagementSystemTypeClass);
			if (delegate != null){
				topComposite = delegate.createEditorComposite(wrapper, SWT.NONE, toolkit);
				if (topComposite != null){
					final UserSecurityGroupSyncConfigSpecificComposite newTopComposite = topComposite;
					newTopComposite.addSyncConfigChangedListener(listener);
					newTopComposite.addDisposeListener(new DisposeListener() {
						@Override
						public void widgetDisposed(DisposeEvent event) {
							newTopComposite.removeSyncConfigChangedListener(listener);
						}
					});
					compositeCache.put(userManagementSystemTypeClass, newTopComposite);
				}
			}
		}
		if (topComposite != null && topComposite != currentComposite){
			currentComposite = topComposite;
			stackLayout.topControl = topComposite;
			topComposite.setCompositeInput(syncConfig);
		}else if (topComposite == null){
			currentComposite = null;
			stackLayout.topControl = defaultLabel;
		}
		wrapper.layout(true, true);
	}
	
	/**
	 * Shows default label and sets current {@link UserSecurityGroupSyncConfigSpecificComposite} to <code>null</code>
	 */
	public void clearSpecificComposite(){
		currentComposite = null;
		stackLayout.topControl = defaultLabel;
		wrapper.layout(true, true);
	}
	
	private void createContents(Section section, FormToolkit toolkit){
		
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite parent = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		GridLayout parentLayout = (GridLayout) parent.getLayout();
		parentLayout.verticalSpacing = 10;
		parentLayout.marginTop = 10;
		parentLayout.marginRight = 20;
		
		wrapper = toolkit.createComposite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		stackLayout.marginWidth = 0;
		stackLayout.marginHeight = 0;
		wrapper.setLayout(stackLayout);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_BOTH);
		gd.minimumWidth = 600;
		gd.widthHint = 600;
		wrapper.setLayoutData(gd);

		defaultLabel = toolkit.createLabel(wrapper, "Please select one of User management system in order to configure sync properties");
		
		stackLayout.topControl = defaultLabel;
	}

}
