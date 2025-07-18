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
    public class HealthArticleService : IHealthArticleService
    {
        private readonly IHealthArticleRepository _healthArticleRepository;
        public HealthArticleService(IHealthArticleRepository healthArticleRepository)
        {
            _healthArticleRepository = healthArticleRepository;
        }
        public void deleteById(int id)
        {
           _healthArticleRepository.deleteById(id);
        }

        public (List<HealthArticle>, int totalItem) findAllWithPagination(int pg, int pageSize)
        {
           return _healthArticleRepository.findAllWithPagination(pg, pageSize);
        }

        public HealthArticle FindById(int id)
        {
           return _healthArticleRepository.FindById(id);
        }

        public List<HealthArticle> GetAll()
        {
           return _healthArticleRepository.GetAll();
        }

        public void Save(HealthArticle healthArticle)
        {
             _healthArticleRepository.Save(healthArticle);
        }

        public void Udpate(HealthArticle healthArticle)
        {
          _healthArticleRepository.Udpate(healthArticle);
        }
    }
}
