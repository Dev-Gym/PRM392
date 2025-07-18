using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface IPatientRepository
    {
        void Add(Patient patient);
        Patient FindById(int id);
        IEnumerable<Patient> GetAllPatients();
        void Update(Patient patient);
        void Delete(int id);
    }
}
