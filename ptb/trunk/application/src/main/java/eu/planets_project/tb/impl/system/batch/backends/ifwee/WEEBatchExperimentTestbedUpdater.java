package eu.planets_project.tb.impl.system.batch.backends.ifwee;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.view.CreateViewResult;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.BatchWorkflowResultLogImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.serialization.JaxbUtil;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;


/**
 * workflow processor specific actions for storing/extracting results when notified by the MDB.
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 22.10.2009
 *
 */
public class WEEBatchExperimentTestbedUpdater {
	
	private static final Log log = LogFactory.getLog(WEEBatchExperimentTestbedUpdater.class);
	
	private TestbedWEEBatchProcessor tbWEEBatch;
	private ExperimentPersistencyRemote edao;
	private TestbedManager testbedMan;
	private DataHandler dh;
	private DataRegistry dataRegistry;
	
	public WEEBatchExperimentTestbedUpdater(){
		tbWEEBatch = TestbedWEEBatchProcessor.getInstance();
		edao = ExperimentPersistencyImpl.getInstance();
		testbedMan = TestbedManagerImpl.getInstance();
		dh = new DataHandlerImpl();
		dataRegistry = DataRegistryFactory.getDataRegistry();

	}
	 
	/*public void processNotify_WorkflowQueued(){
		
	}
	
	public void processNotify_WorkflowRunning(){
		
	}*/
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution. In here all the TB specific mapping is done.
	 * 	1. extract digital objects created
	 *	2. store the WorkflowResult 
	 *	3. set all the stage information
	 * @param expID
	 * @param weeWFResult
	 */
	public void processNotify_WorkflowCompleted(long expID, WorkflowResult weeWFResult){
		log.info("processing WEEBatchExperiment: processNotify_WorkflowCompleted");
		Experiment exp = testbedMan.getExperiment(expID);
		if(weeWFResult==null){
			log.info("processing WEEBatchExperiment: wfResult = null -> processing notify_WorkflowFailed");
			this.processNotify_WorkflowFailed(expID, "WorkflowResult not available");
			return;
		}
		//create a BatchExecutionRecord
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl( (ExperimentExecutableImpl) exp.getExperimentExecutable() );
		//startTime
		Calendar c1 = new GregorianCalendar();
		c1.setTimeInMillis(weeWFResult.getStartTime());
		batchRecord.setStartDate(c1);
		
		//endTime
		Calendar c2 = new GregorianCalendar();
		c2.setTimeInMillis(weeWFResult.getEndTime());
		batchRecord.setStartDate(c2);
	
		BatchWorkflowResultLogImpl wfResultLog = new BatchWorkflowResultLogImpl();
		try {
			//try serializing the workflow result log- as this is the way it needs to be stored
			String wfResultxml = JaxbUtil.marshallObjectwithJAXB(WorkflowResult.class, weeWFResult);
			log.debug("Successfully serialized the workflowResult Log via Jaxb" );
			//store the wfResultLog in the db model bean
			wfResultLog.setSerializedWorkflowResult(wfResultxml);
		} catch (Exception e) {
			log.debug("Problems serializing wfResultLog object",e);
			this.processNotify_WorkflowFailed(expID, "WorkflowResult not serializable");
			return;
		}
		
		batchRecord.setWorkflowExecutionLog(wfResultLog);
		batchRecord.setBatchRunSucceeded(true);
		
		//now iterate over the results and extract and store all crated digos
		List<ExecutionRecordImpl> execRecords = new ArrayList<ExecutionRecordImpl>();
		
		//group related wfResult items per input digital objects 
		Map<URI,List<WorkflowResultItem>> structuredResults = this.getAllWFResultItemsPerInputDigo(weeWFResult);
		//FIXME AL: We still need to crate empty executionRecords for the items that weren't processed by the wee (e.g. expSetup.getInputData and compare to the log)
		for(URI inputDigoURI : structuredResults.keySet()){
			int actionCounter = 0;
			ExecutionRecordImpl execRecord = new ExecutionRecordImpl(batchRecord);
			//the input Digo for all this information is about
			// FIXME This appears to be the resolved URI, not the proper Planets DR URI:
			execRecord.setDigitalObjectReferenceCopy(inputDigoURI+"");
			Properties p = new Properties();
			//iterate over the results and document the migration action - all other information goes into properties.
			for(WorkflowResultItem wfResultItem : structuredResults.get(inputDigoURI)){
				
				//1. check if this record was about the migration action
				String action = wfResultItem.getSActionIdentifier();
				if(action.startsWith(WorkflowResultItem.SERVICE_ACTION_MIGRATION)){
					URI outputDigoRef = wfResultItem.getOutputDigitalObjectRef();
					if(outputDigoRef!=null){
						//DigitalObject outputDigo = dataRegistry.retrieve(outputDigoRef);
						//1.a download the ResultDigo into the TB and store it's reference - if it's the final migration producing the output object
						if(action.equals(WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION)){
							//documenting the final output object
							URI tbUri = execRecord.setDigitalObjectResult(outputDigoRef, exp);
							//FIXME: currently not possible to mix DIGO and PROPERTY result: 
							p.put(ExecutionRecordImpl.RESULT_PROPERTY_URI, tbUri.toString());
						}
						else{
						//1.b documenting the interim results in a multi-migration-workflow
							//DataHandler dh = new DataHandlerImpl();
					        //URI tbUri = dh.storeDigitalObject(outputDigo, exp);
					        p.put(ExecutionRecordImpl.RESULT_PROPERTY_INTERIM_RESULT_URI+"["+actionCounter+"]", outputDigoRef.toString());
						}
						Calendar start = new GregorianCalendar();
						start.setTimeInMillis(wfResultItem.getStartTime());
                        execRecord.setStartDate(start);
						Calendar end = new GregorianCalendar();
						end.setTimeInMillis(wfResultItem.getEndTime());
                        execRecord.setEndDate(end);
					}
				}
			
				//1b. every service action gets persisted as a stage record
				ExecutionStageRecordImpl stageRecord = fillInExecutionStageRecord(wfResultItem,execRecord,action,exp.getEntityID());
	            execRecord.getStages().add(stageRecord);
				
				//2. or about some general reporting information
				if(action.startsWith(WorkflowResultItem.GENERAL_WORKFLOW_ACTION)){
					execRecord.setReportLog(this.parseReportLog(wfResultItem));
				}

				//3. document all other metadata for actions: identification, etc. as properties over all actions
				try{
					this.updateProperties(actionCounter, p, wfResultItem);
				}catch(Exception e){
					log.error("processing WEEBatchExperiment: Problems crating execution record properties for a workflowResultItem "+e);
				}
				actionCounter++;
			}
			try {
				execRecord.setPropertiesListResult(p);
			} catch (IOException e) {
				log.debug("processing WEEBatchExperiment: Problem adding properties to executionRecord: "+e);
			}
			
			//got all information - now add the record for this inputDigo
			log.info("processing WEEBatchExperiment: Adding an execution record: "+inputDigoURI);
			execRecords.add(execRecord);
		}
		batchRecord.setRuns(execRecords);

		this.helperUpdateExpWithBatchRecord(exp, batchRecord);
	}
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution
	 * @param expID
	 * @param failureReason
	 */
	public void processNotify_WorkflowFailed(long expID,String failureReason){
		Experiment exp = testbedMan.getExperiment(expID);
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl((ExperimentExecutableImpl)exp.getExperimentExecutable());
		batchRecord.setBatchRunSucceeded(false);
		
		this.helperUpdateExpWithBatchRecord(exp, batchRecord);
		//TODO AL: any more fields to set?
	}
	
	/**
	 * All actions of setting an experiment into state 'processing has started'
	 * @param expID
	 */
	public void processNotify_WorkflowStarted(long expID){
		Experiment exp = testbedMan.getExperiment(expID);
    	exp.getExperimentExecutable().setExecutableInvoked(true);
    	if ( exp.getExperimentExecutable().getBatchExecutionRecords() != null ) {
            log.info("processNotify_WorkflowStarted: Updating the experiment 'started': #"+exp.getExperimentExecutable().getBatchExecutionRecords().size());
    	} else {
            log.info("processNotify_WorkflowStarted: Updating the experiment 'started': "+exp.getExperimentExecutable().getBatchExecutionRecords());
    	}
    	//testbedMan.updateExperiment(exp);
    	edao.updateExperiment(exp);
	}
	
	private void helperUpdateExpWithBatchRecord(Experiment exp,BatchExecutionRecordImpl record){
        if ( exp.getExperimentExecutable().getBatchExecutionRecords() != null ) {
            log.info("helperUpdateExpWithBatchRecord: Adding new BatchExecutionRecord to this Experiment: #"+exp.getExperimentExecutable().getBatchExecutionRecords().size());
            if ( exp.getExperimentExecutable().getBatchExecutionRecords().size() > 0 )
                log.info("helperUpdateExpWithBatchRecord: Adding new BatchExecutionRecord to this Experiment: #"+exp.getExperimentExecutable().getBatchExecutionRecords().iterator().next().getRuns().size());
        } else {
            log.info("helperUpdateExpWithBatchRecord: Adding new BatchExecutionRecord to this Experiment: "+exp.getExperimentExecutable().getBatchExecutionRecords());
        }
    	exp.getExperimentExecutable().getBatchExecutionRecords().add(record);
		exp.getExperimentExecutable().setExecutionCompleted(true);
        exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
        exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);   
		//testbedMan.updateExperiment(exp);
		edao.updateExperiment(exp);
	}
	
	
	/*private ExecutionRecordImpl createExecutionRecordToExperiment(long eid, WorkflowResult wfr, String filename) {
        DataHandler dh = new DataHandlerImpl();
        try {
            ExecutionRecordImpl rec = new ExecutionRecordImpl();
            rec.setDigitalObjectReferenceCopy(filename);
            try {
                rec.setDigitalObjectSource(dh.get(filename).getName());
            } catch (FileNotFoundException e) {
                rec.setDigitalObjectSource(filename);
            }
            // FIXME Set this in the job somewhere:
            rec.setDate(Calendar.getInstance());
            List<ExecutionStageRecordImpl> stages = rec.getStages();
            
            if( wfr != null && wfr.getStages() != null ) {
                // Examine the result:
                if( WorkflowResult.RESULT_DIGITAL_OBJECT.equals(wfr.getResultType())) {
                    rec.setDigitalObjectResult( (DigitalObject) wfr.getResult(), exp );
                    
                } else if(WorkflowResult.RESULT_CREATEVIEW_RESULT.equals(wfr.getResultType()) ) {
                    CreateViewResult cvr = (CreateViewResult) wfr.getResult( );
                    Properties vp = new Properties();
                    vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_SESSION_ID, cvr.getSessionIdentifier());
                    vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_VIEW_URL, cvr.getViewURL().toString());
                    vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_ENDPOINT_URL, wfr.getMainEndpoint().toString() );
                    rec.setPropertiesListResult(vp);
                    
                } else {
                    rec.setResultType(ExecutionRecordImpl.RESULT_MEASUREMENTS_ONLY);
                }
                
                // Now pull out the stages, which include the measurements etc:
                for( ExecutionStageRecordImpl stage : wfr.getStages() ) {
                    // FIXME Can this be done from the session's Service Registry instead, please!?
                    if( stage.getEndpoint() != null ) {
                        log.info("Recording info about endpoint: "+stage.getEndpoint());
                        stage.setServiceRecord( ServiceBrowser.createServiceRecordFromEndpoint( eid, stage.getEndpoint(), Calendar.getInstance() ) );
                    }
                    // Re-reference this stage object from the Experiment:
                    stages.add(stage);
                }
            }

            batch.getRuns().add(rec);
            log.info("Added records ("+batch.getRuns().size()+") for "+rec.getDigitalObjectSource());
        } catch( Exception e ) {
            log.error("Exception while parsing Execution Record.");
            e.printStackTrace();
        }
        
    }*/

	

	/**
	 * Takes a Wee WorkflowResult object and creates a map with DigoPermanentURI of the inputDigital object
	 * and all of it's WorkflowResultItems that were created. Please note: it does not take the InputDigos the TB submitted the job with,
	 * but all object's that were recorded in the WorkflowResultItem.setInputDigitalObject and groups them by 
	 * the getAboutExecutionDigoRef()
	 * @param wfResult
	 * @param digoPermURI
	 * @return
	 */
	private Map<URI,List<WorkflowResultItem>> getAllWFResultItemsPerInputDigo(WorkflowResult wfResult){
		//the structure: Map<InputDigoPermanentURI, List<WorkflowResultItem>>
		Map<URI,List<WorkflowResultItem>> ret = new HashMap<URI,List<WorkflowResultItem>>();
		for(WorkflowResultItem wfResultItem : wfResult.getWorkflowResultItems()){
			
			URI digoCalledInExecute = wfResultItem.getAboutExecutionDigoRef();
			//the permanentURI is the reference for the TB stored Digos when submitting
			if(digoCalledInExecute!=null){
				//check if we already extracted any information for this digo
				if(!ret.keySet().contains(digoCalledInExecute)){
					//create new record for this digo
					List<WorkflowResultItem> resItems = new ArrayList<WorkflowResultItem>();
					ret.put(digoCalledInExecute, resItems);
				}

				//now update the return object
				ret.get(digoCalledInExecute).add(wfResultItem);
			}
		}
		return ret;
	}
	
	
	/**
	 * Properties contain screen readable information for stage5 for a given ExecutionRecord
	 * @param execRecord
	 * @param wfResultItem
	 * @return
	 * @throws IOException
	 */
	private Properties updateProperties(int count, Properties p, WorkflowResultItem wfResultItem) throws IOException{

		//create a property name that has the action identifier as part of it.
		if((wfResultItem.getServiceEndpoint()!=null)&&(!wfResultItem.getServiceEndpoint().equals(""))){
			p.setProperty(ExecutionRecordImpl.WFResult_ServiceEndpoint+"["+count+"]", wfResultItem.getServiceEndpoint());
		}
		if((wfResultItem.getLogInfo()!=null)&&(wfResultItem.getLogInfo().size()>0)){
			p.setProperty(ExecutionRecordImpl.WFResult_LOG+"["+count+"]", wfResultItem.getLogInfo().toString());
		}
		if((wfResultItem.getSActionIdentifier()!=null)&&(!wfResultItem.getSActionIdentifier().equals(""))){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionIdentifier+"["+count+"]", wfResultItem.getSActionIdentifier());
		}
		if((wfResultItem.getServiceParameters()!=null)&&(wfResultItem.getServiceParameters().size()>0)){
			String sFormatted="";
			for(Parameter sp: wfResultItem.getServiceParameters()){
				sFormatted+="["+sp.getName()+" = "+sp.getValue()+"] ";
			}
			p.setProperty(ExecutionRecordImpl.WFResult_Parameters+"["+count+"]", sFormatted);
		}
		if((wfResultItem.getExtractedInformation()!=null)&&(wfResultItem.getExtractedInformation().size()>0)){
			p.setProperty(ExecutionRecordImpl.WFResult_ExtractedInformation+"["+count+"]", wfResultItem.getExtractedInformation().toString());
		}
		if(wfResultItem.getStartTime()!=-1){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionStartTime+"["+count+"]", wfResultItem.getStartTime()+"");
		}
		if(wfResultItem.getEndTime()!=-1){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionEndTime+"["+count+"]", wfResultItem.getEndTime()+"");
		}
		if(wfResultItem.getServiceReport()!=null){
			p.setProperty(ExecutionRecordImpl.WFResult_ServiceReport+"["+count+"]", wfResultItem.getServiceReport().toString());
		}
		if(wfResultItem.getServiceDescription()!=null){
			p.setProperty(ExecutionRecordImpl.WFResult_ServiceDescription+"["+count+"]", wfResultItem.getServiceDescription().toString());
		}
		return p;
	}
	
	/**
	 * Looks at the given set of extracted properties (for a given input digo)
	 * and aabuilds up the ResultLog if available. (i.e. log on how the workflow was processed on this item, 
	 * e.g. A->B, B->C, C did not terminate properly
	 * @param p
	 * @return
	 */
	private List<String> parseReportLog(WorkflowResultItem wfResultItem){
		List<String> ret = new ArrayList<String>();
		if((wfResultItem.getSActionIdentifier()!=null)&&
		   (wfResultItem.getSActionIdentifier().startsWith(WorkflowResultItem.GENERAL_WORKFLOW_ACTION))){
			ret = wfResultItem.getLogInfo();
		}
		return ret;
	}
	
	/**
	 * Takes a workflow result item and filles in the TB's ExecutionStageRecord from it.
	 * @param wfResultItem: the workflow result item we're building the execution stage record for
	 * @param execRecord the parent record that takes the overall workflow's result
	 * @param stageName a stage name to store this information for
	 * @return
	 */
	private ExecutionStageRecordImpl fillInExecutionStageRecord(WorkflowResultItem wfResultItem, ExecutionRecordImpl execRecord, String stageName, long eid){
		 ExecutionStageRecordImpl stage = new ExecutionStageRecordImpl(execRecord,stageName);
         //for now just filling in the endpoint and serviceRecord information
         try {
        	 //1. set the stage's endpoint
			 stage.setEndpoint(new URL(wfResultItem.getServiceEndpoint()));
			 
			 //2. create the service record information
            if( stage.getEndpoint() != null ) {
                log.info("Recording info about endpoint: "+stage.getEndpoint());
                Calendar exectime = new GregorianCalendar();
                exectime.setTimeInMillis(wfResultItem.getStartTime());
                stage.setServiceRecord( ServiceBrowser.createServiceRecordFromEndpoint( eid, stage.getEndpoint(), exectime ) );
            }
		} catch (MalformedURLException e) {
			log.debug("can't set stage's endpoint."+e);
		}
		return stage;
		
	}

}

