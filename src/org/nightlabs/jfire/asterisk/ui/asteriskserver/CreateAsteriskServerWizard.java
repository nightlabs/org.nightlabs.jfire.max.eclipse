package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.dao.AsteriskServerDAO;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.id.PhoneSystemID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateAsteriskServerWizard
extends DynamicPathWizard
implements INewWizard
{
	private AsteriskServer newAsteriskServer;
	
	public CreateAsteriskServerWizard()
	{
		setWindowTitle(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CreateAsteriskServerWizard.windowTitle")); //$NON-NLS-1$
		newAsteriskServer = new AsteriskServer(IDGenerator.getOrganisationID(), IDGenerator.nextIDString(PhoneSystem.class));
	}

	private CreateAsteriskServerWizardPage createAsteriskServerWizardPage;
	@Override
	public void addPages() {
		createAsteriskServerWizardPage = new CreateAsteriskServerWizardPage(newAsteriskServer);
		addPage(createAsteriskServerWizardPage);
	}
	
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException {

					AsteriskServer asteriskServer = AsteriskServerDAO.sharedInstance().storeAsteriskServer(newAsteriskServer, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					AsteriskServerEditorInput editorInput = new AsteriskServerEditorInput((PhoneSystemID)JDOHelper.getObjectId(asteriskServer));
					try {
						Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, AsteriskServerEditor.EDITOR_ID);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		//Do nothing!!
	}
	
	private static String[] FETCH_GROUP = new String[]{
		FetchPlan.DEFAULT,
		AsteriskServer.FETCH_GROUP_CALL_FILE_PROPERTIES,
		AsteriskServer.FETCH_GROUP_NAME
	};
}