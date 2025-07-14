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
    public class HealhRecordService:IHealthRecordService
    {
        private readonly IHealthRecordRepository _healthRecordRepository;
        public HealhRecordService(IHealthRecordRepository healthRecordRepository)
        {
            _healthRecordRepository = healthRecordRepository;
        }

        public void deleteById(int id)
        {
            _healthRecordRepository.deleteById(id);
        }

        public (List<HealthRecord>, int totalItem) findAllWithPagination(int pg, int pageSize) 
            => _healthRecordRepository.findAllWithPagination(pg, pageSize);


		public HealthRecord FindById(int id)
        {
           return _healthRecordRepository.FindById(id);
        }

        public HealthRecord findByMedicalHistoryId(int hisId)
        {
            return _healthRecordRepository.findByMedicalHistoryId(hisId);
        }

        public List<HealthRecord> GetAll()
        {
           return _healthRecordRepository.GetAll();
        }

        public void Save(HealthRecord healthRecord)
        {
          _healthRecordRepository.Save(healthRecord);
        }

        public void Udpate(HealthRecord healthRecord)
        {
            _healthRecordRepository.Udpate(healthRecord);
        }
    }
}
