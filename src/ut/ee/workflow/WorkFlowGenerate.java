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
import java.util.HashMap;
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
	private Map<String,WorkFlowVariable> offloadingVariables = new HashMap<String,WorkFlowVariable>();
	private Map<String,PartnerLink> offloadingPartnerLinks = new HashMap<String,PartnerLink>();
	
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
	    
	    //TODO create partnerLinks
	    xmlSerializer.startTag("", "partnerLinks");
	    for(Object value : offloadingPartnerLinks.values()){
	    	PartnerLink partnerLink = (PartnerLink) value;
	    	CreatePartnerLink(partnerLink);
	    }
	    xmlSerializer.endTag("", "partnerLinks");
	    //TODO create variables
	    xmlSerializer.startTag("", "variables");
	    for(Object value : offloadingVariables.values()){
	    	WorkFlowVariable workFlowVariable = (WorkFlowVariable) value;
	    	CreateVariable(workFlowVariable);
	    }
	    xmlSerializer.endTag("", "variables");
	    
	    //Open Tag <file>
	    xmlSerializer.startTag("", "process");
		
	}
	private void FinalizeXmlSerializer() throws IllegalArgumentException, IllegalStateException, IOException{
	    xmlSerializer.endDocument();
		Log.e(TAG, writer.toString());
		
	}
	
	private void FindNewOffloadingVariablesAndPartnerLink(String startTask, String endTask){
		if(startTask.equals(endTask)){
			FindCurrentTaskVariableAndPartnerLink(startTask);
		}else{
			while(!startTask.equals(graphMap.get(endTask).get(0))){
				FindCurrentTaskVariableAndPartnerLink(startTask);
				startTask = graphMap.get(startTask).get(0);
			}
		}
	}
	
	private void FindCurrentTaskVariableAndPartnerLink(String currentTag){
		WorkFlowActivity activity = activityMap.get(currentTag);
		if(activity instanceof WorkFlowInvoke){
			WorkFlowInvoke invoke = (WorkFlowInvoke) activity;
			for(WorkFlowVariable variable : variables){
				if(variable.name.equals(invoke.inputVariable)){
					if(!offloadingVariables.containsKey(variable.name)){
						offloadingVariables.put(variable.name, variable);
					}
					
				}else if(variable.name.equals(invoke.outputVariable)){
					if(!offloadingVariables.containsKey(variable.name)){
						offloadingVariables.put(variable.name, variable);
					}
				}					
			}
			
			for(PartnerLink partnerLink : partnerLinks){
				if(partnerLink.name.equals(invoke.partnerLink)){
					if(!offloadingPartnerLinks.containsKey(partnerLink.name)){
						offloadingPartnerLinks.put(partnerLink.name, partnerLink);
					}
				}
			}
		}else if(activity instanceof WorkFlowAssign){
			WorkFlowAssign assign = (WorkFlowAssign) activity;
			for(WorkFlowVariable variable : variables){
				if(variable.name.equals(assign.from)){
					if(!offloadingVariables.containsKey(variable.name)){
						offloadingVariables.put(variable.name, variable);
					}
					
				}else if(variable.name.equals(assign.to)){
					if(!offloadingVariables.containsKey(variable.name)){
						offloadingVariables.put(variable.name, variable);
					}
				}					
			}
		}
	}
	
	//So far only to able to offloading sequenceTask
	public void TaskToBeOffloading(String startTask, String endTask) throws IllegalArgumentException, IllegalStateException, IOException{
		FindNewOffloadingVariablesAndPartnerLink(startTask,endTask);
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
	private void CreateVariable(WorkFlowVariable workFlowVariable) throws IllegalArgumentException, IllegalStateException, IOException{
		xmlSerializer.startTag("", "variable");
		if(workFlowVariable.messageType != null){
			xmlSerializer.attribute("", "messageType", workFlowVariable.messageType);
		}
		xmlSerializer.attribute("", "name", workFlowVariable.name);
		xmlSerializer.endTag("", "variable");
	}
	private void CreatePartnerLink(PartnerLink partnerLink) throws IllegalArgumentException, IllegalStateException, IOException{
		xmlSerializer.startTag("", "partnerLink");
		if(partnerLink.myRole != null){
			xmlSerializer.attribute("", "myRole", partnerLink.myRole);
		}
		if(partnerLink.partnerLinkType != null){
			xmlSerializer.attribute("", "partnerLinkType", partnerLink.partnerLinkType);
		}
		if(partnerLink.name != null){
			xmlSerializer.attribute("", "name", partnerLink.name);
		}
		if(partnerLink.URL != null){
			xmlSerializer.text(partnerLink.URL);
		}
		xmlSerializer.endTag("", "partnerLink");
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
