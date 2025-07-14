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
    public class DiseaseService : IDiseaseService
    {
        private readonly IDiseaseRepository _diseaseRepository;

        public DiseaseService(IDiseaseRepository diseaseRepository)
        {
            _diseaseRepository = diseaseRepository;
        }

        public List<Disease> GetAllDisease()
        {
            return _diseaseRepository.GetAllDisease();
        }
        public Disease FindById(int id)
        {
            return _diseaseRepository.FindById(id);
        }

        public void AddDisease(Disease disease)
        {
            _diseaseRepository.AddDisease(disease);
        }

        public void UpdateDisease(Disease disease)
        {
            _diseaseRepository.UpdateDisease(disease);
        }

        public void DeleteDisease(int id)
        {
            _diseaseRepository.DeleteDisease(id);
        }

        public (List<Disease>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            return _diseaseRepository.FindAllWithPagination(pg, pageSize);
        }
    }
}
