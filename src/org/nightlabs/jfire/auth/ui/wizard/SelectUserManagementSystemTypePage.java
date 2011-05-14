package org.nightlabs.jfire.auth.ui.wizard;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.auth.ui.JFireAuthUIPlugin;
import org.nightlabs.jfire.auth.ui.UserManagementSystemUIMappingRegistry;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * Page for selecting {@link UserManagementSystemType} for creating new {@link UserManagementSystem}. Contributed to {@link CreateUserManagementSystemWizard}
 * by {@link CreateUserManagementSystemWizardHop}.
 * 
 * New wizard pages are added dynamically to {@link CreateUserManagementSystemWizard} when selecting specific {@link UserManagementSystemType}.
 * See {@link #builderSelectListener}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class SelectUserManagementSystemTypePage extends WizardHopPage{

	private static Font boldFont;
	
	private IUserManagementSystemBuilderHop currentUserManagementSystemHop;
	private UserManagementSystemType<?> currentUserManagementSystemType;
	
	private Composite mainWrapper;
	private Label loadingLabel;
	
	/**
	 * Default constructor
	 */
	public SelectUserManagementSystemTypePage()	{
		super(SelectUserManagementSystemTypePage.class.getName(), "Select User Management System Type", SharedImages.getWizardPageImageDescriptor(JFireAuthUIPlugin.sharedInstance(), SelectUserManagementSystemTypePage.class));
		setDescription("Please select one of the available User Management System Types and proceed to the next step");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite parent) {
		mainWrapper = new Composite(parent, SWT.NONE);
		mainWrapper.setLayout(new GridLayout(1, false));
		
		loadingLabel = new Label(mainWrapper, SWT.NONE);
		loadingLabel.setText("Loading all usermanagement system types...");
		
		setControl(mainWrapper);
		return mainWrapper;
	}

	/**
	 * This page could not be the last one, it should be followed by specific {@link UserManagementSystem} page(s).
	 */
	@Override
	public boolean canBeLastPage() {
		return false;
	}
	
	/**
	 * Set loaded {@link UserManagementSystemType} objects to display corresponding UI elements.
	 * 
	 * @param allUserManagementSystemTypes
	 */
	public void setUserManagementSystemTypes(List<UserManagementSystemType<?>> allUserManagementSystemTypes) {
		if (mainWrapper == null){
			throw new IllegalStateException("This method should be called after wizard page contents were created!");
		}
		
		Button firstRadioButton = null;
		for (UserManagementSystemType<?> userManagementSystemType : allUserManagementSystemTypes) {
			Button userManagementSystemTypeButton = createButtonAndDescription(mainWrapper, userManagementSystemType);
			if (firstRadioButton == null){
				firstRadioButton = userManagementSystemTypeButton;
			}
		}
		
		if (firstRadioButton != null){
			loadingLabel.dispose();
			firstRadioButton.setSelection(true);
		}else{
			loadingLabel.setText("No User Management System Types exist!");
		}
		mainWrapper.layout();
		
		if (firstRadioButton != null){
			Event e = new Event();
			e.widget = firstRadioButton;
			SelectionEvent selectionEvent = new SelectionEvent(e);
			builderSelectListener.widgetSelected(selectionEvent);
		}
	}

	/**
	 * Get {@link IUserManagementSystemBuilderHop} based on currently selected {@link UserManagementSystemType}.
	 * 
	 * @return {@link IUserManagementSystemBuilderHop} implementation
	 */
	public IUserManagementSystemBuilderHop getUserManagementSystemBuilderHop() {
		return currentUserManagementSystemHop;
	}
	
	/**
	 * Get selected {@link UserManagementSystemType}
	 * 
	 * @return selected {@link UserManagementSystemType}
	 */
	public UserManagementSystemType<?> getSelectedUserManagementSystemType(){
		return currentUserManagementSystemType;
	}
	
	private Button createButtonAndDescription(Composite parent, UserManagementSystemType<?> userManagementSystemType){
		Button userManagementSystemTypeButton = new Button(parent, SWT.RADIO);
		if (boldFont == null){
			FontData[] initialFontData = userManagementSystemTypeButton.getFont().getFontData();
			for (FontData fontData : initialFontData) {
				fontData.setStyle(SWT.BOLD);
			}
			boldFont = new Font(getShell().getDisplay(), initialFontData);
		}
		userManagementSystemTypeButton.setFont(boldFont);
		GridData gd = new GridData();
		gd.verticalIndent = 10;
		userManagementSystemTypeButton.setLayoutData(gd);
		
		String userManagementSystemTypeName = userManagementSystemType.getClass().getSimpleName();
		if (userManagementSystemType.getName() != null){
			userManagementSystemTypeName = userManagementSystemType.getName().getText();
		}
		userManagementSystemTypeButton.setText(userManagementSystemTypeName);
		userManagementSystemTypeButton.setData(userManagementSystemType);
		userManagementSystemTypeButton.addSelectionListener(builderSelectListener);
		
		String userManagementSystemTypeDescription = "Create and configure an instance of LDAP server of specific type";	// TODO remove
		if (userManagementSystemType.getDescription() != null){
			userManagementSystemTypeDescription = userManagementSystemType.getDescription().getText();
		}
		Label descriptionLabel = new Label(parent, SWT.NONE);
		descriptionLabel.setText(userManagementSystemTypeDescription);
		gd = new GridData();
		gd.horizontalIndent = 15;
		descriptionLabel.setLayoutData(gd);
		
		return userManagementSystemTypeButton;
	}

	private SelectionListener builderSelectListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		@SuppressWarnings("unchecked")
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() instanceof Button){
				
				UserManagementSystemType<?> selectedUserManagementSystemType = (UserManagementSystemType<?>) ((Button) e.getSource()).getData();
				if (selectedUserManagementSystemType == null){
					return;
				}
				currentUserManagementSystemType = selectedUserManagementSystemType;
				
				if (currentUserManagementSystemHop != null) {
					if (getWizard() instanceof DynamicPathWizard) {
						DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
						wiz.removeDynamicWizardPage(currentUserManagementSystemHop.getEntryPage());
					}
				}
				
				currentUserManagementSystemHop = UserManagementSystemUIMappingRegistry.sharedInstance().getUserManagementSystemBuilderWizardHop(
						(Class<? extends UserManagementSystemType<?>>) currentUserManagementSystemType.getClass()
						);
				if (currentUserManagementSystemHop != null) {
					if (getWizard() instanceof DynamicPathWizard) {
						DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
						wiz.addDynamicWizardPage(currentUserManagementSystemHop.getEntryPage());
					}
				}
				getContainer().updateButtons();
			}
		}
		
	};

}
