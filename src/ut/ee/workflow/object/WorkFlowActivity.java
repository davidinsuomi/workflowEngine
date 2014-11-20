package ut.ee.workflow.object;

import java.util.concurrent.atomic.AtomicBoolean;

public class WorkFlowActivity {
	public String name;
	public AtomicBoolean status = new AtomicBoolean(false);
	public WorkFlowActivity(String activityName){
		name = activityName;
	}
}
