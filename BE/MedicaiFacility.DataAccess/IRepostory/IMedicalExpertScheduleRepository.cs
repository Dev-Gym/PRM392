    using MedicaiFacility.BusinessObject;
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    namespace MedicaiFacility.DataAccess.IRepostory
    {
    public interface IMedicalExpertScheduleRepository
    {
        IEnumerable<MedicalExpertSchedule> GetSchedulesByExpertId(int expertId);
        MedicalExpertSchedule AddMedicalExpertSchedule(MedicalExpertSchedule schedule);
        MedicalExpertSchedule UpdateMedicalExpertSchedule(MedicalExpertSchedule schedule);
        void DeleteSchedulesByExpertId(int expertId);
        IEnumerable<MedicalExpertSchedule> GetAll();
    }
}
