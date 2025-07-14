using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service.IService
{
    public interface IPatientService
    {
        Patient getById(int id);
        void CreatePatient(Patient patient);
        Patient GetPatientById(int id);
        IEnumerable<Patient> GetAllPatients();
        void UpdatePatient(Patient patient);
        void DeletePatient(int id);
    }
}
