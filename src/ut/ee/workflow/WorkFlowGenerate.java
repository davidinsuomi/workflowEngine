package ut.ee.workflow;

import ut.ee.workflow.object.WorkFlowProcess;
import ut.ee.workflow.object.PartnerLink;
import ut.ee.workflow.object.WorkFlowActivity;
import ut.ee.workflow.object.WorkFlowAssign;
import ut.ee.workflow.object.WorkFlowInvoke;
import ut.ee.workflow.object.WorkFlowVariable;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class WorkFlowGenerate {
	private Map<String,ArrayList<String>> graphMap;
	private Map<String,ArrayList<String>> graphMapBackword;
	private Map<String,WorkFlowActivity> activityMap;
	private ArrayList<WorkFlowVariable> variables;
	private ArrayList<PartnerLink> partnerLinks;
	
	private Map<String,ArrayList<String>> offloadingGraphMap;
	private Map<String,ArrayList<String>> offloadingGraphMapBackword;
	private Map<String,WorkFlowActivity> offloadingActivityMap;
	private ArrayList<WorkFlowVariable> offloadingVariables;
	private ArrayList<PartnerLink> offloadingPartnerLinks;
	
	private static StringWriter writer = new StringWriter();
	private XmlSerializer xmlSerializer = Xml.newSerializer();
	private static String TAG = "GENERATEXML";
	
	public WorkFlowGenerate(WorkFlowProcess workflowProcess){
		graphMap = workflowProcess.graphMap;
		graphMapBackword = workflowProcess.graphMapBackword;
		activityMap = workflowProcess.activityMap;
		variables = workflowProcess.variables;	
		partnerLinks = workflowProcess.partnerLinks;
	}
	private void InitializeXmlSerializer() throws IllegalArgumentException, IllegalStateException, IOException{
		xmlSerializer.setOutput(writer);
	    //Start Document
	    xmlSerializer.startDocument("UTF-8", true); 
	    xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
	    //Open Tag <file>
	    xmlSerializer.startTag("", "process");
		
	}
	private void FinalizeXmlSerializer() throws IllegalArgumentException, IllegalStateException, IOException{
	    xmlSerializer.endDocument();
		Log.e(TAG, writer.toString());
		
	}
	//So far only to able to offloading sequenceTask
	public void TaskToBeOffloading(String startTask, String endTask) throws IllegalArgumentException, IllegalStateException, IOException{
		InitializeXmlSerializer();
		if(startTask.equals(endTask)){
			//only one task need to offloading
			xmlSerializer.startTag("", "sequence");
			CreateCurrentXMLTag(startTask);
			xmlSerializer.endTag("", "sequence");
			xmlSerializer.endTag("", "process");
		}else{
			xmlSerializer.startTag("", "sequence");
			while(!startTask.equals(graphMap.get(endTask).get(0))){
				CreateCurrentXMLTag(startTask);
				startTask = graphMap.get(startTask).get(0);
			}
			xmlSerializer.endTag("", "sequence");
			xmlSerializer.endTag("", "process");
		}
		
		FinalizeXmlSerializer();
	}
	private void CreateCurrentXMLTag(String currentTag) throws IllegalArgumentException, IllegalStateException, IOException{
		WorkFlowActivity activity = activityMap.get(currentTag);
		if(activity instanceof WorkFlowInvoke){
			WorkFlowInvoke invoke = (WorkFlowInvoke) activity;
			CreateInvoke(invoke);
		}else if(activity instanceof WorkFlowAssign){
			WorkFlowAssign assign = (WorkFlowAssign) activity;
			CreateAssign(assign);
		}
	}
	private void CreateAssign(WorkFlowAssign workFlowAssign) throws IllegalArgumentException, IllegalStateException, IOException{
		xmlSerializer.startTag("", "assign");
		xmlSerializer.attribute("", "name" , workFlowAssign.name);
		xmlSerializer.startTag("", "copy");
		xmlSerializer.startTag("", "from");
		xmlSerializer.attribute("", "variable", workFlowAssign.from);
		xmlSerializer.endTag("", "from");
		xmlSerializer.startTag("", "to");
		xmlSerializer.attribute("", "variable", workFlowAssign.to);
		xmlSerializer.endTag("", "to");
		xmlSerializer.endTag("", "copy");
		xmlSerializer.endTag("", "assign");
	}
	
	private void CreateInvoke(WorkFlowInvoke workFlowInvoke) throws IllegalArgumentException, IllegalStateException, IOException{
		xmlSerializer.startTag("", "invoke");
		xmlSerializer.attribute("", "name", workFlowInvoke.name);
		xmlSerializer.attribute("", "partnerLink", workFlowInvoke.partnerLink);
		xmlSerializer.attribute("", "operation", workFlowInvoke.operation);
		xmlSerializer.attribute("", "inputVariable", workFlowInvoke.inputVariable);
		xmlSerializer.attribute("", "outputVariable", workFlowInvoke.outputVariable);
		xmlSerializer.endTag("", "invoke");
	}
}
