/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umuc.ga.gaPlugin;
import jenes.population.Population;
import jenes.stage.AbstractStage;
import jenes.stage.StageException;
/**
 *
 * @author ironau
 */
public class GATestStage extends AbstractStage{

    @Override
    public void process(Population in, Population out) throws StageException {
        System.out.println("The GATestStage was executed");
    }
    
}
