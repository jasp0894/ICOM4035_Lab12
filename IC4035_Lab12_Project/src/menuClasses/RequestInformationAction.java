package menuClasses;

import ioManagementClasses.IOComponent;
import systemClasses.SystemController;

import java.io.IOException;

import dataManagement.DocsIDManager;

public class RequestInformationAction implements Action {

	
	@Override
	public void execute(Object args) {
		 
		try {
			DocsIDManager didm = DocsIDManager.getInstance();
			SystemController sc = (SystemController) args;
			IOComponent io = IOComponent.getComponent();
			io.output("\nRquesting information regarding document from the index system:\n");
			
			String docName = io.getInput("\nEnter name of document: ").trim(); 
			String docInfo = "";

			if(docName.equals("*")){
				int docID = 1;//docIDs start at 1
				boolean noMoreDocs = false;
				while(!noMoreDocs){
				
					try {
						String dname = didm.getDocName(docID);
						docInfo += sc.getDocInformation(dname);
		
					} catch (Exception e) {
						// TODO: handle exception
						noMoreDocs = true;
					}
					
					
					docID++;
				}
			}else
				docInfo = sc.getDocInformation(docName);
				
			io.output(docInfo);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
