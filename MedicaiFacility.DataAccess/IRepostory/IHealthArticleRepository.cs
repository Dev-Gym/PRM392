using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface IHealthArticleRepository
    {
        public List<HealthArticle> GetAll();
        public HealthArticle FindById(int id);
        public void Save(HealthArticle healthArticle);
        public void Udpate(HealthArticle healthArticle);
        public void deleteById(int id);
        (List<HealthArticle>, int totalItem) findAllWithPagination(int pg, int pageSize);
    }
}
