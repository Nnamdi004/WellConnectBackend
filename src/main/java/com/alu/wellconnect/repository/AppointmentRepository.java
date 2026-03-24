package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.Appointment;
import com.alu.wellconnect.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.therapist.therapistId = :therapistId " +
           "AND DATE(a.scheduledTime) = CAST(:date AS date)")
    List<Appointment> findByTherapistIdAndDate(@Param("therapistId") Long therapistId, @Param("date") String date);

    @Query("SELECT a FROM Appointment a WHERE a.therapist.therapistId = :therapistId " +
           "AND DATE(a.scheduledTime) = :date AND a.status IN :statuses")
    List<Appointment> findAppointmentsByTherapistAndDate(
            @Param("therapistId") Long therapistId,
            @Param("date") LocalDate date,
            @Param("statuses") List<AppointmentStatus> statuses);
}
