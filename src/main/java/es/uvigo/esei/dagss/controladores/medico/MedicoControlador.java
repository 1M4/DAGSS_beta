/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.medico;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.CitaDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicamentoDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicoDAO;
import es.uvigo.esei.dagss.dominio.daos.PrescripcionDAO;
import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.EstadoCita;
import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author ribadas
 */

@Named(value = "medicoControlador")
@SessionScoped
public class MedicoControlador implements Serializable {

    private Medico medicoActual;
    private String dni;
    private String numeroColegiado;
    private String password;
    private List<Cita> listaCitas;
    private Paciente paciente;
    private List<Prescripcion> listaPrescripciones;
    private List<Medicamento> listaMedicamentos;
    private Prescripcion prescripcion;

    

    @Inject
    private AutenticacionControlador autenticacionControlador;
    

    @EJB
    private MedicoDAO medicoDAO;
    
    @Inject
    private CitaDAO citaDAO;

    @Inject
    private PrescripcionDAO prescripcionDAO;
    
    @Inject
    private MedicamentoDAO medicamentoDAO;
    
    /**
     * Creates a new instance of AdministradorControlador
     */
    public MedicoControlador() {
        prescripcion = new Prescripcion();
    }

    public Prescripcion getPrescripcion() {
        return prescripcion;
    }

    public void setPrescripcion(Prescripcion prescripcion) {
        this.prescripcion = prescripcion;
    }
    
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Medico getMedicoActual() {
        return medicoActual;
    }

    public void setMedicoActual(Medico medicoActual) {
        this.medicoActual = medicoActual;
    }

    private boolean parametrosAccesoInvalidos() {
        return (((dni == null) && (numeroColegiado == null)) || (password == null));
    }
    
    public List<Cita> getListaCitas() {
        return listaCitas;
    }

    public void setListaCitas(List<Cita> listaCitas) {
        this.listaCitas = listaCitas;
    }
    
    public List<Cita> doBuscarCitasHoy(){
        listaCitas=null;
        Date today = new Date();
        String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(today);
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            today = format.parse(modifiedDate);
        } catch (ParseException ex) {
            //Logger.getLogger(MedicoControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        listaCitas=citaDAO.buscarCitasHoy(medicoActual,today);
        //return null;
        return listaCitas;
    }
    
    public boolean esPlanificada(Cita cita){
                //System.out.println(">>>>> Cita es planificada "+cita.toString());

        return cita.getEstado().getEtiqueta().equals("PLANIFICADA");
    }
    
    public void doGestionPrescripciones(Cita cita) {
    
        paciente=cita.getPaciente();
        //prescripcion = new Prescripcion();
    }
    
    public List<Prescripcion> doBuscarPrescripcionesPaciente(){
        //prescripcion = new Prescripcion();
        listaPrescripciones = null;
        
        listaPrescripciones= prescripcionDAO.buscarPorPaciente(paciente);
        
        return listaPrescripciones;
    }
    
    public void eliminarPrescripcion(Prescripcion prescripcion){
        prescripcionDAO.eliminar(prescripcion);
    }
    
    public List<Medicamento> doBuscarMedicamentos(){
        listaMedicamentos=null;
        listaMedicamentos=medicamentoDAO.buscarTodos();
        return listaMedicamentos;
    }
    
    public void seleccionarMedicamento(Medicamento medicamento){
        System.out.println("HOLA");
        System.out.println(medicamento);
        //if(prescripcion==null){
          //  prescripcion = new Prescripcion();
        //}
        prescripcion.setMedicamento(medicamento);
    }
    
    public void generarPrescripcion(){
        System.out.println("Prescripcion: " + prescripcion.getMedicamento() + " " + prescripcion.getDosis()+ " " + prescripcion.getIndicaciones()+ " " + prescripcion.getFechaInicioFormateada()+ " " + prescripcion.getFechaFinFormateada());
    }
    
    public void doCompletada(Cita cita){
        //System.out.println(">>>>> Cita "+cita.toString());
        cita.setEstado(EstadoCita.COMPLETADA);
        //cita.setDuracion(520);
        citaDAO.actualizar(cita);
       // listaCitas = doBuscarCitasHoy();
       // return null;
    }
    
    public String doAusente(Cita cita){
        cita.setEstado(EstadoCita.AUSENTE);
        citaDAO.actualizar(cita);
        listaCitas = doBuscarCitasHoy();
        return null;
    }

    private Medico recuperarDatosMedico() {
        Medico medico = null;
        if (dni != null) {
            medico = medicoDAO.buscarPorDNI(dni);
        }
        if ((medico == null) && (numeroColegiado != null)) {
            medico = medicoDAO.buscarPorNumeroColegiado(numeroColegiado);
        }
        return medico;
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un DNI ó un número de colegiado o una contraseña", ""));
        } else {
            Medico medico = recuperarDatosMedico();
            if (medico == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe ningún médico con los datos indicados", ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(medico.getId(), password,
                        TipoUsuario.MEDICO.getEtiqueta().toLowerCase())) {
                    medicoActual = medico;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }
            }
        }
        return destino;
    }

    //Acciones
    public String doShowCita() {
        return "detallesCita";
    }
}
