package menuClasses;

import java.io.IOException;

import ioManagementClasses.IOComponent;
import systemClasses.SystemController;

public class RemoveDocumentAction implements Action {

	@Override
	public void execute(Object arg) {
		// TODO Auto-generated method stub

		SystemController sc = (SystemController) arg;
		IOComponent io = IOComponent.getComponent();
		io.output("\nRemoving a document from the index system:\n");
		String docName = io.getInput("\nEnter name of the document: ").trim();
		String statusMSG = null;
		try {
			statusMSG = sc.removeDocumentFromIndex(docName);
			io.output(statusMSG);
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

}