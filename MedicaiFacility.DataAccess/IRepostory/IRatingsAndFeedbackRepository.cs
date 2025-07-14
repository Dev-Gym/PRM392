using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface IRatingsAndFeedbackRepository
    {
        public List<RatingsAndFeedback> GetAll();
        public RatingsAndFeedback FindById(int id);

        public void Add(RatingsAndFeedback ratingsAndFeedback);
        public void Save(RatingsAndFeedback ratingsAndFeedback);
        public void Update(RatingsAndFeedback ratingsAndFeedback);
        public void Delete(int id);
        public RatingsAndFeedback findByHisId(int id);
        List<RatingsAndFeedback> GetAllByExpertId(int expertId);

		(List<RatingsAndFeedback>, int totalItem) FindAllWithPagination(int pg, int pageSize);
    }
}