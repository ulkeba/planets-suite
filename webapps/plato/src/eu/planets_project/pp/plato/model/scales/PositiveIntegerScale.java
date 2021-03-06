/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.model.scales;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eu.planets_project.pp.plato.model.values.PositiveIntegerValue;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * An integer value with lower and upper bounds to be used in the leaves of the objective tree
 * @author Christoph Becker
 *
 */
@Entity
@DiscriminatorValue("P")
// We don't use the annotation NotNullField anymore, as the error message doesn't allow
// to specify the name of the leaf. So the error message is not very accurate. As we already
// have all methods available in the base class Scale we use them to check for restriction/unit.
//@NotNullField(fieldname="unit", message="Please enter a unit for the scale of type 'Positive Integer'")
public class PositiveIntegerScale extends RestrictedScale {

    private static final long serialVersionUID = 7455117412684178182L;

    public PositiveIntegerValue createValue() {
        PositiveIntegerValue v = new PositiveIntegerValue();
        v.setScale(this);
        return v;
    }

    public  String getDisplayName() {
        return "Positive integer";
    }
    private int upperBound = Integer.MAX_VALUE;

    @Override
    public boolean isInteger() {
        return true;
    }
    
    @Override
    public String getRestriction() {
        if (upperBound == Integer.MAX_VALUE)
            return "";
        else
            return Integer.toString(upperBound);
    }

    @Override
    public String getReadableRestriction() {
        if (this.upperBound == Integer.MAX_VALUE) {
            return "";
        } else {
            return "up to " + this.upperBound;
        }
    }

    @Override
    public void setRestriction(String restriction) {
        if (restriction != null && !"".equals(restriction)) {
            PlatoLogger.getLogger(this.getClass()).debug("setting restriction: "+restriction);
            try {
                setUpperBound(Integer.parseInt(restriction));

            } catch (NumberFormatException e) {
                PlatoLogger.getLogger(this.getClass()).warn("ignoring invalid restriction " +
                        "setting in PositiveFloatValue: "+restriction);
            }
        } else {
            setUpperBound(Integer.MAX_VALUE);
        }
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upper) {
        this.upperBound = upper;
    }

    @Override
    protected boolean restrictionIsValid(String leafName, List<String> errorMessages) {
        if (this.upperBound <= 0) {
            errorMessages.add("The upper bound specified for leaf \"" + leafName + "\" is not greater than zero!");
            return false;
        }
        return true;
    }

    @Override
    public boolean isCorrectlySpecified(String leafName,
            List<String> errorMessages) {

        if (false == super.isCorrectlySpecified(leafName, errorMessages)) {
            return false;
        }

        // we additionally check for the unit
        if (getUnit() == null || "".equals(getUnit())) {
            errorMessages.add("Please enter a unit for the scale of type 'Positive Integer' at leaf '" + leafName + "'");
            return false;
        }

        return true;
    }


    @Override
    public boolean isEvaluated(Value value) {
        boolean evaluated = false;
        if ((value != null) && (value instanceof PositiveIntegerValue)) {
            PositiveIntegerValue v = (PositiveIntegerValue)value;

            evaluated = v.isChanged() &&
            (v.getValue() <= getUpperBound() && v.getValue() >= 0 );
        }
        return evaluated;
    }
}
