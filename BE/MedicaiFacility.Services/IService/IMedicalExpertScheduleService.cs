using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service.IService
{
    public interface IMedicalExpertScheduleService
    {
        List<MedicalExpertSchedule> GetSchedulesByExpertId(int expertId);

        string AddMedicalExpertSchedule(MedicalExpertSchedule schedule);
        MedicalExpertSchedule UpdateMedicalExpertSchedule(MedicalExpertSchedule schedule);
        void DeleteSchedulesByExpertId(int expertId);
        string IsValid(int id, DateTime startTime, DateTime EndTime);
    }
}
