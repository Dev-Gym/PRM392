using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface IMedicalHistoryRepository
    {
        List<MedicalHistory> GetAll();
        MedicalHistory GetById(int id);
        MedicalHistory Create(MedicalHistory medicalHistory);
        MedicalHistory Update(MedicalHistory medicalHistory);
        void DeleteById(int id);
        List<MedicalHistory> GetAllByUserId(int userId);
        (List<MedicalHistory> list, int totalItems) GetALlPagainations(int pg, int pageSize);
        (List<MedicalHistory> list, int totalItems) GetALlPagainationsByPatientId(int pg, int pageSize, int patientId);
        (List<MedicalHistory> list, int totalItems) GetALlPagainationsByExpertId(int pg, int pageSize, int expertId);
        MedicalHistory ExistingMedicalHistory(int appointmentId);

    }
}
