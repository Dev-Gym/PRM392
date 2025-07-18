using MedicaiFacility.BusinessObject;
using System.Collections.Generic;

namespace MedicaiFacility.Service.IService
{
    public interface IMedicalExpertService
    {
        Task<MedicalExpert> GetByIdAsync(int id);
        List<MedicalExpert> GetAllMedicalExpert();
        void UpdateMedicalExpert(MedicalExpert medicalExpert);
        void DeleteMedicalExpert(int id);
        MedicalExpert getById(int id);
        void CreateMedicalExpert(MedicalExpert medicalExpert);
        IEnumerable<MedicalExpert> GetAllMedicalExperts();

        void AddMedicalExpertSchedule(MedicalExpertSchedule schedule);
        void DeleteSchedulesByExpertId(int expertId);
            List<MedicalExpert> SearchDoctors(string searchTerm);
        (List<MedicalExpert>, int totalItem) FindAllWithPagination(int pg, int pageSize);
 
        List<string> GetScheduleByExpertId(int expertId);
        List<RatingsAndFeedback> GetFeedbacksByExpertId(int expertId);
        Task UpdateScheduleAsync(int expertId, List<string> selectedDays);
        List<MedicalExpert> getExpertsByFacilityId(int facilityId);
    }
}