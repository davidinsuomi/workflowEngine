package ut.ee.workflow.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkFlowProcess {
	public ArrayList<PartnerLink> partnerLinks = new ArrayList<PartnerLink>();
	public ArrayList<WorkFlowVariable> variables = new ArrayList<WorkFlowVariable>();
	public Map<String,ArrayList<String>> graphMap = new HashMap<String,ArrayList<String>>();
	public Map<String,WorkFlowActivity> activityMap = new HashMap<String,WorkFlowActivity>();
	public String FirstActivity;
	public String lastActivity;
}