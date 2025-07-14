using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess
{
    public class PatientRepository : IPatientRepository
    {
        private readonly AppDbContext _context;

        public PatientRepository(AppDbContext context)
        {
            _context = context ?? throw new ArgumentNullException(nameof(context));
        }

        public void Add(Patient patient)
        {
            _context.Patients.Add(patient);
            _context.SaveChanges();
        }

        public Patient FindById(int id)
        {
            return _context.Patients
                .Include(p => p.PatientNavigation)
                .FirstOrDefault(p => p.PatientId == id);
        }

        public IEnumerable<Patient> GetAllPatients()
        {
            return _context.Patients
                .Include(p => p.PatientNavigation)
                .ToList();
        }

        public void Update(Patient patient)
        {
            var existingPatient = _context.Patients.FirstOrDefault(p => p.PatientId == patient.PatientId);
            if (existingPatient == null)
            {
                throw new Exception("Patient not found!");
            }

            existingPatient.DateOfBirth = patient.DateOfBirth;
            existingPatient.Gender = patient.Gender;
            existingPatient.MedicalHistory = patient.MedicalHistory;

            _context.Entry(existingPatient).State = EntityState.Modified;
            _context.SaveChanges();
        }

        public void Delete(int id)
        {
            using (var transaction = _context.Database.BeginTransaction())
            {
                try
                {
                    var patient = _context.Patients
                        .Include(p => p.PatientNavigation)
                        .FirstOrDefault(p => p.PatientId == id);

                    if (patient == null)
                    {
                        transaction.Rollback(); // Không cần rollback nếu không có thay đổi
                        return; // Hoặc throw new Exception("Patient not found!");
                    }

                    // Cập nhật User.Patient = null
                    if (patient.PatientNavigation != null)
                    {
                        patient.PatientNavigation.Patient = null;
                        _context.Entry(patient.PatientNavigation).State = EntityState.Modified;
        }

                    // Xóa Patient
                    _context.Patients.Remove(patient);
                    _context.SaveChanges();
       
                    transaction.Commit();
                }
                catch (Exception ex)
                {
                    transaction.Rollback();
                    throw new Exception($"Failed to delete patient with ID {id}: {ex.Message}", ex);
                }
            }
        }
    }
}
