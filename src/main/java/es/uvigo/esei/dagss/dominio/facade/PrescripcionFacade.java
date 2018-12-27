/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.dominio.facade;

import es.uvigo.esei.dagss.dominio.daos.PrescripcionDAO;
import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Fer
 */
@Stateless
public class PrescripcionFacade {
    
    @Inject
    private PrescripcionDAO prescripcionDAO;
    
    @Inject
    private RecetaDAO recetaDAO;
    
    private final PlanificadorReceta planificadorReceta = new PlanificadorDagss();
    
    public PrescripcionFacade(){
        
    }
    
    public void planificarRecetas(Prescripcion p){
        System.out.println("es.uvigo.esei.dagss.dominio.facade.PrescripcionFacade.planificarRecetas()");
        List<Receta> recetas = planificadorReceta.planificarRecetas(p);
        
        prescripcionDAO.crear(p);
        System.out.println("Recetas length: " + recetas.size());
        
        for(int i = 0; i<recetas.size();i++){
            recetaDAO.crear(recetas.get(i));
        }
    }
    
}
