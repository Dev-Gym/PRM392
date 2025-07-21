using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Service.IService;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service
{
    public class PatientService : IPatientService
    {
        private readonly IPatientRepository _patientRepository;
        private readonly IUserRepository _userRepository;

        public PatientService(IPatientRepository patientRepository, IUserRepository userRepository)
        {
            _patientRepository = patientRepository ?? throw new ArgumentNullException(nameof(patientRepository));
            _userRepository = userRepository ?? throw new ArgumentNullException(nameof(userRepository));
        }

        public Patient getById(int id)
        {
            return _patientRepository.FindById(id);
        }
        public void CreatePatient(Patient patient)
        {
            var user = _userRepository.FindById(patient.PatientId);
            if (user == null)
            {
                throw new Exception("User not found!");
            }

            if (user.UserType != "Patient")
            {
                throw new Exception("Selected user is not a patient!");
            }

            if (user.Patient != null)
            {
                throw new Exception("This user already has a patient record!");
            }

            _patientRepository.Add(patient);

            user.Patient = patient;
            _userRepository.UpdateUser(user);
            Console.WriteLine($"Patient created successfully for UserId: {patient.PatientId}");
        }

        public Patient GetPatientById(int id)
        {
            return _patientRepository.FindById(id) ?? throw new Exception("Patient not found!");
        }

        public IEnumerable<Patient> GetAllPatients()
        {
            return _patientRepository.GetAllPatients();
        }

        public void UpdatePatient(Patient patient)
        {
            _patientRepository.Update(patient);
        }

        public void DeletePatient(int id)
        {
            try
        {
                _patientRepository.Delete(id);
        }
            catch (Exception ex)
        {
                throw new Exception($"Failed to delete patient with ID {id}: {ex.Message}", ex);
            }
        }
    }
}
