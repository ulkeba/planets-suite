/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.ExperimentResources;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentResourcesImpl;
import eu.planets_project.tb.impl.model.finals.ExperimentTypesImpl;
import eu.planets_project.tb.impl.services.mockups.WorkflowImpl;

/**
 * @author alindley
 * hmBenchmarkGoalList: 
 * Please not the BenchmarkGoalList does hold the the BenchmarkGoal object itself
 *
 */
@Entity
public class ExperimentSetupImpl extends ExperimentPhaseImpl implements
		eu.planets_project.tb.api.model.ExperimentSetup,
		java.io.Serializable{
	
	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	@OneToOne(cascade={CascadeType.ALL})
	private BasicPropertiesImpl basicProperties;
	
	//BenchmarkGoals:
	private boolean bBenchmarkGoalListFinal;
	//the structure: HashMap<BenchmarkGoal.getXMLID,BenchmarkGoal>();
	private HashMap<String,BenchmarkGoal> hmBenchmarkGoals;
	
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentResourcesImpl experimentResources;
	@OneToOne(cascade={CascadeType.ALL})
	private WorkflowImpl workflow;
	private int iExperimentTypeID;
	private String sExperimentTypeName;
	

	public ExperimentSetupImpl(){
		basicProperties = new BasicPropertiesImpl();
		bBenchmarkGoalListFinal = false;
		hmBenchmarkGoals = new HashMap<String,BenchmarkGoal>();
		experimentResources = new ExperimentResourcesImpl();
		workflow = new WorkflowImpl();
		
		setStageMarker(STAGE_EXPERIMENTSETUP);
	}
	

	public BasicProperties getBasicProperties() {
		return this.basicProperties;
	}

	public ExperimentResources getExperimentResources() {
		return this.experimentResources;
	}

	public int getExperimentTypeID() {
		return this.iExperimentTypeID;
	}

	public String getExperimentTypeName() {
		return this.sExperimentTypeName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getExperimentWorkflow()
	 */
	public Workflow getExperimentWorkflow() {
		return this.workflow;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setExperimentResources(eu.planets_project.tb.api.model.ExperimentResources)
	 */
	public void setExperimentResources(ExperimentResources experimentResources) {
		this.experimentResources = (ExperimentResourcesImpl)experimentResources;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setBasicProperties(eu.planets_project.tb.api.model.BasicProperties)
	 */
	public void setBasicProperties(BasicProperties props) {
		this.basicProperties = (BasicPropertiesImpl)props;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setExperimentType(int)
	 */
	public void setExperimentType(int typeID) {
		ExperimentTypesImpl finalsExperimentTypes = new ExperimentTypesImpl();
		boolean bOK= finalsExperimentTypes.checkExperimentTypeIDisValid(typeID);
		if  (bOK){
			this.iExperimentTypeID = typeID;
			this.sExperimentTypeName = finalsExperimentTypes.getExperimentTypeName(typeID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setWorkflow(eu.planets_project.tb.api.services.mockups.ComplexWorkflow)
	 */
	public void setWorkflow(Workflow workflow) {
		this.workflow = (WorkflowImpl)workflow;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#addBenchmarkGoal(eu.planets_project.tb.api.model.benchmark.BenchmarkGoal)
	 */
	public void addBenchmarkGoal(BenchmarkGoal goal) {
		if(!isBenchmarkGoalListFinal()){
			if(this.hmBenchmarkGoals.containsKey(goal.getID()))
				this.hmBenchmarkGoals.remove(goal.getID());
			
			this.hmBenchmarkGoals.put(goal.getID(), goal);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#addBenchmarkGoals(java.util.List)
	 */
	public void addBenchmarkGoals(List<BenchmarkGoal> goal) {
		for(int i=0;i<goal.size();i++){
			addBenchmarkGoal(goal.get(i));
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getAllAddedBenchmarkGoals()
	 */
	public List<BenchmarkGoal> getAllAddedBenchmarkGoals() {
		List<BenchmarkGoal> ret = new Vector<BenchmarkGoal>();
		Iterator<BenchmarkGoal> itGoals = this.hmBenchmarkGoals.values().iterator();
		while(itGoals.hasNext()){
			ret.add(itGoals.next());
		}
		return ret;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getBenchmarkGoal(java.lang.String)
	 */
	public BenchmarkGoal getBenchmarkGoal(String goalXMLID) {
		return this.hmBenchmarkGoals.get(goalXMLID);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#isBenchmarkGoalListFinal()
	 */
	public boolean isBenchmarkGoalListFinal() {
		return this.bBenchmarkGoalListFinal;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#removeBenchmarkGoal(java.lang.String)
	 */
	public void removeBenchmarkGoal(String benchmarkGoalXMLID) {
		if(!isBenchmarkGoalListFinal()){
			if(this.hmBenchmarkGoals.containsKey(benchmarkGoalXMLID))
				this.hmBenchmarkGoals.remove(benchmarkGoalXMLID);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#removeBenchmarkGoal(java.lang.String, java.lang.String)
	 */
	public void removeBenchmarkGoal(String category, String name) {
		Iterator<BenchmarkGoal> itGoals = this.hmBenchmarkGoals.values().iterator();
		while(itGoals.hasNext()){
			BenchmarkGoal goal = itGoals.next();
			if (goal.getCategory().equals(category)&&goal.getName().equals(name)){
				removeBenchmarkGoal(goal.getID());
			}
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#removeBenchmarkGoals(java.util.List)
	 */
	public void removeBenchmarkGoals(List<String> goalXMLIDs) {
		for(int i=0;i<goalXMLIDs.size();i++){
			removeBenchmarkGoal(goalXMLIDs.get(i));
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setBenchmarkGoalListFinal()
	 */
	public void setBenchmarkGoalListFinal() {
		this.bBenchmarkGoalListFinal = true;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setBenchmarkGoals(java.util.List)
	 */
	public void setBenchmarkGoals(List<BenchmarkGoal> goals) {
		//delete old object
		this.hmBenchmarkGoals = new HashMap<String,BenchmarkGoal>();
		this.addBenchmarkGoals(goals);
	}
	
}
