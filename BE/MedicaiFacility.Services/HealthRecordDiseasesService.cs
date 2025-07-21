using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Service.IService;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service
{
    public class HealthRecordDiseasesService : IHealthRecordDiseasesService
    {
        private readonly IHealthRecordDiseasesRepository _healthRecordDiseasesRepository;
        public HealthRecordDiseasesService(IHealthRecordDiseasesRepository healthRecordDiseasesRepository)
        {
            _healthRecordDiseasesRepository = healthRecordDiseasesRepository;
        }

        public void deleteById(int id)
        {
           _healthRecordDiseasesRepository.deleteById(id);
        }
    }
}
