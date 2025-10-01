package com.proyectointegral2.Model;

    import java.time.LocalDate;
    import java.time.LocalTime;

    public class RegistroCitaInfo {
        private int idReservaCita;
        private LocalDate fechaCita;
        private LocalTime horaCita;
        private String nombrePerro;
        private String nombreCliente;
        private String estadoCita;

        public int getIdReservaCita() { return idReservaCita; }
        public void setIdReservaCita(int idReservaCita) { this.idReservaCita = idReservaCita; }

        public LocalDate getFechaCita() { return fechaCita; }
        public void setFechaCita(LocalDate fechaCita) { this.fechaCita = fechaCita; }

        public LocalTime getHoraCita() { return horaCita; }
        public void setHoraCita(LocalTime horaCita) { this.horaCita = horaCita; }

        public String getNombrePerro() { return nombrePerro; }
        public void setNombrePerro(String nombrePerro) { this.nombrePerro = nombrePerro; }

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public String getEstadoCita() { return estadoCita; }
        public void setEstadoCita(String estadoCita) { this.estadoCita = estadoCita; }
    }