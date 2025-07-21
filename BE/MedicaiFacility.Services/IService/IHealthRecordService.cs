using MedicaiFacility.BusinessObject;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service.IService
{
    public interface IHealthRecordService
    {
        public List<HealthRecord> GetAll();
        public HealthRecord FindById(int id);
        public void Save(HealthRecord healthRecord);
        public void Udpate(HealthRecord healthRecord);
        public void deleteById(int id);
        (List<HealthRecord>, int totalItem) findAllWithPagination(int pg, int pageSize);
        HealthRecord findByMedicalHistoryId(int hisId);


    }
}
