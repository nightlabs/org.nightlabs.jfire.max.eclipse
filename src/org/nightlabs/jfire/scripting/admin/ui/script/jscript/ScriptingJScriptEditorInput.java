/**
 * 
 */
package org.nightlabs.jfire.scripting.admin.ui.script.jscript;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import net.sourceforge.jseditor.util.ExternalFileEditorInput;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.jfire.base.ui.app.JFireApplication;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;
import org.nightlabs.jfire.scripting.ui.ScriptingPlugin;

/**
 * EditorInut that fetches the script from the server and
 * serves as a wrapper to the local copy.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[dot]de>
 *
 */
public class ScriptingJScriptEditorInput implements IPathEditorInput {

	private ExternalFileEditorInput localInput;
	
	private ScriptRegistryItemID registryItemID;
	 
	/**
	 * @param file
	 */
	public ScriptingJScriptEditorInput(ScriptRegistryItemID registryItemID) {
		this.registryItemID = registryItemID;
	}
	
	private ExternalFileEditorInput getLocalInput() {
		if (localInput == null) {
			// TODO remove NullProgressMonitor
			Script script = ScriptRegistryItemProvider.sharedInstance().getScript(
					registryItemID, new NullProgressMonitor());
			File pathFile = new File(JFireApplication.getRootDir()+File.separator+"scripts_tmp");
			if (!pathFile.exists()) {
				if (!pathFile.mkdirs())
					throw new IllegalStateException("Could not create directory for temporary remote layouts: "+pathFile.getPath());
			}
			File file = new File(pathFile, "Script_"+script.getOrganisationID()+"_"+script.getScriptRegistryItemID()+".script");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					throw new IllegalStateException("Could not create temporary file for remote layout: "+file.getAbsolutePath(), e);
				}
			}
			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				if (script.getText() != null)
					out.write(script.getText().getBytes());
				else
					out.write(" test script".getBytes());
				out.close();
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("Could not find temporary file for remote layout: "+file.getAbsolutePath());
			} catch (IOException e) {
				throw new RuntimeException("Could not write temporary file for remote layout: "+file.getAbsolutePath(), e);
			}
			localInput = new ExternalFileEditorInput(file);			
		}
		return localInput;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return getLocalInput().equals(o);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#exists()
	 */
	public boolean exists() {
		return getLocalInput().exists();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return getLocalInput().getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return getLocalInput().getImageDescriptor();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#getName()
	 */
	public String getName() {
		return getLocalInput().getName();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#getPath()
	 */
	public IPath getPath() {
		return getLocalInput().getPath();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		return getLocalInput().getPath(element);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return getLocalInput().getPersistable();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return getLocalInput().getToolTipText();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.jscript.ExternalFileEditorInput#hashCode()
	 */
	public int hashCode() {
		return getLocalInput().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getLocalInput().toString();
	}
	
	public static void saveScript(ScriptingJScriptEditorInput input, IDocument document) 
	{
		// TODO remove NullProgressMonitor
		Script script = ScriptRegistryItemProvider.sharedInstance().getScript(
				input.registryItemID, new NullProgressMonitor());
		// TODO: load script from file? editor?
		script.setText(document.get());
		try {
			ScriptingPlugin.getScriptManager().storeRegistryItem(script, false, null, -1);
		} catch (RemoteException e) {
			throw new RuntimeException("Failed to remotely store ReportLayout", e);
		}
	}

}
