package eu.planets_project.tb.api.model;

import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.impl.AdminManagerImpl;

/**
 *
 * -Phases: Every Experiment consists out of four phases Setup, Approval, Execution and Analysis
 * Every Phase may consist itself of multiple steps (e.g. setup experiment includes: fill in basic properties, design a workflow, etc.). 
 * Within this steps it's possible to navigate forward and backward - afer a phase has completed its data may not be modified any longer. 
 * <p>
 * - Status: Every phase contains a status as well as there's an overall status status for the entire Experiment
 * - ExperimentExecutable: the part of an experiment (related to the idea of an "executable preservation plan)
 *   All phases may read or write into this object, the actual execution takes place on this object.
 *   While the Phases correspond to the actual experiment related ideas the executable capturs the technical bits.
 * @author alindley
 *
 */
public interface Experiment extends ExperimentPhase{

	public void setExperimentSetup(ExperimentSetup setupPhaseObject);
	public ExperimentSetup getExperimentSetup();
	
	public void setExperimentApproval(ExperimentApproval approvalPhase);
	public ExperimentApproval getExperimentApproval();
	
	public void setExperimentExecution(ExperimentExecution executionPhase);
	public ExperimentExecution getExperimentExecution();
	
	public void setExperimentEvaluation(ExperimentEvaluation analysisPhase);
	public ExperimentEvaluation getExperimentEvaluation();
	
	public void setExperimentExecutable(ExperimentExecutable executable);
	public ExperimentExecutable getExperimentExecutable();
	public void removeExperimentExecutable();
	
	public ExperimentPhase getCurrentPhase();
    public int getCurrentPhasePointer();
    
    /**
     * This returns the six-stage version of the phase index.
     *  - 0 means not-yet-started at all (may never happen!).
     *  - 1, 2, 3 mean Phase 1 (Setup), sub-stages 1, 2 and 3.
     *  - 4, 5, 6 mean Experiment phases 2, 3 and 4, as defined herein.
     *  - 7 means finalised.
     * @return An integer from 0 to 7, depending on the phase.
     */
    public int getCurrentPhaseIndex();
	
    public boolean isAwaitingApproval();
    public boolean isApproved();
    public boolean isDenied();
    public String getUsernameOfApprover();
}
