/*
 Proyecto Java EE, DAGSS-2014
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class RecetaDAO extends GenericoDAO<Receta>{
 
    // Completar aqui
    public List<Receta> buscarPorNumTarjeta(String numTarjeta) {
        TypedQuery<Receta> q = em.createQuery("SELECT r FROM Receta AS r "
                                              + "  WHERE (r.prescripcion.paciente.numeroTarjetaSanitaria = :numTarjeta)", Receta.class);
        q.setParameter("numTarjeta",numTarjeta);        
        return q.getResultList();
    }
    

    
}
