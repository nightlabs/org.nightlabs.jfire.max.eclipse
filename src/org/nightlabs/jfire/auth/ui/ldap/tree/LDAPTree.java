package org.nightlabs.jfire.auth.ui.ldap.tree;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTreeEntry.BindCredentials;
import org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTreeEntry.LDAPTreeEntryLoadCallback;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttributeSet;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider;
import org.nightlabs.util.CollectionUtil;

/**
 * Tree of LDAP entries. Either {@link ILDAPConnectionParamsProvider} or root {@link LDAPTreeEntry} objects should
 * be passed as input for this tree when calling {@link #setInput(Object)}. In case root {@link LDAPTreeEntry} objects are set
 * they SHOULD have {@link ILDAPConnectionParamsProvider} set to each of them.
 * 
 * {@link LDAPTree} handles load entry events and shows {@link LDAPBindCredentialsDialog} in case attemted LDAP directory has no 
 * anonymous access. Actual communication with LDAP directory happens inside {@link LDAPTreeEntry} class.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPTree extends AbstractTreeComposite<LDAPTreeEntry> implements LDAPTreeEntryLoadCallback{
	
	/**
	 * {@inheritDoc}
	 */
	public LDAPTree(Composite parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public LDAPTree(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * {@inheritDoc}
	 */
	public LDAPTree(Composite parent, boolean init) {
		super(parent, init);
	}

	/**
	 * {@inheritDoc}
	 */
	public LDAPTree(Composite parent, int style, boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, style, setLayoutData, init, headerVisible);
	}

	/**
	 * {@inheritDoc}
	 */
	public LDAPTree(Composite parent, int style, boolean setLayoutData, boolean init, boolean headerVisible, boolean sortColumns) {
		super(parent, style, setLayoutData, init, headerVisible, sortColumns);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new LDAPTreeContentProvider());
		treeViewer.setLabelProvider(new LDAPTreeLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		new TreeColumn(tree, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTree.treeColumnLDAPEntries")); //$NON-NLS-1$
		tree.setLayout(new WeightedTableLayout(new int[]{100}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void treeEntryLoaded(final LDAPTreeEntry loadedEntry) {
		if (!isDisposed()){
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (getInput() instanceof ILDAPConnectionParamsProvider){
						setInput(loadedEntry.getChildren(null));
					}else{
						refresh(loadedEntry, true);
					}
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hadleTreeEntryLoadError(Throwable cause) {
		if (cause instanceof LoginException){
			
			if (!isDisposed()){
				getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {

						// showing dialog for entering bind login/password
						LDAPBindCredentialsDialog dlg = new LDAPBindCredentialsDialog(RCPUtil.getActiveShell(), null);
						if (Window.OK == dlg.open()){
							final String login = dlg.getLogin();
							final String password = dlg.getPassword();
							((LDAPTreeContentProvider) getTreeViewer().getContentProvider()).setBindCredentials(new BindCredentials() {
								@Override
								public String getPassword() {
									return password;
								}
								@Override
								public String getLogin() {
									return login;
								}
							});
							refresh();
						}
						
					}
				});
			}
			
		}else{
			throw new RuntimeException(cause);
		}
	}


	private static final String OBJECT_CLASS_ATTRIBUTE = "objectClass"; //$NON-NLS-1$

	class LDAPTreeContentProvider extends TreeContentProvider{
		
		private ILDAPConnectionParamsProvider ldapConnectionParamsProvider;
		private LDAPTreeEntry[] rootEntries;
		private BindCredentials bindCredentials;
		
		@Override
		public Object[] getChildren(final Object parentElement) {
			if (parentElement instanceof LDAPTreeEntry){
				LDAPTreeEntry[] children = ((LDAPTreeEntry) parentElement).getChildren(bindCredentials, LDAPTree.this);
				if (children != null){
					return children;
				}
				return new String[]{Messages.getString("org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTree.loadingText")}; //$NON-NLS-1$
			}
			return super.getChildren(parentElement);
		}
		
		public boolean hasChildren(Object element) {
			return true;
		}

		@Override
		public Object[] getElements(Object obj) {
			if (rootEntries == null){
				LDAPTreeEntry rootTreeEntry = new LDAPTreeEntry("", new String[]{OBJECT_CLASS_ATTRIBUTE}); //$NON-NLS-1$
				rootTreeEntry.setLdapConnectionParamsProvider(ldapConnectionParamsProvider);
				return getChildren(rootTreeEntry);
			}else{
				return rootEntries;
			}
		}
	
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof ILDAPConnectionParamsProvider){
				this.ldapConnectionParamsProvider = (ILDAPConnectionParamsProvider) newInput;
			}else if (newInput instanceof LDAPTreeEntry[]){
				this.rootEntries = (LDAPTreeEntry[]) newInput;
			}else{
				super.inputChanged(viewer, oldInput, newInput);
			}
		}
		
		public void setBindCredentials(BindCredentials bindCredentials) {
			this.bindCredentials = bindCredentials;
		}
	}
	
	class LDAPTreeLabelProvider extends TableLabelProvider{

		private static final String POSIXACCOUNT = "posixAccount"; //$NON-NLS-1$
		private static final String PERSON = "person"; //$NON-NLS-1$
		private static final String ORGANIZATIONALUNIT = "organizationalUnit"; //$NON-NLS-1$
		private static final String ORGANIZATION = "organization"; //$NON-NLS-1$

		@Override
		public String getColumnText(Object obj, int i) {
			if (obj instanceof String){
				return (String) obj;
			}else if (obj instanceof LDAPTreeEntry){
				return ((LDAPTreeEntry) obj).getName();
			}
			return ""; //$NON-NLS-1$
		}
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof LDAPTreeEntry){
				return getImageForEntry((LDAPTreeEntry) element);
			}
			return super.getColumnImage(element, columnIndex);
		}
		
		@Override
		public Image getImage(Object element) {
			if (element instanceof LDAPTreeEntry){
				return getImageForEntry((LDAPTreeEntry) element);
			}
			return super.getImage(element);
		}
		
		private Image getImageForEntry(LDAPTreeEntry entry){
			if (!"".equals(entry.getName()) && entry.hasAttributesLoaded()){ //$NON-NLS-1$
				try {
					LDAPAttributeSet attributes = entry.getAttributes(null);
					if (attributes.containsAnyAttributeValue(OBJECT_CLASS_ATTRIBUTE, CollectionUtil.createHashSet(ORGANIZATIONALUNIT, ORGANIZATION))){
						
						return SharedImages.getSharedImage(
								LdapUIPlugin.sharedInstance(), LDAPTree.class, "treeNodeOrg", ImageDimension._16x16.toString(), ImageFormat.png //$NON-NLS-1$
								);
						
					}else if (attributes.containsAnyAttributeValue(OBJECT_CLASS_ATTRIBUTE, CollectionUtil.createHashSet(PERSON, POSIXACCOUNT))){
						
						return SharedImages.getSharedImage(
								LdapUIPlugin.sharedInstance(), LDAPTree.class, "treeNodeUser", ImageDimension._16x16.toString(), ImageFormat.png //$NON-NLS-1$
								);
						
					}
				} catch (Exception e) {
					// do nothing, just show default image
				}
			}
			return SharedImages.getSharedImage(
					LdapUIPlugin.sharedInstance(), LDAPTree.class, "treeNode", ImageDimension._16x16.toString(), ImageFormat.png //$NON-NLS-1$
					);
		}
		
	}

}
