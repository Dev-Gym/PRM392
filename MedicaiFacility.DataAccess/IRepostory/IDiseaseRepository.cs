using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface IDiseaseRepository
    {
        List<Disease> GetAllDisease();
        Disease FindById(int id);
        void AddDisease(Disease disease);
        void UpdateDisease(Disease disease);
        void DeleteDisease(int id);
        (List<Disease>, int totalItem) FindAllWithPagination(int pg, int pageSize);
    }
}
