package ut.ee.workflow;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import ut.ee.workflow.object.WorkFlowProcess;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	WorkFlowXmlParser workFlowXmlParser = new WorkFlowXmlParser();
	private WorkFlowProcess workFlowProcess;
	TextView partnerLinksTextView, variablesTextView, sequenceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getResources().getAssets();
        InputStream inputStream = null;
        partnerLinksTextView = (TextView)findViewById(R.id.partnerLinks);
        variablesTextView =(TextView) findViewById(R.id.variables);
        sequenceTextView =(TextView) findViewById(R.id.sequence);
        try{
        	inputStream = assetManager.open("bpel04.xml" );
        	if(inputStream !=null ){
        		workFlowProcess = workFlowXmlParser.parse(inputStream);
        	}
        }catch(IOException e){
        	e.printStackTrace();
        } catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // the code below just output the parsing result on the UI
//        StringBuilder partnerLinkOutput = new StringBuilder();
//        partnerLinkOutput.append("partnerLinks has " +  workFlowProcess.partnerLinks.size() + " partnerLinks" + "\n");
//        
//        for(int i =0; i < workFlowProcess.partnerLinks.size(); i++){
//        	partnerLinkOutput.append( i +":  " + workFlowProcess.partnerLinks.get(i).toString() );
//        }
//        partnerLinksTextView.setText(partnerLinkOutput.toString());
//        
//        StringBuilder variablesOutput = new StringBuilder();
//        variablesOutput.append("variables has " + workFlowProcess.variables.size() + " variables" + "\n");
//        for(int i=0; i<workFlowProcess.variables.size(); i++){
//        	variablesOutput.append(i + ":  " + workFlowProcess.variables.get(i).toString());
//        }
//        variablesTextView.setText(variablesOutput.toString());
//        
//        StringBuilder sequenceOutput = new StringBuilder();
//        sequenceOutput.append("Sequence has " + workFlowProcess.sequenceDictionary.size() + " activities" + "\n");
//        for(Map.Entry<Integer,String> entry: workFlowProcess.sequenceDictionary.entrySet()){
//        	sequenceOutput.append(entry.getKey() + ":" + entry.getValue() + "\n");
//        }
//        
//        sequenceTextView.setText(sequenceOutput.toString());
    }
}


