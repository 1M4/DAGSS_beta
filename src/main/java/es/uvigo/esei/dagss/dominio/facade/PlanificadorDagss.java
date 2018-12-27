/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.dominio.facade;

import es.uvigo.esei.dagss.dominio.entidades.EstadoReceta;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;

/**
 *
 * @author Fer
 */

@Stateless
public class PlanificadorDagss implements PlanificadorReceta {

    public PlanificadorDagss(){
        
    }
    
    @Override
    public List<Receta> planificarRecetas(Prescripcion p) {
        //System.out.println("es.uvigo.esei.dagss.dominio.facade.PlanificadorDagss.planificarRecetas()");
        List<Receta> toret = new ArrayList<>();
        
        long dif = p.getFechaFin().getTime()-p.getFechaInicio().getTime();
        long dias = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS);
                
        int numDosis = (int) (p.getDosis() * dias);
        System.out.println("Num.Dosis: " + numDosis);
        
        int numCajasMedicamento = (int) Math.ceil(numDosis/p.getMedicamento().getNumeroDosis());
        System.out.println("Num.Cajas: " + numCajasMedicamento);
        
        int diasPorCajaMedicamento =  (int) p.getMedicamento().getNumeroDosis() / p.getDosis();
        
        
        Date fechaFin , fechaInicio=p.getFechaInicio();
        
        //Establecer la primera fecha fin
        Calendar c = Calendar.getInstance();
        c.setTime(fechaInicio);
        c.add(Calendar.DATE, diasPorCajaMedicamento+7);  // number of days to add
        fechaFin = c.getTime();
        
        for(int i = 0; i<numCajasMedicamento;i++){
            toret.add(new Receta(p,1,fechaInicio,fechaFin,EstadoReceta.GENERADA));
            
            //Fecha Inicio = Fecha Fin - 7 dias
            fechaInicio=fechaFin;
            
            c.setTime(fechaInicio);
            c.add(Calendar.DATE, -7);  // number of days to add
            fechaInicio = c.getTime();
            
            //Fecha Fin = Fecha Fin + Dias por Caja + 7 dias
            c.setTime(fechaInicio);
            c.add(Calendar.DATE,diasPorCajaMedicamento+7 );
            fechaFin=c.getTime();
            
        }
        
        return toret;
    }
    
}
