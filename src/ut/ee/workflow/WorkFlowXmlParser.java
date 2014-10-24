package ut.ee.workflow;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import ut.ee.workflow.object.PartnerLink;
import ut.ee.workflow.object.WorkFlowActivity;
import ut.ee.workflow.object.WorkFlowAssign;
import ut.ee.workflow.object.WorkFlowInvoke;
import ut.ee.workflow.object.WorkFlowProcess;
import ut.ee.workflow.object.WorkFlowVariable;

public class WorkFlowXmlParser {
	private WorkFlowProcess workFlowProcess = new WorkFlowProcess();
	private Map<String,ArrayList<String>> graphMap = new HashMap<String,ArrayList<String>>();
	private Map<String,WorkFlowActivity> activityMap = new HashMap<String,WorkFlowActivity>();
	private String previousNodeName;
	private String NodeName;
	private static final String ns = null;
	
	
	String currentTag= null;

	
	public WorkFlowProcess parse(InputStream in) throws XmlPullParserException, IOException {
		try{
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		parser.nextTag();
		// workFlowProcess.partnerLinks
		readAndParse(parser);
		return workFlowProcess;
		}finally{
			in.close();
		}
	}
	
	private void readAndParse(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "process");
		
		while(parser.next() != XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			
			if(name.equals("partnerLinks")){
				workFlowProcess.partnerLinks = readPartnerLink(parser);
			}else if(name.equals("variables")){
				workFlowProcess.variables = (readWorkFlowVariable(parser));
			}else if(name.equals("sequence")){
				readWorkFlowSequence(parser,"Beginnering", XmlPullParser.END_DOCUMENT);
				break;
			}
			Log.d("TAG", " partnerLink" + workFlowProcess.partnerLinks.size());
			Log.d("TAG", " VARIABLES" + workFlowProcess.variables.size());

		}
		
		workFlowProcess.activityMap = activityMap;
		workFlowProcess.graphMap = graphMap;
		System.out.println();

		
	}
	
	private ArrayList<PartnerLink> readPartnerLink(XmlPullParser parser) throws XmlPullParserException , IOException{
		ArrayList<PartnerLink> partnerLinks = new ArrayList<PartnerLink>();
		parser.require(XmlPullParser.START_TAG, ns, "partnerLinks");
		String partnerLinkName = null;
		String partnerLinkType = null;
		String myRole = null;
		
		while(parser.next() != XmlPullParser.END_TAG ){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if(name.equals("partnerLink")){
				//parser.require(XmlPullParser.START_TAG, ns, "partnerLink");
				partnerLinkName = parser.getAttributeValue(ns, "name");
				partnerLinkType = parser.getAttributeValue(ns, "partnerLinkType");
				myRole = parser.getAttributeValue(ns, "myRole");
				partnerLinks.add(new PartnerLink(partnerLinkName, partnerLinkType, myRole));
				
				if (parser.next() == XmlPullParser.TEXT) {
			        parser.nextTag();
			    }
				parser.next();
				//parser.require(XmlPullParser.END_TAG, ns, "partnerLink");
			}
		}
		return partnerLinks;
	}
	
	private ArrayList<WorkFlowVariable> readWorkFlowVariable(XmlPullParser parser) throws XmlPullParserException, IOException{
		ArrayList<WorkFlowVariable> variables = new ArrayList<WorkFlowVariable>();
		parser.require(XmlPullParser.START_TAG, ns, "variables");
		String variableName = null;
		String variableMessageType = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals("variable")) {
				parser.require(XmlPullParser.START_TAG, ns, "variable");
				variableName = parser.getAttributeValue(ns, "name");
				variableMessageType = parser.getAttributeValue(ns,"messageType");
				variables.add(new WorkFlowVariable(variableName, variableMessageType));
				parser.nextTag();
				parser.require(XmlPullParser.END_TAG, ns, "variable");
			}
		}
		return variables;
	}

	private void readWorkFlowSequence(XmlPullParser parser , String previousTag, int TAGTYPE) throws XmlPullParserException, IOException{

		while(parser.next() != TAGTYPE){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String tagName = parser.getName();
//			if(tagName.equals("assign")){
//				currentTag = readAssign(parser);
//			}else if(tagName.equals("flow")){
//				readFlow(parser);
//			}else if(tagName.equals("invoke")){
//				currentTag = readInvoke(parser);
//			}
			switch(tagName){
			case "assign":
				currentTag = readAssign(parser);
				break;
			case "flow":
				readFlow(parser,previousTag);
				break;
			case "invoke":
				currentTag = readInvoke(parser);
				break;
			default:
				break;
			}
			
			// check the first run
			if(previousTag.equals("Beginnering")){
				graphMap.put("Beginnering", new ArrayList<String>(Arrays.asList(currentTag)));
				previousTag = currentTag;
				continue;
			}		
			
			if(graphMap.containsKey(previousTag)){
				ArrayList<String> old = graphMap.get(previousTag);
				old.add(currentTag);
				ArrayList<String> newList = new ArrayList<String>(old);
				graphMap.put(previousTag, newList);
			}else{
				graphMap.put(previousTag, new ArrayList<String>(Arrays.asList(currentTag)));
			}
			
			previousTag = currentTag;
		}
	}
	
	private void readFlow(XmlPullParser parser,String previousTag) throws XmlPullParserException, IOException{
		final String  flowPreviousTag = previousTag;
		parser.require(XmlPullParser.START_TAG, ns, "flow");
		
		while(parser.next() != XmlPullParser.END_TAG){
			String tag = parser.getName();
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			if(tag.equals("sequence")){
				readWorkFlowSequence(parser, flowPreviousTag ,XmlPullParser.END_TAG);
			}
		}
		
		parser.nextTag();
	}
	private String readInvoke(XmlPullParser parser)throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "invoke");
		String name = null;
		String partnerLink = null;
		String operation = null;
		String inputVariable =null;
		String outputVariable = null;
		
		name = parser.getAttributeValue(null,"name");
		partnerLink = parser.getAttributeValue(null, "partnerLink");
		operation = parser.getAttributeValue(null,"operation");
		inputVariable = parser.getAttributeValue(null, "inputVariable");
		outputVariable = parser.getAttributeValue(null, "outputVariable");
		
		
		parser.nextTag();
		WorkFlowInvoke invoke = new WorkFlowInvoke(name, partnerLink, operation, inputVariable, outputVariable);
		
		activityMap.put(name, invoke);
		
		return name;
		
	}
	
	private String readAssign(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "assign");
		String name = null;
		String from = null; 
		String to = null;
		
		name = parser.getAttributeValue(null, "name");
		while(parser.next()!= XmlPullParser.END_TAG){
			String tag = parser.getName();
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
			if(tag.equals("from")){
				from= parser.getAttributeValue(null, "variable");
				parser.nextTag();
			}else if ( tag.equals("to")){
				to = parser.getAttributeValue(null, "variable");
				parser.nextTag();
			}
		}
		parser.nextTag();
		WorkFlowAssign assign = new WorkFlowAssign(name, from, to);
		activityMap.put(name, assign);
		return name;
		
	}
//	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
//	    if (parser.getEventType() != XmlPullParser.START_TAG) {
//	        throw new IllegalStateException();
//	    }
//	    int depth = 1;
//	    while (depth != 0) {
//	        switch (parser.next()) {
//	        case XmlPullParser.END_TAG:
//	            depth--;
//	            break;
//	        case XmlPullParser.START_TAG:
//	            depth++;
//	            break;
//	        }
//	    }
//	 }

}

