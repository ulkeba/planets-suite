/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.ExperimentApproval;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.model.ExperimentSetup;

/**
 * @author alindley
 *
 */
//@Entity
public class Experiment extends eu.planets_project.tb.impl.model.ExperimentPhase
						implements eu.planets_project.tb.api.model.Experiment,
									java.io.Serializable{
	//@Id
	//@GeneratedValue
	private Long lExperimentID;
	private ExperimentEvaluation expEvaluation;
	private ExperimentApproval expApproval;
	private ExperimentExecution expExecution;
	private ExperimentSetup expSetup;
	
	public Experiment(){
		
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentAnalysis()
	 */
	public ExperimentEvaluation getExperimentAnalysis() {
		return this.expEvaluation;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentApproval()
	 */
	public ExperimentApproval getExperimentApproval() {
		return this.expApproval;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentExecution()
	 */
	public ExperimentExecution getExperimentExecution() {
		return this.expExecution;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentID()
	 */
	public long getExperimentID() {
		return this.lExperimentID;
	}
	
	private void setExperimentID(long lID){
		this.lExperimentID = lID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentSetup()
	 */
	public ExperimentSetup getExperimentSetup() {
		return this.expSetup;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentAnalysis(eu.planets_project.tb.api.model.ExperimentEvaluation)
	 */
	public void setExperimentAnalysis(ExperimentEvaluation analysisPhase) {
		this.expEvaluation = analysisPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentApproval(eu.planets_project.tb.api.model.ExperimentApproval)
	 */
	public void setExperimentApproval(ExperimentApproval approvalPhase) {
		this.expApproval = approvalPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentExecution(eu.planets_project.tb.api.model.ExperimentExecution)
	 */
	public void setExperimentExecution(ExperimentExecution executionPhase) {
		this.expExecution = executionPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentSetup(eu.planets_project.tb.api.model.ExperimentSetup)
	 */
	public void setExperimentSetup(ExperimentSetup setupPhaseObject) {
		this.expSetup = setupPhaseObject;
	}

}
